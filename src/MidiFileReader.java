import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MidiFileReader
{
	private ByteFileReader file;

	public MidiFileReader(String fileName) throws MidiException, IOException
	{
		this.file = new ByteFileReader(fileName);
	}// end MidiFileReader - constructor

	public static void main(String[] args)
	{
	}// end main

	@SuppressWarnings("unchecked")
	public void ReadFile(MidiFile midiFile) throws MidiException, UnsupportedEncodingException
	{
		VerifyHeader(midiFile);

		midiFile.trackmode = file.ReadShort();
		midiFile.numEventTracks = file.ReadShort();
		midiFile.quarternote = file.ReadShort();

		MUtil.SetQuarterNote(midiFile.quarternote);

		// TODO: Understand rawtypes
		midiFile.events = new ArrayList[midiFile.numEventTracks]; // I used rawtype, I know...
		midiFile.tracks = new ArrayList<MidiTrack>();
		midiFile.trackPerChannel = false;

		ComputeTracks(midiFile);
		ComputePulsesSong(midiFile);
		VerifyChannels(midiFile);
		CheckStartTimes(midiFile.tracks);
		ReadTimeSignature(midiFile);
		RoundDurations(midiFile);

		file.Empty();
	}// end readFile

	private void VerifyHeader(MidiFile omidiFile) throws MidiException, UnsupportedEncodingException
	{
		long length;

		if (!file.ReadAscii(4).equals("MThd"))
		{
			throw new MidiException("Doesn't start with MThd", 0);
		}// end if

		length = file.ReadInt();
		if (length != 6)
		{
			throw new MidiException("Bad MThd header", 4);
		}// end if
	}// end VerifyHeader

	private void ComputeTracks(MidiFile omidiFile) throws MidiException, UnsupportedEncodingException
	{
		MidiTrack track;
		for (int tracknum = 0; tracknum < omidiFile.numEventTracks; tracknum++)
		{
			// Uncommen this if you want to make sure everything compiles before doing
			// ReadTrackEvents();
			// omidiFile.events[tracknum] = new ArrayList<MidiEvent>();
			omidiFile.events[tracknum] = ReadTrackEvents();
			track = new MidiTrack(omidiFile.events[tracknum], tracknum);
			track.HasNotes = (track.notes.size() > 0);

			if (track.HasNotes)
			{
				omidiFile.tracks.add(track);
			}// end if
		}// end for
	}// end ComputeTracks

	private void ComputePulsesSong(MidiFile omidiFile)
	{
		for (int ev = 0; ev < omidiFile.events.length; ev++)
		{
			for (int e = 0; e < omidiFile.events[ev].size(); e++)
			{
				if (omidiFile.totalPulses < omidiFile.events[ev].get(e).StartTime)
				{
					omidiFile.totalPulses = omidiFile.events[ev].get(e).StartTime;
				}// end if
			}// end for
		}// end for
	}// end ComputePulsesSong

	private void VerifyChannels(MidiFile omidiFile)
	{
		/*
		 * If we only have one track with multiple channels, then treat
		 * each channel as a separate track.
		 */
		if (omidiFile.tracks.size() == 1 && HasMultipleChannels(omidiFile.tracks.get(0)))
		{
			omidiFile.tracks = SplitChannels(omidiFile, omidiFile.events[omidiFile.tracks.get(0).tracknum]);
			omidiFile.trackPerChannel = true;
		}// end if
	}// end VerifyChannels

	/// <summary>
	/// Check that the MidiNote start times are in increasing order.
	/// This is for debugging purposes.
	/// </summary>
	/// <param name="tracks"></param>
	private void CheckStartTimes(List<MidiTrack> tracks) throws MidiException
	{
		MidiTrack track;
		for (int t = 0; t < tracks.size(); t++)
		{
			track = tracks.get(t);
			int      prevtime = -1;
			MidiNote note;
			for (int n = 0; n < track.notes.size(); n++)
			{
				note = track.notes.get(n);
				if (note.startTime < prevtime)
				{
					throw new MidiException("start times not in increasing order", -1);
				}// end if
				prevtime = note.startTime;
			}// end for
		}// end for
	}// end CheckStartTimes

	private void ReadTimeSignature(MidiFile omidiFile) throws MidiException
	{
		/* Determine the time signature */
		int tempo = 0;
		int numer = 0;
		int denom = 0;

		List<MidiEvent> list;
		for (int i = 0; i < omidiFile.events.length; i++)
		{
			list = omidiFile.events[i];
			MidiEvent mevent;
			for (int j = 0; j < list.size(); j++)
			{
				mevent = list.get(j);
				if (mevent.MetaEvent == MUtil.MetaEventTempo && tempo == 0)
				{
					tempo = mevent.Tempo;
				}// end if
				if (mevent.MetaEvent == MUtil.MetaEventTimeSignature && numer == 0)
				{
					numer = mevent.Numerator;
					denom = mevent.Denominator;
				}// end if
			}// end for
		}// end for

		if (tempo == 0)
		{
			tempo = 500000; /* 500,000 microseconds = 0.05 sec */
		}// end if
		if (numer == 0)
		{
			numer = denom = 4;
		}// end if
		omidiFile.timeSig = new TimeSignature(numer, denom, omidiFile.quarternote, tempo);
	}// end ReadTimeSignature

	/*
	 * We want note durations to span up to the next note in general.
	 * The sheet music looks nicer that way. In contrast, sheet music
	 * with lots of 16th/32nd notes separated by small rests doesn't
	 * look as nice. Having nice looking sheet music is more important
	 * than faithfully representing the Midi File data.
	 *
	 * Therefore, this function rounds the duration of MidiNotes up to
	 * the next note where possible.
	 */
	public static void RoundDurations(MidiFile midiFile)
	{
		List<MidiTrack> tracks;
		int             quarterNote;

		tracks = midiFile.tracks;
		quarterNote = midiFile.quarternote;
		for (MidiTrack track : tracks)
		{
			MidiNote prevNote = null;
			for (int i = 0; i < track.notes.size() - 1; i++)
			{
				MidiNote note1 = track.notes.get(i);
				if (prevNote == null)
				{
					prevNote = note1;
				}// end if

				/* Get the next note that has a different start time */
				MidiNote note2 = note1;
				for (int j = i + 1; j < track.notes.size(); j++)
				{
					note2 = track.notes.get(j);
					if (note1.startTime < note2.startTime)
					{
						break;
					}// end if
				}// end for
				int maxduration = note2.startTime - note1.startTime;

				int dur = 0;
				if (quarterNote <= maxduration)
					dur = quarterNote;
				else if (quarterNote / 2 <= maxduration)
					dur = quarterNote / 2;
				else if (quarterNote / 3 <= maxduration)
					dur = quarterNote / 3;
				else if (quarterNote / 4 <= maxduration)
					dur = quarterNote / 4;

				if (dur < note1.length)
				{
					dur = note1.length;
				}// end if

				/*
				 * Special case: If the previous note's duration
				 * matches this note's duration, we can make a notepair.
				 * So don't expand the duration in that case.
				 */
				if ((prevNote.startTime + prevNote.length == note1.startTime) && (prevNote.length == note1.length))
				{
					dur = note1.length;
				}// end if
				note1.length = dur;

				if (track.notes.get(i + 1).startTime != note1.startTime)
				{
					prevNote = note1;
				}// end if
			}// end for
		}// end for
	}// end RoundDuration

	/// <summary>
	/// Parse a single Midi track into a list of MidiEvents.
	/// Entering this function, the file offset should be at the start of the MTrk
	/// header.
	/// Upon exiting, the file offset should be at the
	/// start of the next MTrk header.
	/// </summary>
	/// <returns></returns>
	// TODO: Finish this function just like you are doing...
	private List<MidiEvent> ReadTrackEvents() throws MidiException, UnsupportedEncodingException
	{
		List<MidiEvent> result    = new ArrayList<MidiEvent>();
		int             starttime = 0;
		int             eventflag;
		long            tracklength;
		long            trackend;
		String          id        = file.ReadAscii(4);

		if (!id.equals("MTrk"))
		{
			throw new MidiException("Bad MTrk header", file.GetOffset() - 4);
		}// end if
		tracklength = file.ReadInt();
		trackend = tracklength + file.GetOffset();

		eventflag = 0;

		while (file.GetOffset() < trackend)
		{
			// If the midi file is truncated here, we can still recover.
			// Just return what we've parsed so far.
			int       startoffset, deltatime, channel, peekevent;
			MidiEvent mEvent;
			try
			{
				startoffset = file.GetOffset();
				deltatime = file.ReadVarlen();
				starttime += deltatime;
				peekevent = file.Peek();
			}
			catch (MidiException e)
			{
				return result;
			}// end try-catch

			mEvent = new MidiEvent();
			mEvent.DeltaTime = deltatime;
			mEvent.StartTime = starttime;
			result.add(mEvent);

			if (peekevent >= MUtil.EventNoteOff)
			{
				mEvent.HasEventflag = true;
				eventflag = file.ReadByte();
			}// end if
			channel = 0;
			if (eventflag < Byte.MAX_VALUE)
				channel = eventflag % 16;

			mEvent.EventFlag = (eventflag - channel);
			// TODO: Remove this TESTS
			mEvent.EventFlag = MUtil.EventNoteOn;
			System.out.println(MUtil.EventNoteOn);
			System.out.println(mEvent.EventFlag);

			switch (mEvent.EventFlag)
			{
				case MUtil.EventNoteOn:
					// TODO: Remove this test
					System.out.println("QUIOVOLE PUTO!!!!");
					mEvent.Channel = channel;
					mEvent.Notenumber = file.ReadByte();
					mEvent.Volume = file.ReadByte();
					if (mEvent.Volume > 0)
					{
						mEvent.text = "ON  Ch: " + channel + " key: " + mEvent.Notenumber + " vel: " + mEvent.Volume;
						mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					}
					else
					{
						mEvent.EventFlag = 128;
						mEvent.text = "OFF Ch: " + channel + " key: " + mEvent.Notenumber + " vel: " + mEvent.Volume;
						mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					}// end if-else
					break;

				case MUtil.EventNoteOff:
					mEvent.Channel = channel;
					mEvent.Notenumber = file.ReadByte();
					mEvent.Volume = file.ReadByte();
					mEvent.text = "OFF Ch: " + channel + " key: " + mEvent.Notenumber + " vel: " + mEvent.Volume;
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				case MUtil.EventChannelPressure: // aka ChannelAfterTouch
					mEvent.Channel = channel;
					mEvent.ChanPressure = file.ReadByte();
					mEvent.text = MUtil.EVENT_TYPE.get(mEvent.EventFlag) + " Ch: " + channel + " pressure: " + mEvent.ChanPressure;
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				case MUtil.EventControlChange:
					mEvent.Channel = channel;
					mEvent.ControlNum = file.ReadByte();
					mEvent.ControlValue = file.ReadByte();
					mEvent.text = "CC  Ch: " + channel + " C: " + MUtil.CC.get(mEvent.ControlNum) + " value: " + mEvent.ControlValue;
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				case MUtil.EventKeyPressure:
					mEvent.Channel = channel;
					mEvent.Notenumber = file.ReadByte();
					mEvent.KeyPressure = file.ReadByte();
					mEvent.text = MUtil.EVENT_TYPE.get(mEvent.EventFlag) + " Ch: " + channel + " note: " + mEvent.Notenumber + " pressure: " + mEvent.KeyPressure;
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				case MUtil.EventPitchBend:
					mEvent.Channel = channel;
					mEvent.PitchBend = file.ReadShort();
					mEvent.text = MUtil.EVENT_TYPE.get(mEvent.EventFlag) + " Ch: " + channel + " PitchBend: " + mEvent.PitchBend;
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				case MUtil.EventProgramChange:
					mEvent.Channel = channel;
					mEvent.Instrument = file.ReadByte();
					mEvent.text = "PC  Ch: " + channel + " : " + MUtil.INSTRUMENT.get(mEvent.Instrument);
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				case MUtil.SysexEvent1:
					mEvent.MetaLength = file.ReadVarlen();
					mEvent.Value = file.ReadBytes(mEvent.MetaLength);
					String val = "";
					for (int i = 0; i < mEvent.Value.length; i++)
					{
						val += Byte.toUnsignedInt(mEvent.Value[i]) + " ";
					}// end for
					mEvent.text = MUtil.EVENT_TYPE.get(mEvent.EventFlag) + " length: " + mEvent.MetaLength + " value: " + val;
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				case MUtil.SysexEvent2:
					mEvent.MetaLength = file.ReadVarlen();
					mEvent.Value = file.ReadBytes(mEvent.MetaLength);
					// TODO: should mEvent.Value be interpreted for it's bytes?
					mEvent.text = MUtil.EVENT_TYPE.get(mEvent.EventFlag) + " length: " + mEvent.MetaLength + " value: " + mEvent.Value;
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;
				case MUtil.MetaEvent:
					DefineMetaEvent(mEvent);
					mEvent.type = MUtil.EVENT_TYPE.get(mEvent.EventFlag);
					break;

				default:
					throw new MidiException("Unknown event " + mEvent.EventFlag, file.GetOffset() - 1);
			}// end switch
		}// end while

		return result;
	}// end ReadTrackEvents

	/*
	 * Return true if this track contains multiple channels.
	 * If a MidiFile contains only one track, and it has multiple channels,
	 * then we treat each channel as a separate track.
	 */
	private boolean HasMultipleChannels(MidiTrack track)
	{
		int channel = track.notes.get(0).channel;

		MidiNote _note;
		for (int n = 0; n < track.notes.size(); n++)
		{
			_note = track.notes.get(n);
			if (_note.channel != channel)
			{
				return true;
			}// end if
		}// end for

		return false;
	}// end HasMultipleChannels

	/**
	 * Split the given track into multiple tracks, separating each
	 * channel into a separate track.
	 */
	private List<MidiTrack> SplitChannels(MidiFile file, List<MidiEvent> events)
	{
		MidiTrack origtrack = file.tracks.get(0);
		/* Find the instrument used for each channel */
		int[] channelInstruments = new int[16];

		MidiEvent m_event;
		for (int e = 0; e < events.size(); e++)
		{
			m_event = events.get(e);
			if (m_event.EventFlag == MUtil.EventProgramChange)
			{
				channelInstruments[m_event.Channel] = m_event.Instrument;
			}// end if
		}// end for

		channelInstruments[9] = 128; /* Channel 9 = Percussion */

		List<MidiTrack> result = new ArrayList<MidiTrack>();
		MidiNote        note;
		for (int n = 0; n < origtrack.notes.size(); n++)
		{
			boolean foundchannel = false;
			note = origtrack.notes.get(n);

			MidiTrack track;
			for (int t = 0; t < result.size(); t++)
			{
				track = result.get(t);
				if (note.channel == track.notes.get(0).channel)
				{
					foundchannel = true;
					track.AddNote(note);
				}// end if
			}// end for

			if (!foundchannel)
			{
				track = new MidiTrack(result.size() + 1, MUtil.QuarterNote);
				track.AddNote(note);
				track.instrument = channelInstruments[note.channel];
				track.HasNotes = true;
				result.add(track);
				// file.NumberOfMeasures++;
			}// end if
		}// end for

		return result;
	}// end SplitChannels

	private void DefineMetaEvent(MidiEvent mEvent) throws MidiException, UnsupportedEncodingException
	{
		String result;

		mEvent.EventFlag = MUtil.MetaEvent;
		mEvent.MetaEvent = file.ReadByte();
		mEvent.MetaLength = file.ReadVarlen();
		mEvent.Value = file.ReadBytes(mEvent.MetaLength);
		mEvent.text = "ME ";// "" + EVENT_TYPE.MetaEvent; //+" " + mEvent.MetaEvent;
		mEvent.meta = MUtil.META_EVENT.get(mEvent.MetaEvent);
		result = "";

		switch (mEvent.MetaEvent)
		{
			case MUtil.MetaEventCopyright:
				result = " Copyright " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventCuePoint:
				result = " CuePoint " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventDeviceName:
				result = " DeviceName " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventEndOfTrack:
				result = " End of track ";
				break;
			case MUtil.MetaEventInstrument:
				result = " Instrument " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventKeySignature:
				result += " KeySignature : " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventLyric:
				result = " Lyric " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventMarker:
				result = " Marker " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventMIDIChannelPrefix:
				int channel = Byte.toUnsignedInt(mEvent.Value[0]);
				result = " MIDIChannelPrefix: " + channel;
				break;
			case MUtil.MetaEventMIDIPort:
				int port = Byte.toUnsignedInt(mEvent.Value[0]);
				result = " MIDIPort: " + port;
				break;
			case MUtil.MetaEventProgramName:
				result = "  ProgramName " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventSequence:
				result = " Sequence " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventSequenceName:
				result = " SequenceName " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventSequencerSpecificEvent:
				result = " SequencerSpecific " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventSMPTEOffset:
				result = " SMPTEOffset " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventTempo:
				result = SetMetaEventTempo(mEvent);
				break;
			case MUtil.MetaEventText:
				result = " Text " + new String(mEvent.Value, "UTF-8");
				break;
			case MUtil.MetaEventTimeSignature:
				result = " TimeSignature " + SetMetaEventTimeSignature(mEvent);
				break;

		}// end switch

		mEvent.text += result;
	}// end DefineMetaEvent

	private String SetMetaEventTempo(MidiEvent mEvent) throws MidiException
	{
		if (mEvent.MetaLength != 3)
		{
			throw new MidiException("ME Tempo len == " + mEvent.MetaLength + " != 3", file.GetOffset());
		}// end if

		int value0 = Byte.toUnsignedInt(mEvent.Value[0]);
		int value1 = Byte.toUnsignedInt(mEvent.Value[1]);
		int value2 = Byte.toUnsignedInt(mEvent.Value[2]);
		mEvent.Tempo = ((value0 << 16) | (value1 << 8) | value2);

		return " Tempo: " + mEvent.Tempo;
	}// end SetMetaEventTempo

	private String SetMetaEventTimeSignature(MidiEvent mEvent)
	{
		if (mEvent.MetaLength < 2)
		{
			mEvent.Numerator = 0;
			mEvent.Denominator = 4;
		}
		else if (mEvent.MetaLength >= 2 && mEvent.MetaLength < 4)
		{
			mEvent.Numerator = Byte.toUnsignedInt(mEvent.Value[0]);
			mEvent.Denominator = (int) Math.pow(2, Byte.toUnsignedInt(mEvent.Value[1]));
		}
		else
		{
			mEvent.Numerator = Byte.toUnsignedInt(mEvent.Value[0]);
			mEvent.Denominator = (int) Math.pow(2, Byte.toUnsignedInt(mEvent.Value[1]));
		}// end if-else x 2

		return mEvent.Numerator + " / " + mEvent.Denominator;
	}// end SetMetaEventTimeSignature
}// end MidiFileReader