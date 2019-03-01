import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class MidiFile
{

	public MidiHeader      headerChunk;
	public List<MidiTrack> trackChunks = new ArrayList<MidiTrack>();

	public MidiFile(RandomAccessFile raf) throws IOException, MidiFileException
	{
		this.headerChunk = new MidiHeader(raf);

		while (raf.length() != raf.getFilePointer())
		{
			trackChunks.add(new MidiTrack(raf));
		} // end while
	}// end MidiFile - constructor

	public static void main(String[] args) throws IOException, FileNotFoundException, MidiFileException
	{
		RandomAccessFile raf    = new RandomAccessFile("sample.mid", "r");
		MidiFile         sample = new MidiFile(raf);
		System.out.println(sample);
	}// end main

	@Override
	         public String toString()
	{
		return "-------------\n" + this.headerChunk + "\n-------------\n" + Arrays.toString(trackChunks.toArray());
	}// end toString - override

}// end MidiFile - class
