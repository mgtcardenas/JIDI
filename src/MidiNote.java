/**
 * @class MidiNote
 * A MidiNote contains
 *
 * starttime - The time (measured in pulses) when the note is pressed.
 * channel   - The channel the note is from.  This is used when matching
 * NoteOff events with the corresponding NoteOn event.
 * The channels for the NoteOn and NoteOff events must be
 * the same.
 * notenumber - The note number, from 0 to 127.  Middle C is 60.
 * duration  - The time duration (measured in pulses) after which the
 * note is released.
 *
 * A MidiNote is created when we encounter a NoteOff event.  The duration
 * is initially unknown (set to 0).  When the corresponding NoteOff event
 * is found, the duration is set by the method NoteOff().
 */
public class MidiNote
{
	public  static final String[]     HARMONY      = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
	private              String       name;
	private              int          startTime;                                                                        // The start time, in pulses
	private              int          channel;                                                                          // The channel
	private              int          noteNumber;                                                                       // The note, from 0 to 127. Middle C is 60
	private              int          length;                                                                           // The duration, in pulses
	private              int          velocity;
	private              int          units;                                                                            // relative duration in units
	private              int          start;
	private              NoteDuration noteDuration;

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

	//TODO: Verify that this works

	/**
	 * A noteOff event occurs for this note at the given time.
	 * Calculate the note duration based on the noteoff event.
	 */
	public void noteOff(int endtime)
	{
		length       = endtime - startTime;
		noteDuration = getNoteDuration(length);

		//int whole = MUtil.QuarterNote * 4;
		int unit = MUtil.QuarterNote / 32;
		units = (length * 15) / unit;
		//units /= unit;
		//units *= 15; // to change to our new unit
	}// end noteOff

	//TODO: Verify that this works

	public NoteDuration getNoteDuration(int duration)
	{
		int whole = MUtil.QuarterNote * 4;
		//		 1       = 32/32
		//		 3/4     = 24/32
		//		 1/2     = 16/32
		//		 3/8     = 12/32
		//		 1/4     =  8/32
		//		 3/16    =  6/32
		//		 1/8     =  4/32 =    8/64
		//		 triplet =  5.33/64
		//		 1/16    =  2/32 =    4/64
		//		 1/32    =  1/32 =    2/64

		if      (duration >= 28 * whole /  32)
		    return NoteDuration.Whole;
		else if (duration >= 20 * whole /  32)
		    return NoteDuration.DottedHalf;
		else if (duration >= 14 * whole /  32)
		    return NoteDuration.Half;
		else if (duration >= 10 * whole /  32)
		    return NoteDuration.DottedQuarter;
		else if (duration >=  7 * whole /  32)
		    return NoteDuration.Quarter;
		else if (duration >=  5 * whole /  32)
		    return NoteDuration.DottedEighth;
		else if (duration >=  6 * whole /  64)
		    return NoteDuration.Eighth;
		else if (duration >=  5 * whole /  64)
		    return NoteDuration.Triplet;
		else if (duration >=  3 * whole /  64)
		    return NoteDuration.Sixteenth;
		else if (duration >=  2 * whole /  64)
		    return NoteDuration.ThirtySecond;
		else if (duration >= whole      /  64)
		    return NoteDuration.SixtyFour; // TODO : EXTEND UNTIL 1/128 to be able to extract the onset in SYMBOLIC representation
		else if (duration >= whole      / 128)
		    return NoteDuration.HundredTwentyEight;
		else
		    return NoteDuration.ZERO;
	}// end getNoteDuration

	@Override
	public String toString()
	{
		//return string.Format("Note Ch : {0} key : {1} {2}  \t: VEL {3} \t: {4} - {5}]", channel, notenumber, scale(notenumber + 3) % 12],velocity, startTime,(startTime + duration));
		//return string.Format("Note Ch : {0} key : {1} \t: note {3} \t: VEL{4} \t:{5} - {6}]", channel, notenumber, scale[(notenumber + 3) % 12], velocity, startTime, (startTime + duration));
		return HARMONY[(noteNumber + 3) % 12] + "   " + noteDuration;
	}// end toString
}//end MidiNote - class