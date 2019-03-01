import java.io.IOException;
import java.io.RandomAccessFile;

public class MidiHeader
{

	String type;
	int    length;
	short  format;
	short  tracks;
	short  division;

	public MidiHeader(RandomAccessFile raf) throws IOException, MidiFileException
	{

		try
		{
			byte[] readType = new byte[4];
			raf.read(readType);
			String text = new String(readType, "UTF-8");

			if (text.equals("MThd"))
			{
				this.type = text;
			}
			else
			{
				throw new MidiFileException("Header Error");
			} // end if-else

			this.length   = raf.readInt  ();
			this.format   = raf.readShort();
			this.tracks   = raf.readShort();
			this.division = raf.readShort();

			// Honour the length field
			raf.skipBytes(length - 6);
		}
		catch (MidiFileException e)
		{
			e.printStackTrace();
		} // end try-catch

	}// MidiHeader

	@Override
	         public String toString()
	{
		return "Type: " + this.type + "\nLength: " + this.length + "\nFormat: " + this.format + "\nTracks: " + this.tracks + "\nDivision: " + this.division;
	}// end toString - override

}// end MidiHeader - class
