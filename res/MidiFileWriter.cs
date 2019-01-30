using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace MIDEX
{
    public class MidiFileWriter
    {
        /** Create a new Midi tempo event, with the given tempo  */
        private static MidiEvent CreateTempoEvent(int tempo)
        {
            MidiEvent mevent = new MidiEvent();

            mevent.DeltaTime        = 0;
            mevent.StartTime        = 0;
            mevent.HasEventflag     = true;
            mevent.EventFlag        = (byte)EVENT_TYPE.MetaEvent;
            mevent.MetaEvent        = (byte)META_EVENT.Tempo;
            mevent.MetaLength       = 3;
            mevent.Tempo            = tempo;

            return mevent;
        }

        /** Calculate the track length (in bytes) given a list of Midi events */
        private int GetTrackLength(List<MidiEvent> events)
        {
            int length = 0;
            byte[] buffer = new byte[1024];
            foreach (MidiEvent mevent in events)
            {
                length += VarlenToBytes(mevent.DeltaTime, buffer, 0);
                length += 1;  /* for eventflag */
                switch (mevent.EventFlag)
                {
                    case MUtil.EventNoteOn:             length += 2; break;
                    case MUtil.EventNoteOff:            length += 2; break;
                    case MUtil.EventKeyPressure:        length += 2; break;
                    case MUtil.EventControlChange:      length += 2; break;
                    case MUtil.EventProgramChange:      length += 1; break;
                    case MUtil.EventChannelPressure:    length += 1; break;
                    case MUtil.EventPitchBend:          length += 2; break;
                    case MUtil.SysexEvent1:
                    case MUtil.SysexEvent2:
                        length += VarlenToBytes(mevent.MetaLength, buffer, 0);
                        length += mevent.MetaLength;
                        break;
                    case MUtil.MetaEvent:
                        length += 1;
                        length += VarlenToBytes(mevent.MetaLength, buffer, 0);
                        length += mevent.MetaLength;
                        break;

                    default: break;
                }
            }

            return length;
        }

        /** Write a variable length number to the buffer at the given offset.
         * Return the number of bytes written.
         */
        private int VarlenToBytes(int num, byte[] buf, int offset)
        {
            byte b1 = (byte)((num >> 21) & 0x7F);
            byte b2 = (byte)((num >> 14) & 0x7F);
            byte b3 = (byte)((num >> 7) & 0x7F);
            byte b4 = (byte)(num & 0x7F);

            if (b1 > 0)
            {
                buf[offset] = (byte)(b1 | 0x80);
                buf[offset + 1] = (byte)(b2 | 0x80);
                buf[offset + 2] = (byte)(b3 | 0x80);
                buf[offset + 3] = b4;
                return 4;
            }
            else if (b2 > 0)
            {
                buf[offset] = (byte)(b2 | 0x80);
                buf[offset + 1] = (byte)(b3 | 0x80);
                buf[offset + 2] = b4;
                return 3;
            }
            else if (b3 > 0)
            {
                buf[offset] = (byte)(b3 | 0x80);
                buf[offset + 1] = b4;
                return 2;
            }
            else
            {
                buf[offset] = b4;
                return 1;
            }
        }

        /** Write a 4-byte integer to dataoffset : offset+4] */
        private void IntToBytes(int value, byte[] data, int offset)
        {
            data[offset + 0] = (byte)((value >> 24) & 0xFF);
            data[offset + 1] = (byte)((value >> 16) & 0xFF);
            data[offset + 2] = (byte)((value >> 8) & 0xFF);
            data[offset + 3] = (byte)(value & 0xFF);
        }

        public bool Write(string destfile, List<MidiEvent>[] events, int quarter)
        {
            try
            {
                FileStream stream;
                bool result;

                stream = new FileStream(destfile, FileMode.Create);
                result = WriteEvents(stream, events, 1, quarter);
                
                stream.Close();
                return result;
            }
            catch (IOException)
            {
                return false;
            }
        }

        /// <summary>
        /// Write the given list of Midi events to a stream/file.
        /// This method is used for sound playback, for creating new Midi files
        /// with the tempo, transpose, etc changed.       /// 
        /// </summary>
        /// <param name="file"></param>
        /// <param name="events"></param>
        /// <param name="trackMode"></param>
        /// <param name="quarter"></param>
        /// <returns>true on success, and false on error.</returns>
        private bool WriteEvents(Stream file, List<MidiEvent>[] events, int trackMode, int quarter)
        {
            try
            {
                byte[] buf = new byte[4096];

                /* Write the MThd, len = 6, track mode, number tracks, quarter note */
                file.Write(ASCIIEncoding.ASCII.GetBytes("MThd"), 0, 4);
                IntToBytes(6, buf, 0);
                file.Write(buf, 0, 4);
                buf[0] = (byte)(trackMode >> 8);
                buf[1] = (byte)(trackMode & 0xFF);
                file.Write(buf, 0, 2);
                buf[0] = 0;
                buf[1] = (byte)events.Length;
                file.Write(buf, 0, 2);
                buf[0] = (byte)(quarter >> 8);
                buf[1] = (byte)(quarter & 0xFF);
                file.Write(buf, 0, 2);

                foreach (List<MidiEvent> list in events)
                {
                    /* Write the MTrk header and track length */
                    file.Write(ASCIIEncoding.ASCII.GetBytes("MTrk"), 0, 4);
                    int len = GetTrackLength(list);
                    IntToBytes(len, buf, 0);
                    file.Write(buf, 0, 4);

                    foreach (MidiEvent mevent in list)
                    {
                        int varlen = VarlenToBytes(mevent.DeltaTime, buf, 0);
                        file.Write(buf, 0, varlen);

                        if (mevent.EventFlag == MUtil.SysexEvent1 ||
                            mevent.EventFlag == MUtil.SysexEvent2 ||
                            mevent.EventFlag == MUtil.MetaEvent)
                        {
                            buf[0] = mevent.EventFlag;
                        }
                        else
                        {
                            buf[0] = (byte)(mevent.EventFlag + mevent.Channel);
                        }
                        file.Write(buf, 0, 1);

                        if (mevent.EventFlag == MUtil.EventNoteOn)
                        {
                            buf[0] = mevent.Notenumber;
                            buf[1] = mevent.Volume;
                            file.Write(buf, 0, 2);
                        }
                        else if (mevent.EventFlag == MUtil.EventNoteOff)
                        {
                            buf[0] = mevent.Notenumber;
                            buf[1] = mevent.Volume;
                            file.Write(buf, 0, 2);
                        }
                        else if (mevent.EventFlag == MUtil.EventKeyPressure)
                        {
                            buf[0] = mevent.Notenumber;
                            buf[1] = mevent.KeyPressure;
                            file.Write(buf, 0, 2);
                        }
                        else if (mevent.EventFlag == MUtil.EventControlChange)
                        {
                            buf[0] = mevent.ControlNum;
                            buf[1] = mevent.ControlValue;
                            file.Write(buf, 0, 2);
                        }
                        else if (mevent.EventFlag == MUtil.EventProgramChange)
                        {
                            buf[0] = mevent.Instrument;
                            file.Write(buf, 0, 1);
                        }
                        else if (mevent.EventFlag == MUtil.EventChannelPressure)
                        {
                            buf[0] = mevent.ChanPressure;
                            file.Write(buf, 0, 1);
                        }
                        else if (mevent.EventFlag == MUtil.EventPitchBend)
                        {
                            buf[0] = (byte)(mevent.PitchBend >> 8);
                            buf[1] = (byte)(mevent.PitchBend & 0xFF);
                            file.Write(buf, 0, 2);
                        }
                        else if (mevent.EventFlag == MUtil.SysexEvent1)
                        {
                            int offset = VarlenToBytes(mevent.MetaLength, buf, 0);
                            Array.Copy(mevent.Value, 0, buf, offset, mevent.Value.Length);
                            file.Write(buf, 0, offset + mevent.Value.Length);
                        }
                        else if (mevent.EventFlag == MUtil.SysexEvent2)
                        {
                            int offset = VarlenToBytes(mevent.MetaLength, buf, 0);
                            Array.Copy(mevent.Value, 0, buf, offset, mevent.Value.Length);
                            file.Write(buf, 0, offset + mevent.Value.Length);
                        }
                        else if (mevent.EventFlag == MUtil.MetaEvent && mevent.MetaEvent == MUtil.MetaEventTempo)
                        {
                            buf[0] = mevent.MetaEvent;
                            buf[1] = 3;
                            buf[2] = (byte)((mevent.Tempo >> 16) & 0xFF);
                            buf[3] = (byte)((mevent.Tempo >> 8) & 0xFF);
                            buf[4] = (byte)(mevent.Tempo & 0xFF);
                            file.Write(buf, 0, 5);
                        }
                        else if (mevent.EventFlag == MUtil.MetaEvent)
                        {
                            buf[0] = mevent.MetaEvent;
                            int offset = VarlenToBytes(mevent.MetaLength, buf, 1) + 1;
                            Array.Copy(mevent.Value, 0, buf, offset, mevent.Value.Length);
                            file.Write(buf, 0, offset + mevent.Value.Length);
                        }
                    }
                }
                file.Close();
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        }
    }
}
