/**
 * @class TimeSignature
 * The TimeSignature class represents
 * - The time signature of the song, such as 4/4, 3/4, or 6/8 time, and
 * - The number of pulses per quarter note
 * - The number of microseconds per quarter note
 * <p>
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

	/**
	 * Create a new time signature, with the given numerator,
	 * denominator, pulses per quarter note, and tempo.
	 */
	public TimeSignature(int numerator, int denominator, int quarternote, int tempo) throws MidiException
	{

	}// end TimeSignature - constructor

	/**
	 * Convert a note duration into a stem duration. Dotted durations
	 * are converted into their non-dotted equivalents.
	 */
	public static NoteDuration getStemDuration(NoteDuration dur)
	{
		return null;
	}// end getStemDuration

	/**
	 * Return which measure the given time (in pulses) belongs to.
	 */
	public int getMeasure(int time)
	{
		return time / measure;
	}// end getMeasure

	/**
	 * Given a duration in pulses, return the closest note duration.
	 */
	public NoteDuration getNoteDuration(int duration)
	{
		return null;
	}// end getNoteDuration

	/**
	 * Return the time period (in pulses) the the given duration spans
	 */
	public int durationToTime(NoteDuration dur)
	{
		return 0;
	}// end durationToTime

	@Override
	public String toString()
	{
		return String.format("%d/%d quarter : %d tempo : %d BPM : %d", numerator, denominator, quarternote, tempo, bpm);
	}// end toString
}//end TimeSignature - class