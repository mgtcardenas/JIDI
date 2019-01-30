using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MIDEX
{
    /** @class MidiTrack
         * The MidiTrack takes as input the raw MidiEvents for the track, and gets:
         * - The list of midi notes in the track.
         * - The first instrument used in the track.
         *
         * For each NoteOn event in the midi file, a new MidiNote is created
         * and added to the track, using the AddNote() method.
         * 
         * The NoteOff() method is called when a NoteOff event is encountered,
         * in order to update the duration of the MidiNote.
         */
    public class MidiTrack
    {
        int quarterNote;
        public  bool HasNotes;
        private int tracknum;             /** The track number */
        private List<MidiNote> notes;     /** List of Midi notes */
        private int instrument;           /** Instrument for this track */
        private List<MidiEvent> lyrics;   /** The lyrics in this track */

        /** Create an empty MidiTrack.  Used by the Clone method */
        public MidiTrack(int tracknum, int quarterNote)
        {
            this.tracknum = tracknum;
            notes = new List<MidiNote>();
            this.quarterNote = quarterNote;
            instrument = 0;
        }

        /** Create a MidiTrack based on the Midi events.  Extract the NoteOn/NoteOff
         *  events to gather the list of MidiNotes.
         */
        public MidiTrack(List<MidiEvent> events, int tracknum)
        {
            int lyriccount;

            this.tracknum = tracknum;
            notes = new List<MidiNote>(events.Count);
            instrument = 0;

            foreach (MidiEvent mevent in events)
            {
                if (mevent.EventFlag == MUtil.EventNoteOn && mevent.Volume > 0)
                {
                    MidiNote note = new MidiNote(mevent.StartTime, mevent.Channel, mevent.Notenumber,mevent.Volume, 0);
                    AddNote(note);
                }
                else
                    if (mevent.EventFlag == MUtil.EventNoteOn && mevent.Volume == 0)
                    {
                        NoteOff(mevent.Channel, mevent.Notenumber, mevent.StartTime);
                        mevent.EventFlag = (byte)(128 + mevent.Channel);
                        //mevent.Text = MidiFile.EventName(mevent.EventFlag);
                    }
                    else
                        if (mevent.EventFlag == (MUtil.EventNoteOff ))
                        {
                            NoteOff(mevent.Channel, mevent.Notenumber, mevent.StartTime);
                            //mevent.EventFlag = (byte)(127 + mevent.Channel);
                            //mevent.Text = MidiFile.EventName(mevent.EventFlag);
                        }
                        else
                            if (mevent.EventFlag == MUtil.EventProgramChange)
                            {
                                instrument = mevent.Instrument;
                            }
                            else
                                if (mevent.MetaEvent == MUtil.MetaEventLyric)
                                {
                                    if (lyrics == null)
                                    {
                                        lyrics = new List<MidiEvent>();
                                    }
                                    lyrics.Add(mevent);
                                }
            }
            if (notes.Count > 0 && notes[0].Channel == 9)
            {
                instrument = 128;  /* Percussion */
            }
            lyriccount = 0;

            if (lyrics != null)
            {
                lyriccount = lyrics.Count;
            }
        }

        public int Number
        {
            get { return tracknum; }
        }

        public List<MidiNote> Notes
        {
            get { return notes; }
        }

        public int Instrument
        {
            get { return instrument; }
            set { instrument = value; }
        }

        public string InstrumentName
        {
            get
            {
                if (instrument >= 0 && instrument <= 128)
                    return MUtil.Instruments[instrument];
                else
                    return "";
            }
        }

        public List<MidiEvent> Lyrics
        {
            get { return lyrics; }
            set { lyrics = value; }
        }

        /** Add a MidiNote to this track.  This is called for each NoteOn event */
        public void AddNote(MidiNote m)
        {
            notes.Add(m);
        }

        /** A NoteOff event occured.  Find the MidiNote of the corresponding
         * NoteOn event, and update the duration of the MidiNote.
         */
        public void NoteOff(int channel, int notenumber, int endtime)
        {
            MidiNote note;
            for (int i = notes.Count - 1; i >= 0; i--)
            {
                note = notes[i];
                if (note.Channel == channel && note.Number == notenumber && note.Length == 0)
                {
                    note.NoteOff(endtime);
                    return;
                }
            }
        }
    
        public override string ToString()
        {
            string result = "Track " + tracknum + " " + (INSTRUMENT)instrument + "\n";
            //foreach (MidiNote n in notes)
            //{
            //    result = result + n + "\n";
            //}
            //result += "End Track\n";
            return result;
        }
    }
}
