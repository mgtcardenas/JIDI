using System;
using System.IO;
using System.Text;

namespace MIDEX
{
    public class ByteFileReader
    {
        private byte[] data;
        private int offset;

        public byte[] Data
        {
            get { return data; }
        }

        public ByteFileReader(string fileName)
        {
            FileInfo info = new FileInfo(fileName);
            if (!info.Exists)
            {
                throw new MidiException("File " + fileName + " does not exist", 0);
            }
            if (info.Length == 0)
            {
                throw new MidiException("File " + fileName + " is empty (0 bytes)", 0);
            }

            data = File.ReadAllBytes(fileName);
            offset = 0;
        }

        /** Check that the given number of bytes doesn't exceed the file size */
        private void CheckFileSize(int amount)
        {
            if (offset + amount > data.Length)
            {
                throw new MidiException("File is truncated", offset);
            }
        }

        /** Read the next byte in the file, but don't increment the parse offset */
        public byte Peek()
        {
            CheckFileSize(1);
            return data[offset];
        }

        /** Read a byte from the file */
        public byte ReadByte()
        {
            CheckFileSize(1);
            byte x = data[offset];
            offset++;
            return x;
        }

        /** Read the given number of bytes from the file */
        public byte[] ReadBytes(int amount)
        {
            CheckFileSize(amount);
            byte[] result = new byte[amount];
            for (int i = 0; i < amount; i++)
            {
                result[i] = data[i + offset];
            }
            offset += amount;
            return result;
        }

        /** Read a 16-bit short from the file */
        public ushort ReadShort()
        {
            CheckFileSize(2);
            ushort x = (ushort)((data[offset] << 8) | data[offset + 1]);
            offset += 2;
            return x;
        }

        /** Read a 32-bit int from the file */
        public int ReadInt()
        {
            CheckFileSize(4);
            int x = (int)((data[offset] << 24) | (data[offset + 1] << 16) |
                           (data[offset + 2] << 8) | data[offset + 3]);
            offset += 4;
            return x;
        }

        /** Read an ascii string with the given length */
        public string ReadAscii(int len)
        {
            CheckFileSize(len);
            string s = ASCIIEncoding.ASCII.GetString(data, offset, len);
            offset += len;
            return s;
        }

        /** Read a variable-length integer (1 to 4 bytes). The integer ends
         * when you encounter a byte that doesn't have the 8th bit set
         * (a byte less than 0x80).
         */
        public int ReadVarlen()
        {
            uint result = 0;
            byte b;

            b = ReadByte();
            result = (uint)(b & 0x7f);

            for (int i = 0; i < 3; i++)
            {
                if ((b & 0x80) != 0)
                {
                    b = ReadByte();
                    result = (uint)((result << 7) + (b & 0x7f));
                }
                else
                {
                    break;
                }
            }
            return (int)result;
        }

        /** Skip over the given number of bytes */
        public void Skip(int amount)
        {
            CheckFileSize(amount);
            offset += amount;
        }

        /** Return the current parse offset */
        public int GetOffset()
        {
            return offset;
        }

        /** Return the raw midi file byte data */
        public byte[] GetData()
        {
            return data;
        }

        public void Empty()
        {
            data = new byte[0];
            GC.Collect();
        }
    }
}
