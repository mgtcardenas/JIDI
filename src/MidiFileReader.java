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
		verifyHeader();

		midiFile.setTrackMode      (file.readShort()          );
		midiFile.setNumEventTracks(file.readShort());
		midiFile.setQuarterNote(file.readShort());

		MUtil.setQuarterNote(midiFile.getQuarterNote());

		midiFile.setEvents(new ArrayList[midiFile.getNumEventTracks()]);
		midiFile.setTracks         (new ArrayList<MidiTrack>());
		midiFile.setTrackPerChannel(false                     );

		computeTracks(midiFile);
		//		computePulsesSong(midiFile);
		//		verifyChannels(midiFile);
		//		checkStartTimes(midiFile.getTracks());
		//		readTimeSignature(midiFile);
		//		roundDurations(midiFile);

		file.empty();
	}// end readFile

	/**
	 * Makes sure that the header begins with "MThd" and that length of header is 6
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

	/**
	 * For each of the event tracks (MTrk) that this file says it has,
	 * @param omidiFile
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
			//TODO: Fill the rest of the cases
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

				case MUtil.EventProgramChange:
					mEvent.setChannel(channel);
					mEvent.setInstrument(file.readByte());
					mEvent.setText("PC Ch: " + channel + " : " + MUtil.INSTRUMENT_NAME.get(mEvent.getInstrument()));
					mEvent.setType("EventProgramChange"                                                           );
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

		//TODO: Fill the rest of the cases
		switch (mEvent.getMetaEvent())
		{
			case MUtil.MetaEventTimeSignature:
				result = " TimeSignature " + setMetaEventTimeSignature(mEvent);
				break;

			case MUtil.MetaEventTempo:
				result = setMetaEventTempo(mEvent);
				break;

			case MUtil.MetaEventEndOfTrack:
				result = " End of track ";
				break;

			case MUtil.MetaEventSequenceName:
				result = " SequenceName " + new String(mEvent.getValue(), StandardCharsets.UTF_8);
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

	private void computePulsesSong(MidiFile omidiFile)
	{

	}// end computePulsesSong

	private void verifyChannels(MidiFile omidiFile)
	{

	}// end verifyChannels

	/**
	 * Check that the MidiNote start times are in increasing order.
	 * This is for debugging purposes.
	 */
	private void checkStartTimes(List<MidiTrack> tracks) throws MidiException
	{

	}// end checkStartTimes

	private void readTimeSignature(MidiFile omidiFile) throws MidiException
	{

	}// end readTimeSignature

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

	}// end RoundDuration

	/**
	 * Return true if this track contains multiple channels.
	 * If a MidiFile contains only one track, and it has multiple channels,
	 * then we treat each channel as a separate track.
	 */
	private boolean hasMultipleChannels(MidiTrack track)
	{
		return false;
	}// end hasMultipleChannels

	/**
	 * Split the given track into multiple tracks, separating each
	 * channel into a separate track.
	 */
	private List<MidiTrack> splitChannels(MidiFile file, List<MidiEvent> events)
	{
		return null;
	}// end splitChannels
}//end MidiFileReader - class