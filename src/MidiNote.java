/**
 * @class MidiNote
 * A MidiNote contains
 * <p>
 * starttime - The time (measured in pulses) when the note is pressed.
 * channel - The channel the note is from. This is used when matching
 * noteOff events with the corresponding NoteOn event.
 * The channels for the NoteOn and noteOff events must be
 * the same.
 * noteNumber - The note number, from 0 to 127. Middle C is 60.
 * duration - The time duration (measured in pulses) after which the
 * note is released.
 * <p>
 * A MidiNote is created when we encounter a noteOff event. The duration
 * is initially unknown (set to 0). When the corresponding noteOff event
 * is found, the duration is set by the method noteOff().
 */
public class MidiNote
{
	public static final String[]       HARMONY      = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
	private               String       name;
	private               int          startTime;                                                                        // The start time, in pulses
	private               int          channel;                                                                          // The channel
	private               int          noteNumber;                                                                       // The note, from 0 to 127. Middle C is 60
	private               int          length;                                                                           // The duration, in pulses
	private               int          velocity;
	private               int          units;                                                                            // relative duration in units
	private               int          start;
	private               NoteDuration noteDuration;

	/**
	 * Create a new MidiNote. This is called when a NoteOn event is
	 * encountered in the MidiFile.
	 */
	public MidiNote(int starttime, int channel, int noteNumber, int velocity, int duration)
	{
		this.startTime  = starttime;
		this.channel    = channel;
		this.noteNumber = noteNumber;
		this.length     = duration;
		this.velocity   = velocity;

		name            = HARMONY[(noteNumber + 3) % 12];
	}// end MidiNote - Constructor

	//region Getters & Setters
	public String getName()
	{
		return name;
	}//end getName

	public void setName(String name)
	{
		this.name = name;
	}//end setName

	public int getStartTime()
	{
		return startTime;
	}//end getStartTime

	public void setStartTime(int startTime)
	{
		this.startTime = startTime;
	}//end setStartTime

	public int getChannel()
	{
		return channel;
	}//end getChannel

	public void setChannel(int channel)
	{
		this.channel = channel;
	}//end setChannel

	public int getNoteNumber()
	{
		return noteNumber;
	}//end getNoteNumber

	public void setNoteNumber(int noteNumber)
	{
		this.noteNumber = noteNumber;
	}//end setNoteNumber

	public int getLength()
	{
		return length;
	}//end getLength

	public void setLength(int length)
	{
		this.length = length;
	}//end setLength

	public int getVelocity()
	{
		return velocity;
	}//end getVelocity

	public void setVelocity(int velocity)
	{
		this.velocity = velocity;
	}//end setVelocity

	public int getUnits()
	{
		return units;
	}//end getUnits

	public void setUnits(int units)
	{
		this.units = units;
	}//end setUnits

	public int getStart()
	{
		return start;
	}//end getStart

	public void setStart(int start)
	{
		this.start = start;
	}//end setStart

	public NoteDuration getNoteDuration()
	{
		return noteDuration;
	}//end getNoteDuration

	public void setNoteDuration(NoteDuration noteDuration)
	{
		this.noteDuration = noteDuration;
	}//end setNoteDuration
	//endregion Getters & Setters

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
		return HARMONY[(noteNumber + 3) % 12] + "   " + noteDuration;
	}// end toString
}//end MidiNote - class