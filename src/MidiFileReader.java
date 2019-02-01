import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

	/**
	 * We want note durations to span up to the next note in general.
	 * The sheet music looks nicer that way. In contrast, sheet music
	 * with lots of 16th/32nd notes separated by small rests doesn't
	 * look as nice. Having nice looking sheet music is more important
	 * than faithfully representing the Midi File data.
	 * <p>
	 * Therefore, this function rounds the duration of MidiNotes up to
	 * the next note where possible.
	 */
	public static void roundDurations(MidiFile midiFile)
	{

	}// end RoundDuration

	public void readFile(MidiFile midiFile) throws MidiException, UnsupportedEncodingException
	{

	}// end readFile

	private void verifyHeader(MidiFile omidiFile) throws MidiException, UnsupportedEncodingException
	{

	}// end verifyHeader

	private void computeTracks(MidiFile omidiFile) throws MidiException, UnsupportedEncodingException
	{

	}// end computeTracks

	private void computePulsesSong(MidiFile omidiFile)
	{

	}// end computePulsesSong

	private void verifyChannels(MidiFile omidiFile)
	{

	}// end verifyChannels

	/**
	 * <summary>
	 * Check that the MidiNote start times are in increasing order.
	 * This is for debugging purposes.
	 * </summary>
	 * <param name="tracks"></param>
	 */
	private void checkStartTimes(List<MidiTrack> tracks) throws MidiException
	{

	}// end checkStartTimes

	private void readTimeSignature(MidiFile omidiFile) throws MidiException
	{

	}// end readTimeSignature

	/**
	 * Parse a single Midi track into a list of MidiEvents.
	 * Entering this function, the file offset should be at the start of the MTrk
	 * header.
	 * Upon exiting, the file offset should be at the
	 * start of the next MTrk header.
	 */
	private List<MidiEvent> readTrackEvents() throws MidiException, UnsupportedEncodingException
	{
		return null;
	}// end readTrackEvents

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

	private void defineMetaEvent(MidiEvent mEvent) throws MidiException, UnsupportedEncodingException
	{

	}// end defineMetaEvent

	private String setMetaEventTempo(MidiEvent mEvent) throws MidiException
	{
		return "";
	}// end setMetaEventTempo

	private String setMetaEventTimeSignature(MidiEvent mEvent)
	{
		return "";
	}// end setMetaEventTimeSignature
}//end MidiFileReader - class