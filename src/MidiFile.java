import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @class MidiFile
 * The MidiFile class contains the parsed data from the Midi File. It contains:
 * - All the tracks in the midi file, including all MidiNotes per track.
 * - The time signature (e.g. 4/4, 3/4, 6/8)
 * - The number of pulses per quarter note.
 * - The tempo (number of microseconds per quarter note).
 *
 * The constructor takes a filename as input, and upon returning,
 * contains the parsed data from the midi file.
 *
 * The methods ReadTrack() and ReadMetaEvent() are helper functions
 * called by the constructor during the parsing.
 *
 * After the MidiFile is parsed and created, the user can retrieve the
 * tracks and notes by using the property Tracks and Tracks.Notes.
 *
 * There are two methods for modifying the midi data based on the menu
 * options selected:
 *
 * - ChangeMidiNotes()
 * - Apply the menu options to the parsed MidiFile.
 * - This uses the helper functions:
 * - SplitTrack()
 * - CombineToTwoTracks()
 * - ShiftTime()
 * - Transpose()
 * - RoundStartTimes()
 * - roundDurations()
 * - ChangeSound()
 *
 * Apply the menu options to the MIDI music data, and save the modified
 * midi data to a file, for playback.
 */
public class MidiFile
{
	private String            fileName;        // The MIDI file name
	private List<MidiEvent>[] events;          // The raw MIDI Events, one list per track as an array of list
	private List<MidiTrack>   tracks;          // The tracks of the MIDI file that have notes
	private int               trackMode;       // 0 (single track), 1 (simultaneous tracks), 2 (independent tracks)
	private TimeSignature     timeSig;         // The Time Signature (e.g. 4/4, 3/4, 6/8)
	private int               quarterNote;     // The number of pulses per quarter note. A pulse is a set time unit
	private int               totalPulses;     // The total length of the song, in pulses
	private boolean           trackPerChannel; // True if we've split each channel into a track
	private int               numEventTracks;

	public MidiFile(String fileName) throws IOException, MidiException
	{
		File           file;
		MidiFileReader mfr;

		//gets filename without extension but keeps filename as path for MidiFileReader constructor
		file          = new File(fileName);
		this.fileName = file.getName();
		mfr           = new MidiFileReader(fileName);

		mfr.readFile(this);           // This method initiates flow of operations
	}//end MidiFile - constructor

	//region Getters & Setters
	public String getFileName()
	{
		return fileName;
	}//end getFileName

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}//end setFileName

	public List<MidiEvent>[] getEvents()
	{
		return events;
	}//end getEvents

	public void setEvents(List<MidiEvent>[] events)
	{
		this.events = events;
	}//end setEvents

	public List<MidiTrack> getTracks()
	{
		return tracks;
	}//end getTracks

	public void setTracks(List<MidiTrack> tracks)
	{
		this.tracks = tracks;
	}//end setTracks

	public int getTrackMode()
	{
		return trackMode;
	}//end getTrackMode

	public void setTrackMode(int trackMode)
	{
		this.trackMode = trackMode;
	}//end setTrackMode

	public TimeSignature getTimeSig()
	{
		return timeSig;
	}//end getTimeSig

	public void setTimeSig(TimeSignature timeSig)
	{
		this.timeSig = timeSig;
	}//end setTimeSig

	public int getQuarterNote()
	{
		return quarterNote;
	}//end getQuarterNote

	public void setQuarterNote(int quarterNote)
	{
		this.quarterNote = quarterNote;
	}//end setQuarterNote

	public int getTotalPulses()
	{
		return totalPulses;
	}//end getTotalPulses

	public void setTotalPulses(int totalPulses)
	{
		this.totalPulses = totalPulses;
	}//end setTotalPulses

	public boolean hasTrackPerChannel()
	{
		return trackPerChannel;
	}//end hasTrackPerChannel

	public void setTrackPerChannel(boolean trackPerChannel)
	{
		this.trackPerChannel = trackPerChannel;
	}//end setTrackPerChannel

	public int getNumEventTracks()
	{
		return numEventTracks;
	}//end getNumEventTracks

	public void setNumEventTracks(int numEventTracks)
	{
		this.numEventTracks = numEventTracks;
	}//end setNumEventTracks
	//endregion Getters & Setters

	public boolean write(String name, List<MidiEvent>[] events, int quarterSize)
	{
		MidiFileWriter writer = new MidiFileWriter();
		return writer.write(name, events, quarterSize);
	}//end write

	@Override
	public String toString()
	{
		String result = "Midi File tracks = " + tracks.size() + "\n";
		result += timeSig.toString();
		return result;
	}//eng toString
}//end MidiFile - class