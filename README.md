# The MIDI File Format

## Glossary
- __u1__    - _One byte_
- __u2__    - _Two bytes (big endian)_
- __u4__    - _Four bytes (big endian)_
- __varlen__ - _A variable length integer, that can be 1 to 4 bytes. The  
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;integer ends when you encounter a byte that does not   
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;have the 8th bit set (a byte less than 0x80)._
- __len?__   - _The length of the data depends on some code_

## Basic Structure

### Header
The MIDI files begin with the main MIDI Header:
1. __u4__ = _The four ascii characters 'MThd'_
2. __u4__ = _The length of the MThd header = 6 bytes_
3. __u2__ = _0 if the file contains a single track_  
    _&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1 if the file contains one or more simultaneous tracks_  
    _&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2 if the file contains one or more independent tracks_
4. __u2__ = _number of tracks_
5. __u2__ = _if >  0, the number of pulses per quarter note_  
     _&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if <= 0, then ???_

### Tracks
Next come the individual __MIDI tracks__. The total number of MIDI  
tracks was given above, in the MThd header.  Each track starts  
with a header:

1. __u4__ = _The four ascii characters 'MTrk'_
2. __u4__ = _Amount of track data, in bytes._

The track data consists of a series of __MIDI events__.  Each MIDI event  
has the following format:

1. __varlen__  - _The time between the previous event and this event, measured  
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;in "pulses".  The number of pulses per quarter note is given  
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;in the MThd header._
2. __u1__      - _The Event code, always between 0x80 and 0xFF_
3. __len?__    - _The event data.  The length of this data is determined by the  
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;event code.  The first byte of the event data is always < 0x80._

The event code is optional.  If the event code is missing, then it  
defaults to the previous event code.  For example:  

  _varlen, eventcode1, eventdata,_  
  _varlen, eventcode2, eventdata,_  
  _varlen, eventdata,  // eventcode is eventcode2_  
  _varlen, eventdata,  // eventcode is eventcode2_  
  _varlen, eventcode3, eventdata,_  
  _...._

How do you know if the eventcode is there or missing? Well:
  - All event codes are between 0x80 and 0xFF
  - The first byte of eventdata is always less than 0x80.  
  So, after the varlen delta time, if the next byte is between 0x80  
  and 0xFF, its an event code.  Otherwise, its event data.

## Event Codes

__Note Off Event__

    Code:  u1 - 0x80 thru 0x8F  
                0x80 is for channel 1, 0x8F is for channel 16.  
    Data:  u1 - The note number, 0-127.  Middle C is 60 (0x3C)
           u1 - The note velocity.  This should be 0
    
__Note On Event__
 
     Code:  u1 - 0x90 thru 0x9F
                 0x90 is for channel 1, 0x9F is for channel 16.
     Data:  u1 - The note number, 0-127.  Middle C is 60 (0x3C)
            u1 - The note velocity, from 0 (no sound) to 127 (loud).
                 A value of 0 is equivalent to a Note Off.

__Key Pressure__

     Code:  u1 - 0xA0 thru 0xAF
     Data:  u1 - The note number, 0-127.
            u1 - The pressure.
            
__Control Change__

     Code:  u1 - 0xB0 thru 0xBF
     Data:  u1 - The controller number
            u1 - The value
            
__Program Change__

     Code:  u1 - 0xC0 thru 0xCF
     Data:  u1 - The program number.

__Channel Pressure__

     Code:  u1 - 0xD0 thru 0xDF
            u1 - The pressure.

__Pitch Bend__

     Code:  u1 - 0xE0 thru 0xEF
     Data:  u2 - Some data

__Meta Event__

     Code:  u1     - 0xFF
     Data:  u1     - Metacode
            varlen - Length of meta event
            u1varlen] - Meta event data.
            
## Meta Event Codes

__Sequence Number__

    Metacode: u1        - 0x0
              varlen    - 0 or 2
              u1varlen] - Sequence number

__Text__

     Metacode: u1        - 0x1  
               varlen    - Length of text
               u1varlen] - Text

__Copyright__

     Metacode: u1        - 0x2
               varlen    - Length of text
               u1varlen] - Text

__Track Name__

     Metacode: u1        - 0x3  
               varlen    - Length of name
               u1varlen] - Track Name

__Time Signature__

     Metacode: u1         - 0x58
               varlen     - 4 
               u1         - numerator
               u1         - log2(denominator)
               u1         - clocks in metronome click
               u1         - 32nd notes in quarter note (usually 8)

__Key Signature__

     Metacode: u1         - 0x59  
               varlen     - 2
               u1         - if >= 0, then number of sharps
                            if < 0, then number of flats * -1
               u1         - 0 if major key
                            1 if minor key

__Tempo__

     Metacode: u1         - 0x51  
               varlen     - 3  
               u3         - quarter note length in microseconds