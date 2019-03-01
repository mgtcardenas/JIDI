import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class MidiTrack
{

	// mover file pointer a campo
	public String          type;
	public int             length;
	public byte[]          data;
	       long            filePointer;
	public List<MidiEvent> trackEvents = new ArrayList<MidiEvent>();

	public MidiTrack(RandomAccessFile raf) throws IOException
	{
		byte[] readType = new byte[4];
		raf.read(readType);
		String text = new String(readType, "UTF-8");

		if (text.equals("MTrk"))
		    this.type = text;

		this.length = raf.readInt();

		filePointer = raf.getFilePointer();

		while ((filePointer + length) != raf.getFilePointer())
		{
			trackEvents.add(new MidiEvent(raf));
		} // end while
	}// end public

	@Override
	public String toString()
	{
		return "Type: " + this.type + "\nLength: " + this.length;
	}// end toString - override
}// end MidiTrack - class

// + "\nData: " + Arrays.toString(data)