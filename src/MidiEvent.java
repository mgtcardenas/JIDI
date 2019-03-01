import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

public class MidiEvent
{

	// 'Key' = 'NoteNumber'
	// sequenceName, instrumentName, lyrics, marker, cuePoint, deviceName,
	// midiChannelPrefix van en texto
	// ¿Cómo calculas StartTime (the absolute time this event occurs? eso no viene
	// en la guia)
	// Volume es Velocity
	// Instrument = programNum del evento C
	// MIDIPort sólo es un byte. Pero como 01 y luego un byte. Siempre se lee un 
	// LQ
	// y luego VLQ-bytes
	// En Time Signature sólo importan el numerador y denominador. sharpsFlatsNum,
	// majorMinorKey tampoco importan
	// MIDIChannelPrefix y MIDIPort se leen igual

	// #region meta-events
	public short  sequenceNumber;
	public String text;
	public String copyrightNotice;
	public String programName;
	public byte   midiChannelPrefix;
	// #endregion meta-events

	// public byte Notenumber; // The note number
	// public byte Volume; // The volume of the note
	// public byte Instrument; // The instrument

	private static int   sysexLength;

	public         int   deltaTime;    // The time between the previous event and this on
	public         int   startTime;
	public         int   tempo;
	private        int   metaLength;

	public         short pitchBend;    // The pitch bend value [public ushort PitchBend]

	public         byte  eventFlag;    // The type of event
	public         byte  channel;      // The channel this event occurs on
	public         byte  key;
	public         int   velocity;     // es lo mismo que volume

	public         byte  keyPressure;  // The key pressure
	public         byte  chanPressure; // The channel pressure
	public         byte  controlNum;   // The controller number
	public         byte  controlValue; // The controller value
	public         byte  programNum;   // Used to change the instrument (or sound) to be played when a note-on message
	// is received

	public byte   Numerator;     // The numerator, for TimeSignature meta events
	public byte   Denominator;   // The denominator, for TimeSignature meta events
	public byte   metaEvent;     // The metaevent, used if eventflag is MetaEvent
	public byte   sharpsFlatsNum;
	public byte   majorMinorKey;
	public byte[] readText;
	public byte[] value;

	public static int unsignByte(byte b)
	{
		return b & 0xFF;   // Esto regresa un entero por default (&)
	}// end unsignByte

	public static boolean msbEqualsOne(byte b)
	{
		if (128 == (b & 0x80))
		    return true;

		return false;
	}// end msbEqualsOne

	public static int getVLQ(RandomAccessFile raf) throws IOException
	{

		String result    = "";
		String vlqString = "";
		byte   vlq       = raf.readByte();

		while (msbEqualsOne(vlq))
		{
			vlqString = String.format("%8s", Integer.toBinaryString(vlq & 0x7F)).replace(' ', '0');
			result    = result + vlqString.substring(1);
			vlq       = raf.readByte();
		} // end while

		vlqString = String.format("%8s", Integer.toBinaryString(vlq & 0x7F)).replace(' ', '0');
		result    = result + vlqString.substring(1);

		return Integer.parseInt(result, 2);
	}// end getVLQ

	public static byte getMSHex(byte b)
	{
		return (byte) (b & 0xF0);
	}// end getMSB

	public static byte getLSHex(byte b)
	{
		return (byte) (b & 0x0F);
	}// end getMSB

	public MidiEvent(RandomAccessFile raf) throws IOException
	{
		this.deltaTime = getVLQ(raf);
		byte b = raf.readByte();

		switch (getMSHex(b))
		{
			case (byte) 0x80: // Note Off 8n kk vv
				this.eventFlag = getMSHex(b);
				this.channel   = getLSHex(b);
				this.key       = raf.readByte();
				this.velocity  = unsignByte(raf.readByte());
				break;
			case (byte) 0x90:
				this.eventFlag = getMSHex(b);
				this.channel   = getLSHex(b);
				this.key       = raf.readByte();
				this.velocity  = unsignByte(raf.readByte());
				break;
			case (byte) 0xA0:
				this.eventFlag   = getMSHex(b);
				this.channel     = getLSHex(b);
				this.key         = raf.readByte();
				this.keyPressure = raf.readByte();
				break;
			case (byte) 0xB0:
				this.eventFlag    = getMSHex(b);
				this.channel      = getLSHex(b);
				this.controlNum   = raf.readByte();
				this.controlValue = raf.readByte();
				break;
			case (byte) 0xC0:
				this.eventFlag  = getMSHex(b);
				this.channel    = getLSHex(b);
				this.programNum = raf.readByte();
				break;
			case (byte) 0xD0:
				this.eventFlag    = getMSHex(b);
				this.channel      = getLSHex(b);
				this.chanPressure = raf.readByte();
				break;
			case (byte) 0xE0:
				this.eventFlag = getMSHex(b);
				this.channel   = getLSHex(b);
				raf.skipBytes(2);
				break;
			case (byte) 0xF0:

				switch (getLSHex(b))
				{
					case (byte) 0x00:
						this.eventFlag = b;
						sysexLength    = getVLQ(raf);
						raf.skipBytes(sysexLength);
						break;
					case (byte) 0x07:
						this.eventFlag = b;
						sysexLength    = getVLQ(raf);
						raf.skipBytes(sysexLength);
						break;
					case (byte) 0x0F:
						this.eventFlag = b;
						byte metaType = raf.readByte();
						this.metaEvent = metaType;

						switch (metaType)
						{
							case (byte) 0x00:
								System.out.println("Sequence");
								raf.skipBytes(1);
								this.sequenceNumber = raf.readShort();
								break;

							case (byte) 0x01:
								System.out.println("Text");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.text = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x02:
								System.out.println("Copyright");
								metaLength      = getVLQ(raf);
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.copyrightNotice = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x03:
								System.out.println("SequenceName");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.text = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x04:
								System.out.println("Instrument");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.text = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x05:
								System.out.println("Lyric");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.text = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x06:
								System.out.println("Marker");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.text = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x07:
								System.out.println("CuePoint");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.text = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x08:
								System.out.println("ProgramName");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.programName = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x09:
								System.out.println("DeviceName");
								this.metaLength = getVLQ(raf);
								this.readText   = new byte[metaLength];
								raf.read(this.readText);
								this.text = new String(this.readText, "UTF-8");
								break;

							case (byte) 0x20:
								System.out.println("MIDIChannelPrefix");
								raf.skipBytes(1);
								this.midiChannelPrefix = raf.readByte();
								break;

							case (byte) 0x21:
								System.out.println("MIDIPort");
								break;

							case (byte) 0x2F:
								System.out.println("EndOfTrack");
								raf.skipBytes(1);
								break;

							case (byte) 0x51:
								System.out.println("Tempo");
								raf.skipBytes(1);
								this.value = new byte[3];
								raf.read(this.value);
								// NEED TO CHANGE THIS CAUSE THERE IS AN UNDERFLOW
								// MUST USE BIGINTEGER IN THAT CASE
								// TEST IT IN ANOTHER FILE
								BigInteger bi = new BigInteger(value);
								// raf.skipBytes(3);
								this.tempo = bi.intValue();
								break;

							case (byte) 0x54:
								System.out.println("SMPTEOffset");
								raf.skipBytes(6);
								break;

							case (byte) 0x58:
								System.out.println("TimeSignature");
								raf.skipBytes(1);
								this.Numerator   = raf.readByte();
								this.Denominator = raf.readByte();
								raf.skipBytes(2);
								break;

							case (byte) 0x59:
								System.out.println("KeySignature");
								raf.skipBytes(1);
								this.sharpsFlatsNum = raf.readByte();
								this.majorMinorKey  = raf.readByte();
								break;

							case (byte) 0x7F:
								System.out.println("SequencerSpecificEvent");
								this.metaLength = getVLQ(raf);
								raf.skipBytes(this.metaLength);
								break;

							default:
								System.out.println("Unrecognized Event Error");
								break;
						}// end switch
						break;

					default:
						System.out.println("Unrecognized Event Error");
						break;
				}// end switch
		}// end switch
	}// end MidiEvent - constructor

	@Override
	         public String toString()
	{
		return "Delta Time: " + this.deltaTime;
	}// end toString - override
}// end MidiEvent - class

// "\n" + Arrays.toString(data)
