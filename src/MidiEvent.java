/**
 * @class MidiEvent
 * A MidiEvent represents a single event (such as EventNoteOn) in the
 * Midi file. It includes the delta time of the event.
 */
public class MidiEvent
{
	private String  text;
	private String  type;         // Used to be EVENT_TYPE
	private String  meta;         // Used to be META_EVENT
	private long    deltaTime;    // The time between the previous event and this on
	private long    startTime;    // The absolute time this event occurs
	private int     tempo;        // The tempo, for tempo meta events
	private int     metaLength;   // The metaevent length
	private int     pitchBend;    // The pitch bend value
	private boolean hasEventflag; // False if this is using the previous eventflag
	private int     eventFlag;    // NoteOn, noteOff, etc. Full list is in class MidiFile
	private int     channel;      // The channel this event occurs on
	private int     notenumber;   // The note number (as in C4, D3, Bb5, ...)
	private int     volume;       // The volume of the note aka velocity
	private int     instrument;   // The instrument
	private int     keyPressure;  // The key pressure
	private int     chanPressure; // The channel pressure
	private int     controlNum;   // The controller number
	private int     controlValue; // The controller value
	private int     numerator;    // The numerator, for TimeSignature meta events
	private int     denominator;  // The denominator, for TimeSignature meta events
	private int     metaEvent;    // The metaevent, used if eventflag is metaEvent
	private byte[]  value;        // The raw byte value, for Sysex and meta events; SHOULD THIS BE BYTE ARRAY OR INT ARRAY?

	public MidiEvent()
	{
	}// end MidiEvent - constructor

	//region Getters & Setters
	public String getText()
	{
		return text;
	}//end getText

	public void setText(String text)
	{
		this.text = text;
	}//end setText

	public String getType()
	{
		return type;
	}//end getType

	public void setType(String type)
	{
		this.type = type;
	}//end setType

	public String getMeta()
	{
		return meta;
	}//end getMeta

	public void setMeta(String meta)
	{
		this.meta = meta;
	}//end setMeta

	public long getDeltaTime()
	{
		return deltaTime;
	}//end getDeltaTime

	public void setDeltaTime(long deltaTime)
	{
		this.deltaTime = deltaTime;
	}//end setDeltaTime

	public long getStartTime()
	{
		return startTime;
	}//end getStartTime

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}//end setStartTime

	public int getTempo()
	{
		return tempo;
	}//end getTempo

	public void setTempo(int tempo)
	{
		this.tempo = tempo;
	}//end setTempo

	public int getMetaLength()
	{
		return metaLength;
	}//end getMetaLength

	public void setMetaLength(int metaLength)
	{
		this.metaLength = metaLength;
	}//end setMetaLength

	public int getPitchBend()
	{
		return pitchBend;
	}//end getPitchBend

	public void setPitchBend(int pitchBend)
	{
		this.pitchBend = pitchBend;
	}//end setPitchBend

	public boolean hasEventflag()
	{
		return hasEventflag;
	}//end hasEventFlag

	public void setHasEventflag(boolean hasEventflag)
	{
		this.hasEventflag = hasEventflag;
	}//end setHasEventflag

	public int getEventFlag()
	{
		return eventFlag;
	}//end getEventFlag

	public void setEventFlag(int eventFlag)
	{
		this.eventFlag = eventFlag;
	}//end setEventFlag

	public int getChannel()
	{
		return channel;
	}//end getChannel

	public void setChannel(int channel)
	{
		this.channel = channel;
	}//end setChannel

	public int getNotenumber()
	{
		return notenumber;
	}//end getNotenumber

	public void setNotenumber(int notenumber)
	{
		this.notenumber = notenumber;
	}//end setNotenumber

	public int getVolume()
	{
		return volume;
	}//end getVolume

	public void setVolume(int volume)
	{
		this.volume = volume;
	}//end setVolume

	public int getInstrument()
	{
		return instrument;
	}//end getInstrument

	public void setInstrument(int instrument)
	{
		this.instrument = instrument;
	}//end setInstrument

	public int getKeyPressure()
	{
		return keyPressure;
	}//end getKeyPressure

	public void setKeyPressure(int keyPressure)
	{
		this.keyPressure = keyPressure;
	}//end setKeyPressure

	public int getChanPressure()
	{
		return chanPressure;
	}//end getChanPressure

	public void setChanPressure(int chanPressure)
	{
		this.chanPressure = chanPressure;
	}//end setChanPressure

	public int getControlNum()
	{
		return controlNum;
	}//end getControlNum

	public void setControlNum(int controlNum)
	{
		this.controlNum = controlNum;
	}//end setControlNum

	public int getControlValue()
	{
		return controlValue;
	}//end getControlValue

	public void setControlValue(int controlValue)
	{
		this.controlValue = controlValue;
	}//end setControlValue

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

	public int getMetaEvent()
	{
		return metaEvent;
	}//end getMetaEvent

	public void setMetaEvent(int metaEvent)
	{
		this.metaEvent = metaEvent;
	}//end setMetaEvent

	public byte[] getValue()
	{
		return value;
	}//end getValue

	public void setValue(byte[] value)
	{
		this.value = value;
	}//end setValue
	//endregion Getters & Setters

	@Override
	public String toString()
	{
		return text + " Start : " + startTime + " Delta : " + deltaTime;
	}// end toString
}//end MidiEvent - class