import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * "The largest value allowed within a MIDI file is 0x0FFFFFFF (‭268,435,455)‬.
 * This limit is set to allow variable-length quantities to be manipulated as 32-bit integers.""
 *  - The MIDI File Format
 *
 * 2,147,483,647 is a signed integer's max value, so there is no point in using long...
 */
public class ByteFileReader
{
	private byte[] data;
	private int    offset;

	public ByteFileReader(String filePath) throws IOException, MidiException
	{
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory())
		    throw new MidiException("File " + filePath + " does not exist", 0);
		if (file.length() == 0                  )
		    throw new MidiException("File " + filePath + " is empty (0 bytes)", 0);

		this.data   = Files.readAllBytes(file.toPath());
		this.offset = 0;
	}// end ByteFileReader - constructor

	/**
	 * Return the current parse offset
	 */
	public int getOffset()
	{
		return offset;
	}// end getOffset

	/**
	 * Return the raw midi file byte data
	 */
	public byte[] getData()
	{
		return data;
	}// end getData

	/**
	 * Check that the given number of bytes doesn't exceed the file size
	 */
	private void checkFileSize(int amount) throws MidiException
	{
		if (offset + amount > data.length)
		{
			throw new MidiException("File is truncated", offset);
		}// end if
	}// end checkFileSize

	public void empty()
	{
		data = new byte[0];
	}// end empty

	/**
	 * skip over the given number of bytes
	 */
	public void skip(int amount) throws MidiException
	{
		checkFileSize(amount);
		offset += amount;
	}// end skip

	/**
	 * Read the next byte in the file, but don't increment the parse offset
	 */
	public int peek() throws MidiException
	{
		checkFileSize(1);

		int x = Byte.toUnsignedInt(data[offset]);
		return x;
	}// end peek

	/**
	 * Read a byte from the file
	 */
	public int readByte() throws MidiException
	{
		checkFileSize(1);
		int x = Byte.toUnsignedInt(data[offset]);
		offset++;
		return x;
	}// end readByte

	/**
	 * Read a 16-bit short from the file
	 */
	public int readShort() throws MidiException
	{
		checkFileSize(2);
		int firstByte  = Byte.toUnsignedInt(data[offset]    );
		int secondByte = Byte.toUnsignedInt(data[offset + 1]);
		int result     = (firstByte << 8 | secondByte);
		offset += 2;
		return result;
	}// end readShort

	/**
	 * Read a 32-bit int from the file
	 */
	public int readInt() throws MidiException
	{
		checkFileSize(4);
		int firstByte  = Byte.toUnsignedInt(data[offset]    );
		int secondByte = Byte.toUnsignedInt(data[offset + 1]);
		int thirdByte  = Byte.toUnsignedInt(data[offset + 2]);
		int fourthByte = Byte.toUnsignedInt(data[offset + 3]);
		int result     = (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte);
		offset += 4;
		return result;
	}// end readInt

	/**
	 * Read the given number of bytes from the file
	 */
	public byte[] readBytes(int amount) throws MidiException
	{
		checkFileSize(amount);
		byte[] result = new byte[amount];
		System.arraycopy(data, offset, result, 0, amount);
		offset += amount;
		return result;
	}// end readBytes

	/**
	 * Read an ascii string with the given length
	 */
	public String readAscii(int len) throws MidiException
	{
		checkFileSize(len);
		byte[] text = readBytes(len);
		return new String(text, StandardCharsets.UTF_8);
	}// end readAscii

	/**
	 * Read a variable-length integer (1 to 4 bytes). The integer ends
	 * when you encounter a byte that doesn't have the 8th bit set
	 * (a byte less than 0x80). Max value allowed in midi is ‭268,435,455
	 * but 2,147,483,647‬ is a signed ints max value
	 */
	public int readVarlen() throws MidiException
	{
		int result;
		int b;

		b      = readByte();
		result = (b & 0x7f);

		for (int i = 0; i < 3; i++)
		{
			if ((b & 0x80) != 0)
			{
				b      = readByte();
				result = ((result << 7) + (b & 0x7f));
			}
			else
			    break;
		}// end for

		return result;
	}// end readVarlen
}//end ByteFileReader - class