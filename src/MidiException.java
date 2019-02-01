public class MidiException extends Exception
{
	private static final long SERIAL_VERSION_UID = 1L;

	public MidiException(String message, int offset)
	{
		super(message + " at offset " + offset);
	}// end MidiFileException - constructor
}//end MidiFileException - class