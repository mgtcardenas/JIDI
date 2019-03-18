import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MidiFileReader
{
	private ByteFileReader file;

	public MidiFileReader(String fileName) throws MidiException, IOException
	{
		this.file = new ByteFileReader(fileName);
	}// end MidiFileReader - constructor

	public void readFile(MidiFile midiFile) throws MidiException
	{
		verifyHeader  (        );

		midiFile.setTrackMode      (file.readShort()          );
		midiFile.setNumEventTracks(file.readShort());
		midiFile.setQuarterNote(file.readShort());

		MUtil.setQuarterNote(midiFile.getQuarterNote());

		midiFile.setEvents(new ArrayList[midiFile.getNumEventTracks()]);
		midiFile.setTracks         (new ArrayList<MidiTrack>());
		midiFile.setTrackPerChannel(false                     );

		computeTracks    (midiFile);
		computePulsesSong(midiFile);
		verifyChannels(midiFile);
		checkStartTimes(midiFile.getTracks());
		readTimeSignature(midiFile);
		//		roundDurations(midiFile); is commented in the resources

		file.empty();
	}// end readFile

	/**
	 * Makes sure that the header begins with "MThd" and that length of header is 6
	 *
	 * @throws MidiException - if any of the two conditions above are false
	 */
	private void verifyHeader() throws MidiException
	{
		long length;

		if (!file.readAscii(4).equals("MThd"))
		    throw new MidiException("Doesn't start with MThd", 0);

		length = file.readInt();

		if (length != 6)
		    throw new MidiException("Bad MThd header length", 4);
	}// end verifyHeader

	//region Regarding Tracks

	/**
	 * For each of the event tracks (MTrk) that this file says it has,
	 *
	 * @param omidiFile
	 *
	 * @throws MidiException
	 */
	private void computeTracks(MidiFile omidiFile) throws MidiException
	{
		MidiTrack track;
		for (int tracknum = 0; tracknum < omidiFile.getNumEventTracks(); tracknum++)
		{
			omidiFile.getEvents()[tracknum] = readTrackEvents();
			track                           = new MidiTrack(omidiFile.getEvents()[tracknum], tracknum);

			track.setHasNotes((track.getNotes().size() > 0));
			if (track.hasNotes())
			    omidiFile.getTracks().add(track);
		}//end for - tracknum
	}// end computeTracks

	/**
	 * Parse a single Midi track into a list of MidiEvents
	 *
	 * Entering this function, the file offset should be
	 * at the start of the MTrk header.
	 *
	 * Upon exiting, the file offset should be at the
	 * start of the next MTrk header.
	 */
	private List<MidiEvent> readTrackEvents() throws MidiException
	{
		List<MidiEvent> result;
		int             eventFlag, startTime, trackLength, trackEnd;

		result    = new ArrayList<MidiEvent>();
		startTime = 0;

		if (!file.readAscii(4).equals("MTrk"))
		    throw new MidiException("Bad MTrk header", file.getOffset() - 4);

		trackLength = file.readInt();
		trackEnd    = trackLength + file.getOffset();
		eventFlag   = 0;

		while (file.getOffset() < trackEnd)
		{
			// If the midi file is truncated here, we can still recover. Just return what we've parsed so far.
			int       startOffset, deltaTIme, channel, peekEvent;
			MidiEvent mEvent;

			try
			{
				startOffset =  file.getOffset();
				deltaTIme   =  file.readVarlen();
				startTime   += deltaTIme;
				peekEvent   =  file.peek();
			}
			catch (MidiException e)
			{
				return result;
			}//end try - catch

			mEvent = new MidiEvent();
			mEvent.setDeltaTime(deltaTIme);
			mEvent.setStartTime(startTime);
			result.add(mEvent);

			if (peekEvent >= MUtil.EventNoteOff)
			{
				mEvent.setHasEventflag(true);
				eventFlag = file.readByte();
			}//end if

			channel = 0;

			if (eventFlag < MUtil.UBYTE_MAX_VALUE)
			    channel = eventFlag % 16;

			mEvent.setEventFlag(eventFlag - channel);
			switch (mEvent.getEventFlag())
			{
				case MUtil.EventNoteOn:
					mEvent.setChannel(channel);
					mEvent.setNoteNumber(file.readByte());
					mEvent.setVolume(file.readByte());
					if (mEvent.getVolume() > 0)
					{
						mEvent.setText("ON Ch: " + channel + " key: " + mEvent.getNoteNumber() + " vel: " + mEvent.getVolume());
						mEvent.setType("EventNoteOn"                                                                          );
					}
					else
					{
						mEvent.setEventFlag(0x80);
						mEvent.setText("OFF Ch: " + channel + " key: " + mEvent.getNoteNumber() + " vel: " + mEvent.getVolume());
						mEvent.setType("EventNoteOff"                                                                          );
					}//end if - else
					break;

				case MUtil.EventNoteOff:
					mEvent.setChannel(channel);
					mEvent.setNoteNumber(file.readByte());
					mEvent.setVolume(file.readByte());
					mEvent.setText("OFF Ch: " + channel + " key: " + mEvent.getNoteNumber() + " vel: " + mEvent.getVolume());
					mEvent.setType("EventNoteOff"                                                                          );
					break;

				//TODO: Verify that this works
				case MUtil.EventChannelPressure: // aka ChannelAfterTouch
					mEvent.setChannel     (channel        );
					mEvent.setChanPressure(file.readByte());
					mEvent.setText("ChannelAfterTouch Ch: " + channel + " pressure: " + mEvent.getChanPressure());
					mEvent.setType("EventChannelPressure"                                                       );
					break;

				//TODO: Verify that this works
				case MUtil.EventControlChange:
					mEvent.setChannel     (channel        );
					mEvent.setControlNum  (file.readByte());
					mEvent.setControlValue(file.readByte());
					mEvent.setText("CC Ch: " + channel + " C: " + MUtil.CC.get(mEvent.getControlNum()) + " value: " + mEvent.getControlValue());
					mEvent.setType("EventControlChange"                                                                                       );
					break;

				//TODO: Verify that this works
				case MUtil.EventKeyPressure:
					mEvent.setChannel(channel);
					mEvent.setNoteNumber(file.readByte());
					mEvent.setKeyPressure(file.readByte());
					mEvent.setText("EventKeyPressure Ch: " + channel + " note: " + mEvent.getNoteNumber() + " pressure: " + mEvent.getKeyPressure());
					mEvent.setType("EventKeyPressure"                                                                                              );
					break;

				//TODO: Verify that this works
				case MUtil.EventPitchBend:
					mEvent.setChannel(channel);
					mEvent.setPitchBend(file.readShort());
					mEvent.setText("EventPitchBend Ch: " + channel + " PitchBend: " + mEvent.getPitchBend());
					mEvent.setType("EventPitchBend"                                                        );
					break;

				case MUtil.EventProgramChange:
					mEvent.setChannel(channel);
					mEvent.setInstrument(file.readByte());
					mEvent.setText("PC Ch: " + channel + " : " + MUtil.INSTRUMENT_NAME.get(mEvent.getInstrument()));
					mEvent.setType("EventProgramChange"                                                           );
					break;

				//TODO: Verify that this works
				case MUtil.SysexEvent1:
					mEvent.setMetaLength(file.readVarlen());
					mEvent.setValue(file.readBytes(mEvent.getMetaLength()));
					String val = "";
					for (int i = 0; i < mEvent.getValue().length; i++)
						val += mEvent.getValue()[i] + " ";

					mEvent.setText("SysexEvent1 length: " + mEvent.getMetaLength() + " value: " + val);
					mEvent.setType("SysexEvent1"                                                     );
					break;

				//TODO: Verify that this works
				case MUtil.SysexEvent2:
					mEvent.setMetaLength(file.readVarlen());
					mEvent.setValue(file.readBytes(mEvent.getMetaLength()));
					mEvent.setText("SysexEvent2 length: " + mEvent.getMetaLength() + " value: " + mEvent.getValue());
					mEvent.setType("SysexEvent2"                                                                   );
					break;

				case MUtil.MetaEvent:
					defineMetaEvent(mEvent);
					mEvent.setType("MetaEvent");
					break;
				default:
					throw new MidiException("Unknown event " + mEvent.getEventFlag(), file.getOffset() - 1);
			}//end switch mEvent.getEventFlag()
		}//end while

		return result;
	}// end readTrackEvents

	private void defineMetaEvent(MidiEvent mEvent) throws MidiException
	{
		String result;

		mEvent.setEventFlag(MUtil.MetaEvent);
		mEvent.setMetaEvent (file.readByte  ()                         );
		mEvent.setMetaLength(file.readVarlen()                         );
		mEvent.setValue(file.readBytes(mEvent.getMetaLength()));
		mEvent.setText("ME ");                                            //"" + EVENT_TYPE.MetaEvent; //+" " + mEvent.MetaEvent;
		mEvent.setMeta      (MUtil.META_NAME.get(mEvent.getMetaEvent()));
		result = "";

		switch (mEvent.getMetaEvent())
		{
			//TODO: Verify that this works
			case MUtil.MetaEventCopyright:
				result = " Copyright " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventCuePoint:
				result = " CuePoint " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventDeviceName:
				result = " DeviceName " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			case MUtil.MetaEventEndOfTrack:
				result = " End of track ";
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventInstrument:
				result = " Instrument " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventKeySignature:
				result = " KeySignature " + MUtil.translateKeySignature(mEvent.getValue());
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventLyric:
				result = " Lyric " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventMarker:
				result = " Marker " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventMIDIChannelPrefix:
				int channel = mEvent.getValue()[0];
				result = " MIDIChannelPrefix " + channel;
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventMIDIPort:
				int port = mEvent.getValue()[0];
				result = " MIDIPort " + port;
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventProgramName:
				result = " ProgramName " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventSequence:
				result = " Sequence " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			case MUtil.MetaEventSequenceName:
				result = " SequenceName " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventSequencerSpecificEvent:
				result = " SequencerSpecific " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventSMPTEOffset:
				result = " SMPTEOffset " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			case MUtil.MetaEventTempo:
				result = setMetaEventTempo(mEvent);
				break;

			//TODO: Verify that this works
			case MUtil.MetaEventText:
				result = " Text " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
				break;

			case MUtil.MetaEventTimeSignature:
				result = " TimeSignature " + setMetaEventTimeSignature(mEvent);
				break;
		}//end switch mEvent.getMetaEvent()

		mEvent.setText(mEvent.getText() + result);
	}// end defineMetaEvent

	private String setMetaEventTimeSignature(MidiEvent mEvent)
	{
		if (mEvent.getMetaLength() < 2)
		{
			mEvent.setNumerator(0);
			mEvent.setDenominator(4);
		}
		else if (mEvent.getMetaLength() >= 2 && mEvent.getMetaLength() < 4)
		{
			mEvent.setNumerator(mEvent.getValue()[0]);
			mEvent.setDenominator((int) Math.pow(2, mEvent.getValue()[1]));
		}
		else
		{
			mEvent.setNumerator(mEvent.getValue()[0]);
			mEvent.setDenominator((int) Math.pow(2, mEvent.getValue()[1]));
		}

		return mEvent.getNumerator() + " / " + mEvent.getDenominator();
	}// end setMetaEventTimeSignature

	private String setMetaEventTempo(MidiEvent mEvent) throws MidiException
	{
		int firstByte, secondByte, thirdByte, tempo;

		if (mEvent.getMetaLength() != 3)
		    throw new MidiException("ME Tempo len == " + mEvent.getMetaLength() + " != 3", file.getOffset());

		firstByte  = Byte.toUnsignedInt(mEvent.getValue()[0]);
		secondByte = Byte.toUnsignedInt(mEvent.getValue()[1]);
		thirdByte  = Byte.toUnsignedInt(mEvent.getValue()[2]);

		tempo      = (firstByte << 16 | secondByte << 8 | thirdByte);

		mEvent.setTempo(tempo);

		return " Tempo " + mEvent.getTempo();
	}// end setMetaEventTempo
	//endregion Regarding Tracks

	//TODO: Verify that this works
	private void computePulsesSong(MidiFile omidiFile)
	{
		for (int ev = 0; ev < omidiFile.getEvents().length; ev++)
			for (int e = 0; e < omidiFile.getEvents()[ev].size(); e++)
				if (omidiFile.getTotalPulses() < omidiFile.getEvents()[ev].get(e).getStartTime())
					omidiFile.setTotalPulses(omidiFile.getEvents()[ev].get(e).getStartTime());
	}// end computePulsesSong

	//region Regarding Channels
	//TODO: Verify that this works
	private void verifyChannels(MidiFile omidiFile)
	{
		// If we only have one track with multiple channels, then treat each channel as a separate track.
		if (omidiFile.getTracks().size() == 1 && hasMultipleChannels(omidiFile.getTracks().get(0)))
		{
			omidiFile.setTracks         (splitChannels(omidiFile, omidiFile.getEvents()[omidiFile.getTracks().get(0).getTracknum()]));
			omidiFile.setTrackPerChannel(true                                                                                       );
		}//end if
	}// end verifyChannels

	//TODO: Verify that this works

	/**
	 * Return true if this track contains multiple channels.
	 * If a MidiFile contains only one track, and it has multiple channels,
	 * then we treat each channel as a separate track.
	 */
	private boolean hasMultipleChannels(MidiTrack track)
	{
		int      channel = track.getNotes().get(0).getChannel();

		MidiNote note;
		for (int n = 0; n < track.getNotes().size(); n++)
		{
			note = track.getNotes().get(n);
			if (note.getChannel() != channel)
			    return true;
		}//end for - n

		return false;
	}// end hasMultipleChannels

	//TODO: Verify that this works

	/**
	 * Split the given track into multiple tracks, separating each
	 * channel into a separate track.
	 */
	private List<MidiTrack> splitChannels(MidiFile file, List<MidiEvent> events)
	{
		MidiTrack origtrack          = file.getTracks().get(0);
		int[]     channelInstruments = new int[16];             // Find the instrument used for each channel

		MidiEvent m_event;
		for (int e = 0; e < events.size(); e++)
		{
			m_event = events.get(e);
			if (m_event.getEventFlag() == MUtil.EventProgramChange)
			    channelInstruments[m_event.getChannel()] = m_event.getInstrument();
		}//end for - e

		channelInstruments[9] = 128; // Channel 9 = Percussion

		List<MidiTrack> result = new ArrayList<>();

		MidiNote        note;
		for (int n = 0; n < origtrack.getNotes().size(); n++)
		{
			boolean foundchannel = false;
			note = origtrack.getNotes().get(n);

			MidiTrack track;
			for (int t = 0; t < result.size(); t++)
			{
				track = result.get(t);
				if (note.getChannel() == track.getNotes().get(0).getChannel())
				{
					foundchannel = true;
					track.addNote(note);
				}//end if
			}//end for - t

			if (!foundchannel)
			{
				track = new MidiTrack(result.size() + 1, MUtil.QuarterNote);
				track.addNote(note);
				track.setInstrument(channelInstruments[note.getChannel()]);
				track.setHasNotes(true);
				result.add(track);
				//file.NumberOfMeasures++;
			}//end if
		}//end for - n

		return result;
	}// end splitChannels
	//endregion Regarding Channels

	//TODO: Verify that this works

	/**
	 * Check that the MidiNote start times are in increasing order.
	 * This is for debugging purposes.
	 */
	private void checkStartTimes(List<MidiTrack> tracks) throws MidiException
	{
		MidiTrack track;
		for (int t = 0; t < tracks.size(); t++)
		{
			track = tracks.get(t);
			int      prevtime = -1;
			MidiNote note;
			for (int n = 0; n < track.getNotes().size(); n++)
			{
				note = track.getNotes().get(n);
				if (note.getStartTime() < prevtime)
				    throw new MidiException("start times not in increasing order", -1);

				prevtime = note.getStartTime();
			}//end for - n
		}//end for - t
	}// end checkStartTimes

	//TODO: Verify that this works
	private void readTimeSignature(MidiFile omidiFile) throws MidiException
	{
		// Determine the time signature
		int             tempo = 0;
		int             numer = 0;
		int             denom = 0;

		List<MidiEvent> list;
		for (int i = 0; i < omidiFile.getEvents().length; i++)
		{
			list = omidiFile.getEvents()[i];
			MidiEvent mevent;
			for (int j = 0; j < list.size(); j++)
			{
				mevent = list.get(j);
				if (mevent.getMetaEvent() == MUtil.MetaEventTempo         && tempo == 0)
				    tempo = mevent.getTempo();
				if (mevent.getMetaEvent() == MUtil.MetaEventTimeSignature && numer == 0)
				{
					numer = mevent.getNumerator();
					denom = mevent.getDenominator();
				}//end if
			}//end for - j
		}//end for - i

		if (tempo == 0)
		    tempo = 500000;    /* 500,000 microseconds = 0.05 sec */
		if (numer == 0)
		    numer = denom = 4;

		omidiFile.setTimeSig(new TimeSignature(numer, denom, omidiFile.getQuarterNote(), tempo));
	}// end readTimeSignature

	//TODO: Verify that this works

	/**
	 * We want note durations to span up to the next note in general.
	 * The sheet music looks nicer that way. In contrast, sheet music
	 * with lots of 16th/32nd notes separated by small rests doesn't
	 * look as nice. Having nice looking sheet music is more important
	 * than faithfully representing the Midi File data.
	 *
	 * Therefore, this function rounds the duration of MidiNotes up to
	 * the next note where possible.
	 */
	public static void roundDurations(MidiFile midiFile)
	{
		List<MidiTrack> tracks;
		int             quarterNote;

		tracks      = midiFile.getTracks();
		quarterNote = midiFile.getQuarterNote();
		for (MidiTrack track : tracks)
		{
			MidiNote prevNote = null;
			for (int i = 0; i < track.getNotes().size() - 1; i++)
			{
				MidiNote note1 = track.getNotes().get(i);
				if (prevNote == null)
				    prevNote = note1;

				// Get the next note that has a different start time
				MidiNote note2 = note1;
				for (int j = i + 1; j < track.getNotes().size(); j++)
				{
					note2 = track.getNotes().get(j);
					if (note1.getStartTime() < note2.getStartTime())
					    break;
				}//end for
				int maxduration = note2.getStartTime() - note1.getStartTime();

				int dur         = 0;
				if      (quarterNote     <= maxduration)
				    dur = quarterNote;
				else if (quarterNote / 2 <= maxduration)
				    dur = quarterNote / 2;
				else if (quarterNote / 3 <= maxduration)
				    dur = quarterNote / 3;
				else if (quarterNote / 4 <= maxduration)
				    dur = quarterNote / 4;

				if      (dur < note1.getLength()       )
				    dur = note1.getLength();

				/* Special case: If the previous note's duration
				 * matches this note's duration, we can make a notepair.
				 * So don't expand the duration in that case.
				 */
				if ((prevNote.getStartTime() + prevNote.getLength() == note1.getStartTime()) && (prevNote.getLength() == note1.getLength()))
				    dur = note1.getLength();

				note1.setLength(dur);
				if (track.getNotes().get(i + 1).getStartTime() != note1.getStartTime())
				    prevNote = note1;
			}//end for - i
		}//end foreach
	}// end RoundDuration
}//end MidiFileReader - class