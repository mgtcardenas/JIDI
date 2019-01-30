import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ByteFileReaderTest
{
	ByteFileReader bfr;

	@BeforeEach
	void setUp() throws IOException, MidiException
	{
		bfr = new ByteFileReader("D:\\INTELLIJ_IDEA_PROJECTS\\JIDI\\tests\\test.mid");
	}//end setUp

	@AfterEach
	void tearDown()
	{
	}

	@Test
	void opensCorrectly()
	{
		assertThrows(MidiException.class, () ->
		{
			ByteFileReader noByteFileReader = new ByteFileReader("tests/no-file.txt");
		});

		assertThrows(MidiException.class, () ->
		{
			ByteFileReader noByteFileReader = new ByteFileReader("tests/DIRECTORY");
		});

		byte[] byteArray = {(byte) 135, (byte) 104, (byte) 101, (byte) 120, (byte) 105, (byte) 110, (byte) 103};
		assertArrayEquals(byteArray, bfr.getData());
		assertEquals(0, bfr.getOffset());
	}//end openedCorrectly

	@Test
	void peeks() throws MidiException
	{
		assertEquals(135, bfr.peek());
		assertEquals(0, bfr.getOffset());
	}//end peeks


	@Test
	void readsByte() throws MidiException
	{
		assertEquals(135, bfr.readByte());
		assertEquals(1, bfr.getOffset());
	}//end readsByte

	@Test
	void readsShort() throws MidiException
	{
		assertEquals(34664, bfr.readShort());
		assertEquals(2, bfr.getOffset());
	}//end readsShort

	@Test
	void readsInt() throws MidiException
	{
		assertEquals(2271765880L, bfr.readInt());
		assertEquals(4, bfr.getOffset());
	}//end readsInt

	@Test
	void readsAscii() throws MidiException
	{
		assertEquals("�hexing", bfr.readAscii(7));
	}//end readsAscii

	@Test
	void skips() throws MidiException
	{
		bfr.skip(4);
		assertEquals(4, bfr.getOffset());
	}//end skips

	@Test
	void readsVarlen() throws MidiException
	{
		assertEquals(1000, bfr.readVarlen());
	}//end readsVarlen

	@Test
	void empties()
	{
		byte[] emptyArray = new byte[0];
		bfr.empty();
		assertArrayEquals(emptyArray, bfr.getData());
	}//end empties
}//end ByteFileReaderTest