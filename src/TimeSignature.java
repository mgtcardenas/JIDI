/**
 * @class TimeSignature
 * The TimeSignature class represents
 * - The time signature of the song, such as 4/4, 3/4, or 6/8 time, and
 * - The number of pulses per quarter note
 * - The number of microseconds per quarter note
 *
 * In midi files, all time is measured in "pulses". Each note has
 * a start time (measured in pulses), and a duration (measured in
 * pulses). This class is used mainly to convert pulse durations
 * (like 120, 240, etc) into note durations (half, quarter, eighth, etc).
 */
public class TimeSignature
{
	private int numerator;   // Numerator of the time signature
	private int denominator; // Denominator of the time signature
	private int quarternote; // Number of pulses per quarter note
	private int measure;     // Number of pulses per measure
	private int tempo;       // Number of microseconds per quarter note
	private int bpm;

	public TimeSignature(int numerator, int denominator, int quarternote, int tempo) throws MidiException
	{
		int beat;

		if (numerator <= 0 || denominator <= 0 || quarternote <= 0)
		    throw new MidiException("Invalid time signature", 0);

		// Midi File gives wrong time signature sometimes
		if (numerator == 5)
		    numerator = 4;

		this.numerator   = numerator;
		this.denominator = denominator;
		this.quarternote = quarternote;
		this.tempo       = tempo;

		if   (denominator == 2)
		    beat = quarternote       * 2;
		else
		    beat = (quarternote * 4) / denominator;

		measure = numerator * beat;
		bpm     = 60000000  / tempo;
	}// end TimeSignature - constructor

	//region Getters & Setters
	public int getNumerator()
	{
		return numerator;
	}//end getNumerator

	public void setNumerator(int numerator)
	{
		this.numerator = numerator;
	}//end setNumerator

	public int getDenominator()
	{
		return denominator;
	}//end getDenominator

	public void setDenominator(int denominator)
	{
		this.denominator = denominator;
	}//end setDenominator

	public int getQuarternote()
	{
		return quarternote;
	}//end getQuarternote

	public void setQuarternote(int quarternote)
	{
		this.quarternote = quarternote;
	}//end setQuarternote

	public int getMeasure()
	{
		return measure;
	}//end getMeasure

	public void setMeasure(int measure)
	{
		this.measure = measure;
	}//end setMeasure

	public int getTempo()
	{
		return tempo;
	}//end getTempo

	public void setTempo(int tempo)
	{
		this.tempo = tempo;
	}//end setTempo

	public int getBpm()
	{
		return bpm;
	}//end getBpm

	public void setBpm(int bpm)
	{
		this.bpm = bpm;
	}//end setBpm
	//endregion Getters & Setters

	//TODO: Verify that this works

	/**
	 * @param duration - the duration in pulses
	 * @return - the closest note duration
	 */
	public NoteDuration getNoteDuration(int duration)
	{
		int whole = quarternote * 4;
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

	/**
	 *
	 * @param time - the time in pulses
	 * @return - which measure the given time belongs to
	 */
	public int getMeasure(int time)
	{
		return time / measure;
	}// end getMeasure

	//TODO: Verify that this works

	/**
	 * Dotted durations are converted into their non-dotted equivalents
	 * @param dur - a note duration
	 * @return - a stem duration converted from a note duration
	 */
	public static NoteDuration getStemDuration(NoteDuration dur)
	{
		if      (dur == NoteDuration.DottedHalf   )
		    return NoteDuration.Half;
		else if (dur == NoteDuration.DottedQuarter)
		    return NoteDuration.Quarter;
		else if (dur == NoteDuration.DottedEighth )
		    return NoteDuration.Eighth;
		else
		    return dur;
	}// end getStemDuration

	//TODO: Verify that this works

	/**
	 *
	 * @param dur - a note duration
	 * @return - the time period in pulses the given duration spans
	 */
	public int durationToTime(NoteDuration dur)
	{
		int eighth    = quarternote / 2;
		int sixteenth = eighth      / 2;

		switch (dur)
		{
			case Whole:
				return quarternote * 4;
			case DottedHalf:
				return quarternote * 3;
			case Half:
				return quarternote * 2;
			case DottedQuarter:
				return 3 * eighth;
			case Quarter:
				return quarternote;
			case DottedEighth:
				return 3 * sixteenth;
			case Eighth:
				return eighth;
			case Triplet:
				return quarternote / 3;
			case Sixteenth:
				return sixteenth;
			case ThirtySecond:
				return sixteenth / 2;
			case HundredTwentyEight:
				return quarternote / 32;
			default:
				return 0;
		}//end switch
	}// end durationToTime

	@Override
	public String toString()
	{
		return String.format("%d/%d quarter : %d tempo : %d BPM : %d", numerator, denominator, quarternote, tempo, bpm);
	}// end toString
}//end TimeSignature - class