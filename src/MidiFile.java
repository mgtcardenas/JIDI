import java.util.List;

/**
 * @class MidiFile
 * The MidiFile class contains the parsed data from the Midi File. It contains:
 * - All the tracks in the midi file, including all MidiNotes per track.
 * - The time signature (e.g. 4/4, 3/4, 6/8)
 * - The number of pulses per quarter note.
 * - The tempo (number of microseconds per quarter note).
 * <p>
 * The constructor takes a filename as input, and upon returning,
 * contains the parsed data from the midi file.
 * <p>
 * The methods ReadTrack() and ReadMetaEvent() are helper functions
 * called by the constructor during the parsing.
 * <p>
 * After the MidiFile is parsed and created, the user can retrieve the
 * tracks and notes by using the property Tracks and Tracks.Notes.
 * <p>
 * There are two methods for modifying the midi data based on the menu
 * options selected:
 * <p>
 * - ChangeMidiNotes()
 * Apply the menu options to the parsed MidiFile.
 * This uses the helper functions:
 * SplitTrack()
 * CombineToTwoTracks()
 * ShiftTime()
 * Transpose()
 * RoundStartTimes()
 * roundDurations()
 * <p>
 * - ChangeSound()
 * Apply the menu options to the MIDI music data, and save the modified
 * midi data to a file, for playback.
 */
public class MidiFile
{
	public String          fileName;
	public List<MidiEvent> events;
	public List<MidiTrack> tracks;
	public int             trackMode;
	public TimeSignature   timeSig;
	public int             quarterNote;
	public int             totalPulses;
	public boolean         trackPerChannel;
	public int             numEventTracks;

	public MidiFile(String fileName)
	{
		this.fileName = fileName;
	}//end MidiFile - constructor

	private String getFileNameWithoutExtension(String fileName)
	{
		return "";
	}
}//end MidiFile - class