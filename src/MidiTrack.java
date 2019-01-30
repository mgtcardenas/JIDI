import java.util.ArrayList;
import java.util.List;

/**
 * @class MidiTrack
 * The MidiTrack takes as input the raw MidiEvents for the track, and
 * gets:
 * - The list of midi notes in the track.
 * - The first instrument used in the track.
 * <p>
 * For each NoteOn event in the midi file, a new MidiNote is created
 * and added to the track, using the AddNote() method.
 * <p>
 * The NoteOff() method is called when a NoteOff event is encountered,
 * in order to update the duration of the MidiNote.
 */

public class MidiTrack
{
	public int             quarterNote;
	public boolean         HasNotes;
	public int             tracknum;    // The track number
	public List<MidiNote>  notes;        // List of Midi notes
	public int             instrument;    // Instrument for this track
	public List<MidiEvent> lyrics;        // The lyrics in this track

	/**
	 * Create an empty MidiTrack. Used by the Clone method
	 */
	public MidiTrack(int tracknum, int quarterNote)
	{
		this.tracknum = tracknum;
		this.notes = new ArrayList<MidiNote>();
		this.quarterNote = quarterNote;
		instrument = 0;
	}// end MidiTrack - constructor

	/*
	 * Create a MidiTrack based on the Midi events. Extract the NoteOn/NoteOff
	 * events to gather the list of MidiNotes.
	 */
	public MidiTrack(List<MidiEvent> events, int tracknum)
	{
		int lyriccount;

		this.tracknum = tracknum;
		notes = new ArrayList<MidiNote>(events.size());
		instrument = 0;

		for (MidiEvent mevent : events)
		{
			if (mevent.EventFlag == MUtil.EventNoteOn && mevent.Volume > 0)
			{
				MidiNote note = new MidiNote(mevent.StartTime, mevent.Channel, mevent.Notenumber, mevent.Volume, 0);
				AddNote(note);
			}
			else if (mevent.EventFlag == MUtil.EventNoteOn && mevent.Volume == 0)
			{
				NoteOff(mevent.Channel, mevent.Notenumber, mevent.StartTime);
				mevent.EventFlag = (byte) (128 + mevent.Channel);
				// mevent.Text = MidiFile.EventName(mevent.EventFlag);
			}
			else if (mevent.EventFlag == (MUtil.EventNoteOff))
			{
				NoteOff(mevent.Channel, mevent.Notenumber, mevent.StartTime);
				// mevent.EventFlag = (byte)(127 + mevent.Channel);
				// mevent.Text = MidiFile.EventName(mevent.EventFlag);
			}
			else if (mevent.EventFlag == MUtil.EventProgramChange)
			{
				instrument = mevent.Instrument;
			}
			else if (mevent.MetaEvent == MUtil.MetaEventLyric)
			{
				if (lyrics == null)
				{
					lyrics = new ArrayList<MidiEvent>();
				}// end if
				lyrics.add(mevent);
			}// end if-else X 4
		}
		if (notes.size() > 0 && notes.get(0).channel == 9)
		{
			instrument = 128; /* Percussion */
		}// end if
		lyriccount = 0;

		if (lyrics != null)
		{
			lyriccount = lyrics.size();
		}// end if
	}// end MidiTrack - constructor 2

	/**
	 * Add a MidiNote to this track. This is called for each NoteOn event
	 */
	public void AddNote(MidiNote m)
	{
		this.notes.add(m);
	}// end AddNote

	/**
	 * A NoteOff event occured. Find the MidiNote of the corresponding
	 * NoteOn event, and update the duration of the MidiNote.
	 */
	public void NoteOff(int channel, int notenumber, int endtime)
	{
		MidiNote note;
		for (int i = notes.size() - 1; i >= 0; i--)
		{
			note = notes.get(i);
			if (note.channel == channel && note.notenumber == notenumber && note.length == 0)
			{
				note.NoteOff(endtime);
				return;
			}// end if
		}// end
	}// end NoteOff

	public static void main(String[] args)
	{
	}// end main

	@Override
	public String toString()
	{
		String result = "Track " + tracknum + " " + getInstrumentName() + "\n";
		// foreach (MidiNote n in notes)
		// {
		// result = result + n + "\n";
		// }
		// result += "End Track\n";
		return result;
	}// end toString

	public String getInstrumentName()
	{
		if (this.instrument >= 0 && this.instrument <= 128)
			return MUtil.INSTRUMENT.get(this.instrument);
		else
			return "";
	}// end getInstrumentName
}// end MidiTrack