import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MidiFileTest
{
	MidiFile mf;

	@BeforeEach
	void setUp() throws IOException, MidiException
	{
		mf = new MidiFile("tests/c-scale.mid");
	}//end setUp

	@Test
	void hasCorrectFileName()
	{
		assertEquals("c-scale", mf.getFileName());
	}//end hasCorrectFileName

	@Test
	void readsHeaderCorrectly()
	{
		assertEquals(1, mf.getTrackMode());
		assertEquals(2, mf.getNumEventTracks());
		assertEquals(384, mf.getQuarterNote());
	}//end readsHeaderCorrectly

	//TODO: Create test for the whole MIDI File fields values
}//end MidiFileTest - class