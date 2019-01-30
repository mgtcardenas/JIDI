import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class ByteFileReader
{
	private byte[] data;
	private int    offset;

	public ByteFileReader(String filePath) throws IOException, MidiException
	{
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory())
			throw new MidiException("File " + filePath + " does not exist", 0);
		if (file.length() == 0)
			throw new MidiException("File " + filePath + " is empty (0 bytes)", 0);

		this.data = Files.readAllBytes(file.toPath());
		this.offset = 0;
	}// end ByteFileReader - constructor

	/**
	 * Read the next byte in the file, but don't increment the parse offset
	 */
	public int Peek() throws MidiException
	{
		CheckFileSize(1);

		int x = Byte.toUnsignedInt(data[offset]);
		return x;
	}// end Peek

	/**
	 * Check that the given number of bytes doesn't exceed the file size
	 */
	private void CheckFileSize(int amount) throws MidiException
	{
		if (offset + amount > data.length)
		{
			throw new MidiException("File is truncated", offset);
		}// end if
	}// end CheckFileSize

	/**
	 * Read a 16-bit short from the file
	 */
	public int ReadShort() throws MidiException
	{
		CheckFileSize(2);
		int firstByte  = Byte.toUnsignedInt(data[offset]);
		int secondByte = Byte.toUnsignedInt(data[offset + 1]);
		int result     = (firstByte << 8 | secondByte);
		offset += 2;
		return result;
	}// end ReadShort

	/**
	 * Read a 32-bit int from the file
	 */
	public long ReadInt() throws MidiException
	{
		CheckFileSize(4);
		long firstByte  = Byte.toUnsignedLong(data[offset]);
		long secondByte = Byte.toUnsignedLong(data[offset + 1]);
		long thridByte  = Byte.toUnsignedLong(data[offset + 2]);
		long fourthByte = Byte.toUnsignedLong(data[offset + 3]);
		long result     = (firstByte << 24 | secondByte << 16 | thridByte << 8 | fourthByte);
		offset += 4;
		return result;
	}// end ReadInt

	/**
	 * Read an ascii string with the given length
	 */
	public String ReadAscii(int len) throws MidiException, UnsupportedEncodingException
	{
		CheckFileSize(len);
		byte[] text = new byte[len];
		text = ReadBytes(len);
		String s = new String(text, "UTF-8");
		return s;
	}// end ReadAscii

	/**
	 * Read the given number of bytes from the file
	 */
	public byte[] ReadBytes(int amount) throws MidiException
	{
		CheckFileSize(amount);
		byte[] result = new byte[amount];
		for (int i = 0; i < amount; i++)
		{
			result[i] = data[i + offset];
		}// end for
		offset += amount;
		return result;
	}// end ReadBytes

	/**
	 * Read a variable-length integer (1 to 4 bytes). The integer ends
	 * when you encounter a byte that doesn't have the 8th bit set
	 * (a byte less than 0x80).
	 */
	public int ReadVarlen() throws MidiException
	{
		int result = 0;
		int b;

		b = ReadByte();
		result = (b & 0x7f);

		for (int i = 0; i < 3; i++)
		{
			if ((b & 0x80) != 0)
			{
				b = ReadByte();
				result = ((result << 7) + (b & 0x7f));
			}
			else
			{
				break;
			}// end if-else
		}// end for
		return result;
	}// end ReadVarlen

	/**
	 * Read a byte from the file
	 */
	public int ReadByte() throws MidiException
	{
		CheckFileSize(1);
		int x = Byte.toUnsignedInt(data[offset]);
		offset++;
		return x;
	}// end ReadByte

	/**
	 * Skip over the given number of bytes
	 */
	public void Skip(int amount) throws MidiException
	{
		CheckFileSize(amount);
		offset += amount;
	}// end Skip

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

	public void empty()
	{
		data = new byte[0];
	}// end empty

}// end ByteFileReader