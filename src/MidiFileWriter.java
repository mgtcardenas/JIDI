import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

//TODO: Verify this whole class works
public class MidiFileWriter
{
	private static MidiEvent createTempoEvent(int tempo)
	{
		MidiEvent mevent = new MidiEvent();

		mevent.setDeltaTime(0);
		mevent.setStartTime(0);
		mevent.setHasEventflag(true);
		mevent.setEventFlag(0xFF);
		mevent.setMetaEvent (0x51);
		mevent.setMetaLength(   3);
		mevent.setTempo(tempo);

		return mevent;
	}//end createTempoEvent

	/**
	 * Calculate the track length
	 *
	 * @param events - the list of MidiEvents
	 *
	 * @return - the track length in bytes
	 */
	private int getTracLength(List<MidiEvent> events)
	{
		int    length = 0;
		byte[] buffer = new byte[1024];
		for (MidiEvent mevent : events)
		{
			length += varlenToBytes(mevent.getDeltaTime(), buffer, 0);
			length += 1;                                               // for eventflag
			switch (mevent.getEventFlag())
			{
				case MUtil.EventNoteOn:
					length += 2;
					break;
				case MUtil.EventNoteOff:
					length += 2;
					break;
				case MUtil.EventKeyPressure:
					length += 2;
					break;
				case MUtil.EventControlChange:
					length += 2;
					break;
				case MUtil.EventProgramChange:
					length += 1;
					break;
				case MUtil.EventChannelPressure:
					length += 1;
					break;
				case MUtil.EventPitchBend:
					length += 2;
					break;
				case MUtil.SysexEvent1:
				case MUtil.SysexEvent2:
					length += varlenToBytes(mevent.getMetaLength(), buffer, 0);
					length += mevent.getMetaLength();
					break;
				case MUtil.MetaEvent:
					length += 1;
					length += varlenToBytes(mevent.getMetaLength(), buffer, 0);
					length += mevent.getMetaLength();
					break;

				default:
					break;
			}//end switch
		}//end foreach

		return length;
	}//end getTrackLength

	/**
	 * Write a varlen number to the buffer at the given offset
	 *
	 * @param num    - the varlen number
	 * @param buf    - the buffer to write to
	 * @param offset - the offset
	 *
	 * @return - the number of bytes written
	 */
	private int varlenToBytes(int num, byte[] buf, int offset)
	{
		byte b1 = (byte) ((num >> 21) & 0x7F);
		byte b2 = (byte) ((num >> 14) & 0x7F);
		byte b3 = (byte) ((num >> 7 ) & 0x7F);
		byte b4 = (byte) (num         & 0x7F);

		if (b1 > 0)
		{
			buf[offset]     = (byte) (b1 | 0x80);
			buf[offset + 1] = (byte) (b2 | 0x80);
			buf[offset + 2] = (byte) (b3 | 0x80);
			buf[offset + 3] = b4;
			return 4;
		}
		else if (b2 > 0)
		{
			buf[offset]     = (byte) (b2 | 0x80);
			buf[offset + 1] = (byte) (b3 | 0x80);
			buf[offset + 2] = b4;
			return 3;
		}
		else if (b3 > 0)
		{
			buf[offset]     = (byte) (b3 | 0x80);
			buf[offset + 1] = b4;
			return 2;
		}
		else
		{
			buf[offset] = b4;
			return 1;
		}//end if - else x 3
	}//end varlenToBytes

	/**
	 * write a 4-byte int to byte array
	 *
	 * @param value  - the 4-byte int
	 * @param data   - the buffer to write
	 * @param offset - the offset
	 */
	private void intToBytes(int value, byte[] data, int offset)
	{
		data[offset + 0] = (byte) ((value >> 24) & 0xFF);
		data[offset + 1] = (byte) ((value >> 16) & 0xFF);
		data[offset + 2] = (byte) ((value >> 8 ) & 0xFF);
		data[offset + 3] = (byte) (value         & 0xFF);
	}//end intToBytes

	public boolean write(String destfile, List<MidiEvent>[] events, int quarter)
	{
		try
		{
			FileOutputStream stream;
			boolean          result;

			stream = new FileOutputStream(destfile);
			result = writeEvents(stream, events, 1, quarter);

			stream.close();
			return result;
		}
		catch (IOException e)
		{
			return false;
		}//end try - catch
	}//end write

	/**
	 * Write the given list of MidiEvents to a Stream/File.
	 * This method is used for sound playback, for creating new Midi files
	 * with the tempo, transpose, etc changed.
	 *
	 * @param file      - the stream to be written
	 * @param events    - the list of MidiEvents to write
	 * @param trackMode - the track mode of the MIDI file
	 * @param quarter   - the number of pulses per quarter note?
	 *
	 * @return true on success and false on error
	 */
	private boolean writeEvents(FileOutputStream file, List<MidiEvent>[] events, int trackMode, int quarter)
	{
		try
		{
			byte[] buf = new byte[4096];

			// Write the MThd, len = 6, track mode, number tracks, quarter note
			file.write("MThd".getBytes(StandardCharsets.UTF_8), 0, 4);
			intToBytes(6, buf, 0);
			file.write(buf, 0, 4                                    );
			buf[0] = (byte) (trackMode >>    8);
			buf[1] = (byte) (trackMode &  0xFF);
			file.write(buf, 0, 2);
			buf[0] = 0;
			buf[1] = (byte) events.length;
			file.write(buf, 0, 2);
			buf[0] = (byte) (quarter >>    8);
			buf[1] = (byte) (quarter &  0xFF);
			file.write(buf, 0, 2);

			for (List<MidiEvent> list : events)
			{
				// write(); the MTrk header and track length
				file.write("MTrk".getBytes(StandardCharsets.UTF_8), 0, 4);
				int len = getTracLength(list);
				intToBytes(len, buf, 0);
				file.write(buf, 0, 4);

				for (MidiEvent mevent : list)
				{
					int varlen = varlenToBytes(mevent.getDeltaTime(), buf, 0);
					file.write(buf, 0, varlen);

					if   (mevent.getEventFlag() == MUtil.SysexEvent1 || mevent.getEventFlag() == MUtil.SysexEvent2 || mevent.getEventFlag() == MUtil.MetaEvent)
					    buf[0] = (byte) mevent.getEventFlag();
					else
					    buf[0] = (byte) (mevent.getEventFlag() + mevent.getChannel());

					file.write(buf, 0, 1);

					if (mevent.getEventFlag() == MUtil.EventNoteOn)
					{
						buf[0] = (byte) mevent.getNoteNumber();
						buf[1] = (byte) mevent.getVolume();
						file.write(buf, 0, 2);
					}
					else if (mevent.getEventFlag() == MUtil.EventNoteOff)
					{
						buf[0] = (byte) mevent.getNoteNumber();
						buf[1] = (byte) mevent.getVolume();
						file.write(buf, 0, 2);
					}
					else if (mevent.getEventFlag() == MUtil.EventKeyPressure)
					{
						buf[0] = (byte) mevent.getNoteNumber();
						buf[1] = (byte) mevent.getKeyPressure();
						file.write(buf, 0, 2);
					}
					else if (mevent.getEventFlag() == MUtil.EventControlChange)
					{
						buf[0] = (byte) mevent.getControlNum  ();
						buf[1] = (byte) mevent.getControlValue();
						file.write(buf, 0, 2);
					}
					else if (mevent.getEventFlag() == MUtil.EventProgramChange)
					{
						buf[0] = (byte) mevent.getInstrument();
						file.write(buf, 0, 1);
					}
					else if (mevent.getEventFlag() == MUtil.EventChannelPressure)
					{
						buf[0] = (byte) mevent.getChanPressure();
						file.write(buf, 0, 1);
					}
					else if (mevent.getEventFlag() == MUtil.EventPitchBend)
					{
						buf[0] = (byte) (mevent.getPitchBend() >>    8);
						buf[1] = (byte) (mevent.getPitchBend() &  0xFF);
						file.write(buf, 0, 2);
					}
					else if (mevent.getEventFlag() == MUtil.SysexEvent1)
					{
						int offset = varlenToBytes(mevent.getMetaLength(), buf, 0);
						System.arraycopy(mevent.getValue(), 0, buf, offset, mevent.getValue().length);
						file.write(buf, 0, offset + mevent.getValue().length);
					}
					else if (mevent.getEventFlag() == MUtil.SysexEvent2)
					{
						int offset = varlenToBytes(mevent.getMetaLength(), buf, 0);
						System.arraycopy(mevent.getValue(), 0, buf, offset, mevent.getValue().length);
						file.write(buf, 0, offset + mevent.getValue().length);
					}
					else if (mevent.getEventFlag() == MUtil.MetaEvent && mevent.getMetaEvent() == MUtil.MetaEventTempo)
					{
						buf[0] = (byte) mevent.getMetaEvent();
						buf[1] = 3;
						buf[2] = (byte) ((mevent.getTempo() >> 16) & 0xFF);
						buf[3] = (byte) ((mevent.getTempo() >> 8 ) & 0xFF);
						buf[4] = (byte) (mevent.getTempo()         & 0xFF);
						file.write(buf, 0, 5);
					}
					else if (mevent.getEventFlag() == MUtil.MetaEvent)
					{
						buf[0] = (byte) mevent.getMetaEvent();
						int offset = varlenToBytes(mevent.getMetaLength(), buf, 1) + 1;
						System.arraycopy(mevent.getValue(), 0, buf, offset, mevent.getValue().length);
						file.write(buf, 0, offset + mevent.getValue().length);
					}//end if - else x a lot
				}//end foreach
			}//end foreach
			file.close();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}//end try - catch
	}//end writeEvents
}//end MidiFileWriter - class
