import java.util.List;

/**
 * @class MidiTrack
 * The MidiTrack takes as input the raw MidiEvents for the track, and
 * gets:
 * - The list of midi notes in the track.
 * - The first instrument used in the track.
 * <p>
 * For each NoteOn event in the midi file, a new MidiNote is created
 * and added to the track, using the AddNote() method.
 * <p>
 * The noteOff() method is called when a noteOff event is encountered,
 * in order to update the duration of the MidiNote.
 */

public class MidiTrack
{
	public int             quarterNote;
	public boolean         hasNotes;
	public int             tracknum;   // The track number
	public List<MidiNote>  notes;      // List of Midi notes
	public int             instrument; // Instrument for this track
	public List<MidiEvent> lyrics;     // The lyrics in this track

	/**
	 * Create an empty MidiTrack. Used by the clone method
	 */
	public MidiTrack(int tracknum, int quarterNote)
	{
		this.tracknum    = tracknum;
		this.quarterNote = quarterNote;
	}// end MidiTrack - constructor

	/*
	 * Create a MidiTrack based on the Midi events. Extract the NoteOn/noteOff
	 * events to gather the list of MidiNotes.
	 */
	public MidiTrack(List<MidiEvent> events, int tracknum)
	{

	}// end MidiTrack - constructor 2

	/**
	 * Add a MidiNote to this track. This is called for each NoteOn event
	 */
	public void addNote(MidiNote m)
	{
		this.notes.add(m);
	}// end addNote

	/**
	 * A noteOff event occurred. Find the MidiNote of the corresponding
	 * NoteOn event, and update the duration of the MidiNote.
	 */
	public void noteOff(int channel, int notenumber, int endtime)
	{

	}// end noteOff

	@Override
	public String toString()
	{
		return "Track " + tracknum + " " + getInstrumentName() + "\n";
	}// end toString

	public String getInstrumentName()
	{
		return "";
	}// end getInstrumentName
}//end MidiTrack - class