/**
 * @class MidiEvent
 * A MidiEvent represents a single event (such as EventNoteOn) in the
 * Midi file. It includes the delta time of the event.
 */
public class MidiEvent
{
	public String  text;
	public String  type;         // Used to be EVENT_TYPE
	public String  meta;         // Used to be META_EVENT
	public int     deltaTime;    // The time between the previous event and this on
	public int     startTime;    // The absolute time this event occurs
	public int     tempo;        // The tempo, for tempo meta events
	public int     metaLength;   // The metaevent length
	public int     pitchBend;    // The pitch bend value
	public boolean hasEventflag; // False if this is using the previous eventflag
	public int     eventFlag;    // NoteOn, noteOff, etc. Full list is in class MidiFile
	public int     channel;      // The channel this event occurs on
	public int     notenumber;   // The note number
	public int     volume;       // The volume of the note
	public int     instrument;   // The instrument
	public int     keyPressure;  // The key pressure
	public int     chanPressure; // The channel pressure
	public int     controlNum;   // The controller number
	public int     controlValue; // The controller value
	public int     numerator;    // The numerator, for TimeSignature meta events
	public int     denominator;  // The denominator, for TimeSignature meta events
	public int     metaEvent;    // The metaevent, used if eventflag is metaEvent
	public byte[]  value;        // The raw byte value, for Sysex and meta events; SHOULD THIS BE BYTE ARRAY OR INT ARRAY?

	public MidiEvent()
	{
	}// end MidiEvent - constructor

	@Override
	public String toString()
	{
		return text + " Start : " + startTime + " Delta : " + deltaTime;
	}// end toString
}//end MidiEvent - class