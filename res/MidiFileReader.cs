using System.Collections.Generic;
using System;

namespace MIDEX
{
    public class MidiFileReader
    {
        private ByteFileReader file;

        public MidiFileReader(string fileName)
        {
            file = new ByteFileReader(fileName);
        }

        public void ReadFile(MidiFile midiFile)
        {
            VerifyHeader(midiFile);

            midiFile.TrackMode         = file.ReadShort();
            midiFile.NumEventTracks    = file.ReadShort();
            midiFile.QuarterNote       = file.ReadShort();

            MUtil.SetQuarterNote(midiFile.QuarterNote);

            midiFile.Events            = new List<MidiEvent>[midiFile.NumEventTracks];
            midiFile.Tracks            = new List<MidiTrack>();
            midiFile.TrackPerChannel   = false;

            ComputeTracks(midiFile);
            ComputePulsesSong(midiFile);
            VerifyChannels(midiFile);
            CheckStartTimes(midiFile.Tracks);
            ReadTimeSignature(midiFile);
            //RoundDurations(midiFile);

            file.Empty();
        }

        private void VerifyHeader(MidiFile omidiFile)
        {
            int length;
           
            if (file.ReadAscii(4) != "MThd")
            {
                throw new MidiException("Doesn't start with MThd", 0);
            }

            length = file.ReadInt();
            if (length != 6)
            {
                throw new MidiException("Bad MThd header", 4);
            }
        }

        private void ComputeTracks(MidiFile omidiFile)
        {
            MidiTrack track;
            for (int tracknum = 0; tracknum < omidiFile.NumEventTracks; tracknum++)
            {
                omidiFile.Events[tracknum] = ReadTrackEvents();
                track = new MidiTrack(omidiFile.Events[tracknum], tracknum);
                track.HasNotes = (track.Notes.Count > 0);

                if (track.HasNotes)
                {
                    omidiFile.Tracks.Add(track);
                }
            }
        }

        private void VerifyChannels(MidiFile omidiFile)
        {
            /* If we only have one track with multiple channels, then treat
             * each channel as a separate track.
             */
            if (omidiFile.Tracks.Count == 1 && HasMultipleChannels(omidiFile.Tracks[0]))
            {
                omidiFile.Tracks = SplitChannels(omidiFile, omidiFile.Events[omidiFile.Tracks[0].Number]);
                omidiFile.TrackPerChannel = true;
            }
        }

        private void ComputePulsesSong(MidiFile omidiFile)
        {
            for (int ev = 0; ev < omidiFile.Events.Length; ev++)
            {
                for (int e = 0; e < omidiFile.Events[ev].Count; e++)
                {
                    if (omidiFile.TotalPulses < omidiFile.Events[ev][e].StartTime)
                    {
                        omidiFile.TotalPulses = omidiFile.Events[ev][e].StartTime;
                    }
                }
            }
        }
                
        private void ReadTimeSignature(MidiFile omidiFile)
        {
            /* Determine the time signature */
            int tempo = 0;
            int numer = 0;
            int denom = 0;

            List<MidiEvent> list;
            for (int i = 0; i < omidiFile.Events.Length; i++)
            {
                list = omidiFile.Events[i];
                MidiEvent mevent;
                for (int j = 0; j < list.Count; j++)
                {
                    mevent = list[j];
                    if (mevent.MetaEvent == MUtil.MetaEventTempo && tempo == 0)
                    {
                        tempo = mevent.Tempo;
                    }
                    if (mevent.MetaEvent == MUtil.MetaEventTimeSignature && numer == 0)
                    {
                        numer = mevent.Numerator;
                        denom = mevent.Denominator;
                    }
                }
            }

            if (tempo == 0)
            {
                tempo = 500000; /* 500,000 microseconds = 0.05 sec */
            }
            if (numer == 0)
            {
                numer = denom = 4;
            }
            omidiFile.Time = new TimeSignature(numer, denom, omidiFile.QuarterNote, tempo);
        }

        /// <summary>
        /// Parse a single Midi track into a list of MidiEvents.
        /// Entering this function, the file offset should be at the start of the MTrk header.  
        /// Upon exiting, the file offset should be at the
        /// start of the next MTrk header.
        /// </summary>
        /// <returns></returns>
        private List<MidiEvent> ReadTrackEvents()
        {
            List<MidiEvent> result = new List<MidiEvent>();
            int starttime = 0;
            int eventflag;
            int tracklength;
            int trackend;
            string id = file.ReadAscii(4);

            if (id != "MTrk")
            {
                throw new MidiException("Bad MTrk header", file.GetOffset() - 4);
            }
            tracklength = file.ReadInt();
            trackend = tracklength + file.GetOffset();

            eventflag = 0;

            while (file.GetOffset() < trackend)
            {
                // If the midi file is truncated here, we can still recover.
                // Just return what we've parsed so far.
                int startoffset, deltatime, channel;
                byte peekevent;
                MidiEvent mEvent;
                try
                {
                    startoffset     = file.GetOffset();
                    deltatime       = file.ReadVarlen();
                    starttime      += deltatime;
                    peekevent       = file.Peek();
                }
                catch (MidiException)
                {
                    return result;
                }

                mEvent              = new MidiEvent();
                mEvent.DeltaTime    = deltatime;
                mEvent.StartTime    = starttime;
                result.Add(mEvent);

                if (peekevent >= MUtil.EventNoteOff)
                {
                    mEvent.HasEventflag = true;
                    eventflag = file.ReadByte();
                }
                channel = 0;
                if (eventflag < byte.MaxValue)
                    channel = eventflag % 16;

                mEvent.EventFlag = (byte)(eventflag - channel);
                switch ((EVENT_TYPE)mEvent.EventFlag)
                {
                    case EVENT_TYPE.NoteOn:
                        mEvent.Channel      = (byte)(channel);
                        mEvent.Notenumber   = file.ReadByte();
                        mEvent.Volume       = file.ReadByte();
                        if (mEvent.Volume > 0)
                        {
                            mEvent.Text = "ON  Ch: " + channel + " key: " + mEvent.Notenumber + " vel: " + mEvent.Volume;
                            mEvent.Type = EVENT_TYPE.NoteOn;
                        }
                        else
                        {
                            mEvent.EventFlag = (byte)(128);
                            mEvent.Text = "OFF Ch: " + channel + " key: " + mEvent.Notenumber + " vel: " + mEvent.Volume;
                            mEvent.Type = EVENT_TYPE.NoteOff;
                        }
                        break;
                    case EVENT_TYPE.NoteOff:
                        mEvent.Channel = (byte)(channel);
                        mEvent.Notenumber = file.ReadByte();
                        mEvent.Volume = file.ReadByte();
                        mEvent.Text = "OFF Ch: " + channel + " key: " + mEvent.Notenumber + " vel: " + mEvent.Volume;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.ChannelAfterTouch:
                        mEvent.Channel = (byte)(channel);
                        mEvent.ChanPressure = file.ReadByte();
                        mEvent.Text = EVENT_TYPE.ChannelAfterTouch + " Ch: " + channel + " pressure: " + mEvent.ChanPressure;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.ControlChange:
                        mEvent.Channel = (byte)(channel);
                        mEvent.ControlNum = file.ReadByte();
                        mEvent.ControlValue = file.ReadByte();
                        mEvent.Text = "CC  Ch: " + channel + " C: " + ((CC)(mEvent.ControlNum)).ToString() + " value: " + mEvent.ControlValue;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.KeyPressure:
                        mEvent.Channel = (byte)(channel);
                        mEvent.Notenumber = file.ReadByte();
                        mEvent.KeyPressure = file.ReadByte();
                        mEvent.Text = EVENT_TYPE.KeyPressure + " Ch: " + channel + " note: " + mEvent.Notenumber + " pressure: " + mEvent.KeyPressure;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.PitchBend:
                        mEvent.Channel = (byte)(channel);
                        mEvent.PitchBend = file.ReadShort();
                        mEvent.Text = EVENT_TYPE.PitchBend + " Ch: " + channel + " PitchBend: " + mEvent.PitchBend;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.ProgramChange:
                        mEvent.Channel = (byte)(channel);
                        mEvent.Instrument = file.ReadByte();
                        mEvent.Text = "PC  Ch: " + channel + " : " + (INSTRUMENT)mEvent.Instrument;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.SysexEvent1:
                        mEvent.MetaLength = file.ReadVarlen();
                        mEvent.Value = file.ReadBytes(mEvent.MetaLength);
                        string val = string.Empty;
                        for (int i = 0; i < mEvent.Value.Length; i++)
                        {
                            val += mEvent.Value[i] + " ";
                        }
                        mEvent.Text = EVENT_TYPE.SysexEvent1 + " length: " + mEvent.MetaLength + " value: " + val;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.SysexEvent2:
                        mEvent.MetaLength = file.ReadVarlen();
                        mEvent.Value = file.ReadBytes(mEvent.MetaLength);
                        mEvent.Text = EVENT_TYPE.SysexEvent2 + " length: " + mEvent.MetaLength + " value: " + mEvent.Value;
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    case EVENT_TYPE.MetaEvent:
                        DefineMetaEvent(mEvent);
                        mEvent.Type = ((EVENT_TYPE)mEvent.EventFlag);
                        break;
                    default:
                        throw new MidiException("Unknown event " + mEvent.EventFlag, file.GetOffset() - 1);
                }
            }

            return result;
        }

        private void DefineMetaEvent(MidiEvent mEvent)
        {
            string result;

            mEvent.EventFlag = MUtil.MetaEvent;
            mEvent.MetaEvent = file.ReadByte();
            mEvent.MetaLength= file.ReadVarlen();
            mEvent.Value = file.ReadBytes(mEvent.MetaLength);
            mEvent.Text = "ME ";//"" + EVENT_TYPE.MetaEvent; //+" " + mEvent.MetaEvent;
            mEvent.Meta = ((META_EVENT)mEvent.MetaEvent);
            result = string.Empty;

            switch ((META_EVENT)mEvent.MetaEvent)
            {
                case META_EVENT.Copyright:
                    result = " Copyright " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.CuePoint:
                    result = " CuePoint " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.DeviceName:
                    result = " DeviceName " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.EndOfTrack:
                    result = " End of track ";
                    break;
                case META_EVENT.Instrument:
                    result = " Instrument " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.KeySignature:
                    result += " KeySignature : " + MUtil.TranslateKeySignature(mEvent.Value);
                    break;
                case META_EVENT.Lyric:
                    result = " Lyric " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.Marker:
                    result = " Marker " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.MIDIChannelPrefix:
                    byte channel = mEvent.Value[0];
                    result = " MIDIChannelPrefix: " + channel;
                    break;
                case META_EVENT.MIDIPort:
                    byte port = mEvent.Value[0];
                    result = " MIDIPort: " + port;
                    break;
                case META_EVENT.ProgramName:
                    result = "  ProgramName " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.Sequence:
                    result = " Sequence " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.SequenceName:
                    result = " SequenceName " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.SequencerSpecificEvent:
                    result = " SequencerSpecific " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.SMPTEOffset:
                    result = " SMPTEOffset " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.Tempo:
                    result = SetMetaEventTempo(mEvent);
                    break;
                case META_EVENT.Text:
                    result = " Text " + System.Text.Encoding.UTF8.GetString(mEvent.Value);
                    break;
                case META_EVENT.TimeSignature:
                    result = " TimeSignature " + SetMetaEventTimeSignature(mEvent);
                    break;
            }

            mEvent.Text += result;
        }

        private string SetMetaEventTempo(MidiEvent mEvent)
        {
            if (mEvent.MetaLength != 3)
            {
                throw new MidiException("ME Tempo len == " + mEvent.MetaLength + " != 3", file.GetOffset());
            }
            mEvent.Tempo = ((mEvent.Value[0] << 16) | (mEvent.Value[1] << 8) | mEvent.Value[2]);

            return " Tempo: " + mEvent.Tempo;
        }

        private string SetMetaEventTimeSignature(MidiEvent mEvent)
        {
            if (mEvent.MetaLength < 2)
            {
                mEvent.Numerator = (byte)0;
                mEvent.Denominator = (byte)4;
            }
            else if (mEvent.MetaLength >= 2 && mEvent.MetaLength < 4)
            {
                mEvent.Numerator = (byte)mEvent.Value[0];
                mEvent.Denominator = (byte)System.Math.Pow(2, mEvent.Value[1]);
            }
            else
            {
                mEvent.Numerator = (byte)mEvent.Value[0];
                mEvent.Denominator = (byte)System.Math.Pow(2, mEvent.Value[1]);
            }

            return mEvent.Numerator + " / " + mEvent.Denominator;
        }

        /** We want note durations to span up to the next note in general.
        * The sheet music looks nicer that way.  In contrast, sheet music
        * with lots of 16th/32nd notes separated by small rests doesn't
        * look as nice.  Having nice looking sheet music is more important
        * than faithfully representing the Midi File data.
        *
        * Therefore, this function rounds the duration of MidiNotes up to
        * the next note where possible.
        */
        public static void RoundDurations(MidiFile midiFile)
        {
            List<MidiTrack> tracks;
            int quarterNote;

            tracks      = midiFile.Tracks;
            quarterNote = midiFile.QuarterNote;
            foreach (MidiTrack track in tracks)
            {
                MidiNote prevNote = null;
                for (int i = 0; i < track.Notes.Count - 1; i++)
                {
                    MidiNote note1 = track.Notes[i];
                    if (prevNote == null)
                    {
                        prevNote = note1;
                    }

                    /* Get the next note that has a different start time */
                    MidiNote note2 = note1;
                    for (int j = i + 1; j < track.Notes.Count; j++)
                    {
                        note2 = track.Notes[j];
                        if (note1.StartTime < note2.StartTime)
                        {
                            break;
                        }
                    }
                    int maxduration = note2.StartTime - note1.StartTime;

                    int dur = 0;
                    if (quarterNote <= maxduration)
                        dur = quarterNote;
                    else if (quarterNote / 2 <= maxduration)
                        dur = quarterNote / 2;
                    else if (quarterNote / 3 <= maxduration)
                        dur = quarterNote / 3;
                    else if (quarterNote / 4 <= maxduration)
                        dur = quarterNote / 4;
                    
                    if (dur < note1.Length)
                    {
                        dur = note1.Length;
                    }

                    /* Special case: If the previous note's duration
                     * matches this note's duration, we can make a notepair.
                     * So don't expand the duration in that case.
                     */
                    if ((prevNote.StartTime + prevNote.Length == note1.StartTime) &&
                        (prevNote.Length == note1.Length))
                    {
                        dur = note1.Length;
                    }
                    note1.Length = dur;
                    if (track.Notes[i + 1].StartTime != note1.StartTime)
                    {
                        prevNote = note1;
                    }
                }
            }
        }

        /** Return true if this track contains multiple channels.
         * If a MidiFile contains only one track, and it has multiple channels,
         * then we treat each channel as a separate track.
         */
        private bool HasMultipleChannels(MidiTrack track)
        {
            int channel = track.Notes[0].Channel;

            MidiNote _note;
            for (int n = 0; n < track.Notes.Count; n++)
            {
                _note = track.Notes[n];
                if (_note.Channel != channel)
                {
                    return true;
                }
            }

            return false;
        }

        /** Split the given track into multiple tracks, separating each
        * channel into a separate track.
        */
        private List<MidiTrack> SplitChannels(MidiFile file, List<MidiEvent> events)
        {
            MidiTrack origtrack = file.Tracks[0];
            /* Find the instrument used for each channel */
            int[] channelInstruments = new int[16];

            MidiEvent m_event;
            for (int e = 0; e < events.Count; e++)
            {
                m_event = events[e];
                if (m_event.EventFlag == MUtil.EventProgramChange)
                {
                    channelInstruments[m_event.Channel] = m_event.Instrument;
                }
            }

            channelInstruments[9] = 128; /* Channel 9 = Percussion */

            List<MidiTrack> result = new List<MidiTrack>();

            MidiNote note;
            for (int n = 0; n < origtrack.Notes.Count; n++)
            {
                bool foundchannel = false;
                note = origtrack.Notes[n];

                MidiTrack track;
                for (int t = 0; t < result.Count; t++)
                {
                    track = result[t];
                    if (note.Channel == track.Notes[0].Channel)
                    {
                        foundchannel = true;
                        track.AddNote(note);
                    }
                }

                if (!foundchannel)
                {
                    track = new MidiTrack(result.Count + 1,MUtil.QuarterNote);
                    track.AddNote(note);
                    track.Instrument = channelInstruments[note.Channel];
                    track.HasNotes = true;
                    result.Add(track);
                    //file.NumberOfMeasures++;
                }
            }


            return result;
        }

        /// <summary>
        /// Check that the MidiNote start times are in increasing order.
        /// This is for debugging purposes.
        /// </summary>
        /// <param name="tracks"></param>
        private void CheckStartTimes(List<MidiTrack> tracks)
        {
            MidiTrack track;
            for (int t = 0; t < tracks.Count; t++)
            {
                track = tracks[t];
                int prevtime = -1;
                MidiNote note;
                for (int n = 0; n < track.Notes.Count; n++)
                {
                    note = track.Notes[n];
                    if (note.StartTime < prevtime)
                    {
                        throw new System.ArgumentException("start times not in increasing order");
                    }
                    prevtime = note.StartTime;
                }
            }
        }
    }
}
