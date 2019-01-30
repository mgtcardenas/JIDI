using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MIDEX
{
    /** @class MidiNote
       * A MidiNote contains
       *
       * starttime - The time (measured in pulses) when the note is pressed.
       * channel   - The channel the note is from.  This is used when matching
       *             NoteOff events with the corresponding NoteOn event.
       *             The channels for the NoteOn and NoteOff events must be
       *             the same.
       * notenumber - The note number, from 0 to 127.  Middle C is 60.
       * duration  - The time duration (measured in pulses) after which the 
       *             note is released.
       *
       * A MidiNote is created when we encounter a NoteOff event.  The duration
       * is initially unknown (set to 0).  When the corresponding NoteOff event
       * is found, the duration is set by the method NoteOff().
       */
    public class MidiNote
    {
        //private static readonly string[] scale = { "A", "A%23", "B", "C", "C%23", "D", "D%23", "E", "F", "F%23", "G", "G%23" };
        private static readonly string[] harmony = { "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" };

        private string name;
        private int startTime;   /** The start time, in pulses */
        private int channel;     /** The channel */
        private int notenumber;  /** The note, from 0 to 127. Middle C is 60 */
        private int length;     /** The duration, in pulses */
        private int velocity;
        private int units;      //** relative duration in units

        NoteDuration noteDuration;
        private int start;

        public int Start
        {
            get { return start; }
            set { start = value; }
        }

        public NoteDuration NoteDuration
        {
            get { return noteDuration; }
            set { noteDuration = value; }
        }

        public int Units
        {
            get { return units; }
        }

        /* Create a new MidiNote.  This is called when a NoteOn event is
         * encountered in the MidiFile.
         */
        public MidiNote(int starttime, int channel, int notenumber, int velocity, int duration)
        {
            this.startTime  = starttime;
            this.channel    = channel;
            this.notenumber = notenumber;
            this.length   = duration;
            this.velocity   = velocity;

            name = harmony[(notenumber + 3) % 12];
        }
        public string Name
        {
            get { return name; }
        }

        public int StartTime
        {
            get { return startTime; }
            set { startTime = value; }
        }

        public int EndTime
        {
            get { return startTime + length; }
        }

        public int Channel
        {
            get { return channel; }
            set { channel = value; }
        }

        public int Number
        {
            get { return notenumber; }
            set { notenumber = value; }
        }

        public int Length
        {
            get { return length; }
            set { length = value; }
        }

        /* A NoteOff event occurs for this note at the given time.
         * Calculate the note duration based on the noteoff event.
         */
        public void NoteOff(int endtime)
        {
            length = endtime - startTime;
            noteDuration = GetNoteDuration(length);

            //int whole = MUtil.QuarterNote * 4;
            int unit = MUtil.QuarterNote / 32;
            units = (length * 15) / unit;
            //units /= unit;
            //units *= 15; // to change to our new unit
        }


        public NoteDuration GetNoteDuration(int duration)
        {
            int whole = MUtil.QuarterNote * 4;

            /**
             1       = 32/32
             3/4     = 24/32
             1/2     = 16/32
             3/8     = 12/32
             1/4     =  8/32
             3/16    =  6/32
             1/8     =  4/32 =    8/64
             triplet =  5.33/64
             1/16    =  2/32 =    4/64
             1/32    =  1/32 =    2/64
             **/

            if (duration >= 28 * whole / 32)
                return NoteDuration.Whole;
            else if (duration >= 20 * whole / 32)
                return NoteDuration.DottedHalf;
            else if (duration >= 14 * whole / 32)
                return NoteDuration.Half;
            else if (duration >= 10 * whole / 32)
                return NoteDuration.DottedQuarter;
            else if (duration >= 7 * whole / 32)
                return NoteDuration.Quarter;
            else if (duration >= 5 * whole / 32)
                return NoteDuration.DottedEighth;
            else if (duration >= 6 * whole / 64)
                return NoteDuration.Eighth;
            else if (duration >= 5 * whole / 64)
                return NoteDuration.Triplet;
            else if (duration >= 3 * whole / 64)
                return NoteDuration.Sixteenth;
            else if (duration >= 2 * whole / 64)
                return NoteDuration.ThirtySecond;
            else if (duration >= whole / 64)
                return NoteDuration.SixtyFour;// TODO : EXTEND UNTIL 1/128 to be able to extract the onset in SYMBOLIC representation
            else if (duration >= whole / 128)
                return NoteDuration.HundredTwentyEight;
            else
                return NoteDuration.ZERO;
        }

        
        public override string ToString()
        {
            //return string.Format("Note Ch : {0} key : {1} {2}  \t: VEL {3} \t: {4} - {5}]", channel, notenumber, scale(notenumber + 3) % 12],velocity, startTime,(startTime + duration));
            ////return string.Format("Note Ch : {0} key : {1} \t: note {3} \t: VEL{4} \t:{5} - {6}]", channel, notenumber, scale[(notenumber + 3) % 12], velocity, startTime, (startTime + duration));
            return harmony[(notenumber + 3) % 12] + "   " + noteDuration;
        }
    }

}
