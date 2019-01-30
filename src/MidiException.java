public class MidiException extends Exception
{
	private static final long serialVersionUID = 1L;

	public MidiException(String message, int offset)
	{
		super(message + " at offset " + offset);
	}// end MidiFileException - constructor
}// end MidiFileException