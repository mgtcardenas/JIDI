﻿using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace MIDEX
{
/* This file contains the classes for parsing and modifying
 * MIDI music files.
 */

/* MIDI file format.
 *
 * The Midi File format is described below.  The description uses
 * the following abbreviations.
 *
 * u1     - One byte
 * u2     - Two bytes (big endian)
 * u4     - Four bytes (big endian)
 * varlen - A variable length integer, that can be 1 to 4 bytes. The 
 *          integer ends when you encounter a byte that doesn't have 
 *          the 8th bit set (a byte less than 0x80).
 * len?   - The length of the data depends on some code
 *          
 *
 * The Midi files begins with the main Midi header
 * u4 = The four ascii characters 'MThd'
 * u4 = The length of the MThd header = 6 bytes
 * u2 = 0 if the file contains a single track
 *      1 if the file contains one or more simultaneous tracks
 *      2 if the file contains one or more independent tracks
 * u2 = number of tracks
 * u2 = if >  0, the number of pulses per quarter note
 *      if <= 0, then ???
 *
 * Next come the individual Midi tracks.  The total number of Midi
 * tracks was given above, in the MThd header.  Each track starts
 * with a header:
 *
 * u4 = The four ascii characters 'MTrk'
 * u4 = Amount of track data, in bytes.
 * 
 * The track data consists of a series of Midi events.  Each Midi event
 * has the following format:
 *
 * varlen  - The time between the previous event and this event, measured
 *           in "pulses".  The number of pulses per quarter note is given
 *           in the MThd header.
 * u1      - The Event code, always betwee 0x80 and 0xFF
 * len?    - The event data.  The length of this data is determined by the
 *           event code.  The first byte of the event data is always < 0x80.
 *
 * The event code is optional.  If the event code is missing, then it
 * defaults to the previous event code.  For example:
 *
 *   varlen, eventcode1, eventdata,
 *   varlen, eventcode2, eventdata,
 *   varlen, eventdata,  // eventcode is eventcode2
 *   varlen, eventdata,  // eventcode is eventcode2
 *   varlen, eventcode3, eventdata,
 *   ....
 *
 *   How do you know if the eventcode is there or missing? Well:
 *   - All event codes are between 0x80 and 0xFF
 *   - The first byte of eventdata is always less than 0x80.
 *   So, after the varlen delta time, if the next byte is between 0x80
 *   and 0xFF, its an event code.  Otherwise, its event data.
 *
 * The Event codes and event data for each event code are shown below.
 *
 * Code:  u1 - 0x80 thru 0x8F - Note Off event.
 *             0x80 is for channel 1, 0x8F is for channel 16.
 * Data:  u1 - The note number, 0-127.  Middle C is 60 (0x3C)
 *        u1 - The note velocity.  This should be 0
 * 
 * Code:  u1 - 0x90 thru 0x9F - Note On event.
 *             0x90 is for channel 1, 0x9F is for channel 16.
 * Data:  u1 - The note number, 0-127.  Middle C is 60 (0x3C)
 *        u1 - The note velocity, from 0 (no sound) to 127 (loud).
 *             A value of 0 is equivalent to a Note Off.
 *
 * Code:  u1 - 0xA0 thru 0xAF - Key Pressure
 * Data:  u1 - The note number, 0-127.
 *        u1 - The pressure.
 *
 * Code:  u1 - 0xB0 thru 0xBF - Control Change
 * Data:  u1 - The controller number
 *        u1 - The value
 *
 * Code:  u1 - 0xC0 thru 0xCF - Program Change
 * Data:  u1 - The program number.
 *
 * Code:  u1 - 0xD0 thru 0xDF - Channel Pressure
 *        u1 - The pressure.
 *
 * Code:  u1 - 0xE0 thru 0xEF - Pitch Bend
 * Data:  u2 - Some data
 *
 * Code:  u1     - 0xFF - Meta Event
 * Data:  u1     - Metacode
 *        varlen - Length of meta event
 *        u1varlen] - Meta event data.
 *
 *
 * The Meta Event codes are listed below:
 *
 * Metacode: u1         - 0x0  Sequence Number
 *           varlen     - 0 or 2
 *           u1varlen] - Sequence number
 *
 * Metacode: u1         - 0x1  Text
 *           varlen     - Length of text
 *           u1varlen] - Text
 *
 * Metacode: u1         - 0x2  Copyright
 *           varlen     - Length of text
 *           u1varlen] - Text
 *
 * Metacode: u1         - 0x3  Track Name
 *           varlen     - Length of name
 *           u1varlen] - Track Name
 *
 * Metacode: u1         - 0x58  Time Signature
 *           varlen     - 4 
 *           u1         - numerator
 *           u1         - log2(denominator)
 *           u1         - clocks in metronome click
 *           u1         - 32nd notes in quarter note (usually 8)
 *
 * Metacode: u1         - 0x59  Key Signature
 *           varlen     - 2
 *           u1         - if >= 0, then number of sharps
 *                        if < 0, then number of flats * -1
 *           u1         - 0 if major key
 *                        1 if minor key
 *
 * Metacode: u1         - 0x51  Tempo
 *           varlen     - 3  
 *           u3         - quarter note length in microseconds
 */

    /** @class MidiFile
 *
 * The MidiFile class contains the parsed data from the Midi File.
 * It contains:
 * - All the tracks in the midi file, including all MidiNotes per track.
 * - The time signature (e.g. 4/4, 3/4, 6/8)
 * - The number of pulses per quarter note.
 * - The tempo (number of microseconds per quarter note).
 *
 * The constructor takes a filename as input, and upon returning,
 * contains the parsed data from the midi file.
 *
 * The methods ReadTrack() and ReadMetaEvent() are helper functions called
 * by the constructor during the parsing.
 *
 * After the MidiFile is parsed and created, the user can retrieve the 
 * tracks and notes by using the property Tracks and Tracks.Notes.
 *
 * There are two methods for modifying the midi data based on the menu
 * options selected:
 *
 * - ChangeMidiNotes()
 *   Apply the menu options to the parsed MidiFile.  
 *   This uses the helper functions:
 *      SplitTrack()
 *      CombineToTwoTracks()
 *      ShiftTime()
 *      Transpose()
 *      RoundStartTimes()
 *      RoundDurations()
 *
 * - ChangeSound()
 *   Apply the menu options to the MIDI music data, and save the modified midi data 
 *   to a file, for playback. 
 *   
 */

    public class MidiFile
    {
        private string filename;          /** The Midi file name */
        private List<MidiEvent>[] events; /** The raw MidiEvents, one list per track */     
        private List<MidiTrack> tracks;   /** The tracks of the midifile that have notes */
        private ushort trackmode;         /** 0 (single track), 1 (simultaneous tracks) 2 (independent tracks) */
        private TimeSignature timeSig;    /** The time signature */
        private int quarternote;          /** The number of pulses per quarter note */
        private int totalPulses;          /** The total length of the song, in pulses */
        private bool trackPerChannel;     /** True if we've split each channel into a track */
        private int numEventTracks;
                
        /** Get the list of tracks */
        public List<MidiTrack> Tracks
        {
            get { return tracks; }
            set { tracks = value; }
        }

        public List<MidiEvent>[] Events
        {
            get { return events; }
            set { events = value; }
        }

        public int QuarterNote
        {
            get { return quarternote; }
            set { quarternote = value; }
        }
        public int NumEventTracks
        {
            get { return numEventTracks; }
            set { numEventTracks = value; }
        }
        public ushort TrackMode
        {
            get { return trackmode; }
            set { trackmode = value; }
        }

        public bool TrackPerChannel
        {
            get { return trackPerChannel; }
            set { trackPerChannel = value; }
        }

        /** Get the time signature */
        public TimeSignature Time
        {
            get { return timeSig; }
            set { timeSig = value; }
        }

        /** Get the file name */
        public string FileName
        {
            get { return filename; }
        }

        /** Get the total length (in pulses) of the song */
        public int TotalPulses
        {
            get { return totalPulses; }
            set { totalPulses = value; }
        }

        /** Create a new MidiFile from the file. */
        public MidiFile(string fileName)
        {
            MidiFileReader omidiFileReader;
            
            this.filename   = Path.GetFileNameWithoutExtension(fileName);
            omidiFileReader = new MidiFileReader(fileName);

            omidiFileReader.ReadFile(this);
        }

        public bool Write(string name,List<MidiEvent>[] events,int quarterSize)
        {
            MidiFileWriter writer = new MidiFileWriter();
            return writer.Write(name, events, quarterSize);
        }
      
        public override string ToString()
        {
            string result = "Midi File tracks=" + tracks.Count + "\n";
            result += Time.ToString() + "\n";
        
            return result;
        }

    } /* End class MidiFile */

}
