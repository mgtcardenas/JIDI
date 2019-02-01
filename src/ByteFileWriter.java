import java.io.FileOutputStream;
import java.io.IOException;

public class ByteFileWriter
{
	FileOutputStream fos;

	public ByteFileWriter(String filename) throws IOException
	{
		this.fos = new FileOutputStream(filename, true);
	}//end ByteFileWriter - constructor

	public void writeByte(byte b) throws IOException
	{
		byte[] data = new byte[1];
		data[0] = b;
		fos.write(data, 0, data.length);
		fos.flush();
	}//end writeByte

	public void writeBytes(byte[] data) throws IOException
	{
		fos.write(data, 0, data.length);
		fos.flush();
	}//end writeBytes
}//end ByteFileWriter - class
