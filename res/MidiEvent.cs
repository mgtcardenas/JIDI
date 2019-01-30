using System.Collections.Generic;

namespace MIDEX
{
    /** @class MidiEvent
     * A MidiEvent represents a single event (such as EventNoteOn) in the
     * Midi file. It includes the delta time of the event.
     */
    public class MidiEvent 
    {
        private string text;
        private EVENT_TYPE type;
        private META_EVENT meta;       
        
        public int DeltaTime;      /** The time between the previous event and this on */
        public int StartTime;      /** The absolute time this event occurs */
        public int Tempo;          /** The tempo, for Tempo meta events */
        public int MetaLength;     /** The metaevent length  */

        public ushort PitchBend;   /** The pitch bend value */
        public bool HasEventflag;  /** False if this is using the previous eventflag */

        public byte EventFlag;     /** NoteOn, NoteOff, etc.  Full list is in class MidiFile */
        public byte Channel;       /** The channel this event occurs on */
        public byte Notenumber;    /** The note number  */
        public byte Volume;        /** The volume of the note */
        public byte Instrument;    /** The instrument */

        public byte KeyPressure;   /** The key pressure */
        public byte ChanPressure;  /** The channel pressure */
        public byte ControlNum;    /** The controller number */
        public byte ControlValue;  /** The controller value */

        public byte Numerator;     /** The numerator, for TimeSignature meta events */
        public byte Denominator;   /** The denominator, for TimeSignature meta events */
        public byte MetaEvent;     /** The metaevent, used if eventflag is MetaEvent */
        public byte[] Value;       /** The raw byte value, for Sysex and meta events */

        public MidiEvent()
        {
        }

        public string Text
        {
            get { return text; }
            set { text = value; }
        }

        public EVENT_TYPE Type
        {
            get { return type; }
            set { type = value; }
        }

        public META_EVENT Meta
        {
            get { return meta; }
            set { meta = value; }
        }

        public override string ToString()
        {
            return text + " Start : " + StartTime + " Delta : " +DeltaTime;
        }
    }
}
