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
	void fileDoesNotExist()
	{
		assertThrows(MidiException.class, () ->
		{
			ByteFileReader noByteFileReader = new ByteFileReader("tests/no-file.txt");
		});
	}//end fileDoesNotExist

	@Test
	void isDirectory()
	{
		assertThrows(MidiException.class, () ->
		{
			ByteFileReader noByteFileReader = new ByteFileReader("D:\\INTELLIJ_IDEA_PROJECTS\\JIDI\\tests\\DIRECTORY");
		});
	}//end isDirectory

	@Test
	void openedCorrectly()
	{
		byte[] byteArray = {(byte) 135, (byte) 104, (byte) 101, (byte) 120, (byte) 105, (byte) 110, (byte) 103};
		assertArrayEquals(byteArray, bfr.getData());
		assertEquals(0, bfr.getOffset());
	}//end openedCorrectly
}//end ByteFileReaderTest