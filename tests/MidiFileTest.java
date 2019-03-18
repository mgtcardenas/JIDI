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
		assertEquals(1536,mf.getTotalPulses()); // has correct number of total pulses
		assertEquals(2,mf.getNumEventTracks()); // correctly identifies num of event tracks
	}//end readsHeaderCorrectly

	@Test
	void hasCorrectTimeSignature()
	{
		assertEquals(88,mf.getEvents()[0].get(0).getMetaEvent());
		assertEquals(4,mf.getEvents()[0].get(0).getMetaLength());
		assertEquals(4,mf.getEvents()[0].get(0).getNumerator());
		assertEquals(4,mf.getEvents()[0].get(0).getDenominator());
	}//end hasCorrectTimeSignature

	@Test
	void hasCorrectTempo()
	{
		assertEquals(81,mf.getEvents()[0].get(1).getMetaEvent());
		assertEquals(3,mf.getEvents()[0].get(1).getMetaLength()); // always 3
		assertEquals(545454,mf.getEvents()[0].get(1).getTempo());
	}//end hasCorrectTempo
}//end MidiFileTest - class