/**
 * @class MidiNote
 * A MidiNote contains
 * <p>
 * starttime - The time (measured in pulses) when the note is pressed.
 * channel - The channel the note is from. This is used when matching
 * noteOff events with the corresponding NoteOn event.
 * The channels for the NoteOn and noteOff events must be
 * the same.
 * notenumber - The note number, from 0 to 127. Middle C is 60.
 * duration - The time duration (measured in pulses) after which the
 * note is released.
 * <p>
 * A MidiNote is created when we encounter a noteOff event. The duration
 * is initially unknown (set to 0). When the corresponding noteOff event
 * is found, the duration is set by the method noteOff().
 */
public class MidiNote
{
	public static final String[]     HARMONY      = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
	public              String       name;
	public              int          startTime;                                                                        // The start time, in pulses
	public              int          channel;                                                                          // The channel
	public              int          notenumber;                                                                       // The note, from 0 to 127. Middle C is 60
	public              int          length;                                                                           // The duration, in pulses
	public              int          velocity;
	public              int          units;                                                                            // relative duration in units
	public              int          start;
	public              NoteDuration noteDuration;

	/**
	 * Create a new MidiNote. This is called when a NoteOn event is
	 * encountered in the MidiFile.
	 */
	public MidiNote(int starttime, int channel, int noteNumber, int velocity, int duration)
	{
		this.startTime  = starttime;
		this.channel    = channel;
		this.notenumber = noteNumber;
		this.length     = duration;
		this.velocity   = velocity;

		name            = HARMONY[(noteNumber + 3) % 12];
	}// end MidiNote - Constructor

	/**
	 * A noteOff event occurs for this note at the given time.
	 * Calculate the note duration based on the noteoff event.
	 */
	public void noteOff(int endtime)
	{
	}// end noteOff

	public NoteDuration getNoteDuration(int duration)
	{
		return null;
	}// end getNoteDuration

	@Override
	public String toString()
	{
		return HARMONY[(notenumber + 3) % 12] + "   " + noteDuration;
	}// end toString
}//end MidiNote - class