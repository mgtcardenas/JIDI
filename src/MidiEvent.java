public class MidiEvent
{
	/**
	 * @class MidiEvent
	 * A MidiEvent represents a single event (such as EventNoteOn) in the
	 * Midi file. It includes the delta time of the event.
	 */

	public String text;
	public String type; // Used to be EVENT_TYPE
	public String meta; // Used to be META_EVENT

	public int DeltaTime;    // The time between the previous event and this on
	public int StartTime;    // The absolute time this event occurs
	public int Tempo;        // The tempo, for Tempo meta events
	public int MetaLength; // The metaevent length

	public int     PitchBend;        // The pitch bend value
	public boolean HasEventflag;    // False if this is using the previous eventflag

	// TODO: ALL OF THE REST ATTRIBUTES USED TO BE BYTES

	public int EventFlag;    // NoteOn, NoteOff, etc. Full list is in class MidiFile
	public int Channel;    // The channel this event occurs on
	public int Notenumber; // The note number
	public int Volume;        // The volume of the note
	public int Instrument; // The instrument

	public int KeyPressure;    // The key pressure
	public int ChanPressure;    // The channel pressure
	public int ControlNum;    // The controller number
	public int ControlValue;    // The controller value

	public int    Numerator;        // The numerator, for TimeSignature meta events
	public int    Denominator;    // The denominator, for TimeSignature meta events
	public int    MetaEvent;        // The metaevent, used if eventflag is MetaEvent
	// SHOULD THIS BE BYTE ARRAY OR INT ARRAY?
	public byte[] Value;            // The raw byte value, for Sysex and meta events

	public MidiEvent()
	{
	}// end MidiEvent - constructor

	public static void main(String[] args)
	{
	}// end main

	@Override
	public String toString()
	{
		return text + " Start : " + StartTime + " Delta : " + DeltaTime;
	}// end toString

}// end MidiEvent