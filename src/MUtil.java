import java.util.Hashtable;

public final class MUtil
{
	public static final String[]                   SCALE                           = {"A", "Bb", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
	/* The list of Midi Events */
	public static final int                        EventNoteOff                    = 0x80;
	public static final int                        EventNoteOn                     = 0x90;
	public static final int                        EventKeyPressure                = 0xA0;
	public static final int                        EventControlChange              = 0xB0;
	public static final int                        EventProgramChange              = 0xC0;
	public static final int                        EventChannelPressure            = 0xD0;
	public static final int                        EventPitchBend                  = 0xE0;
	public static final int                        SysexEvent1                     = 0xF0;
	public static final int                        SysexEvent2                     = 0xF7;
	public static final int                        MetaEvent                       = 0xFF;
	/* The list of Meta Events */
	public static final int                        MetaEventSequence               = 0x0;
	public static final int                        MetaEventText                   = 0x1;
	public static final int                        MetaEventCopyright              = 0x2;
	public static final int                        MetaEventSequenceName           = 0x3;
	public static final int                        MetaEventInstrument             = 0x4;
	public static final int                        MetaEventLyric                  = 0x5;
	public static final int                        MetaEventMarker                 = 0x6;
	public static final int                        MetaEventCuePoint               = 0x7;
	public static final int                        MetaEventProgramName            = 0x8;
	public static final int                        MetaEventDeviceName             = 0x9;
	public static final int                        MetaEventMIDIChannelPrefix      = 0x20;
	public static final int                        MetaEventMIDIPort               = 0x21;
	public static final int                        MetaEventEndOfTrack             = 0x2F;
	public static final int                        MetaEventTempo                  = 0x51;
	public static final int                        MetaEventSMPTEOffset            = 0x54;
	public static final int                        MetaEventTimeSignature          = 0x58;
	public static final int                        MetaEventKeySignature           = 0x59;
	public static final int                        MetaEventSequencerSpecificEvent = 0x7F;
	public static final Hashtable<Integer, String> INSTRUMENT                      = new Hashtable<Integer, String>();
	public static final Hashtable<Integer, String> EVENT_TYPE                      = new Hashtable<Integer, String>();
	public static final Hashtable<Integer, String> META_EVENT                      = new Hashtable<Integer, String>();
	public static final Hashtable<Integer, String> CC                              = new Hashtable<Integer, String>();
	public static       int                        QuarterNote                     = 0;
	public static       int                        whole                           = 0;
	public static       int                        base32                          = 0;
	public static       int                        base64                          = 0;
	public static       int                        WholeValue;
	public static       int                        DottedHalfValue;
	public static       int                        HalfValue;
	public static       int                        DottedQuarterValue;
	public static       int                        QuarterValue;
	public static       int                        DottedEighthValue;
	public static       int                        EighthValue;
	public static       int                        TripletValue;
	public static       int                        SixteenthValue;

	static
	{
		INSTRUMENT.put(0, "Acoustic_Grand_Piano");
		INSTRUMENT.put(1, "Bright_Acoustic_Piano");
		INSTRUMENT.put(2, "Electric_Grand_Piano");
		INSTRUMENT.put(3, "Honky_tonk_Piano");
		INSTRUMENT.put(4, "Electric_Piano_1");
		INSTRUMENT.put(5, "Electric_Piano_2");
		INSTRUMENT.put(6, "Harpsichord");
		INSTRUMENT.put(7, "Clavi");
		INSTRUMENT.put(8, "Celesa");
		INSTRUMENT.put(9, "Glockenspiel");
		INSTRUMENT.put(10, "Music_Box");
		INSTRUMENT.put(11, "Vibraphone");
		INSTRUMENT.put(12, "Marimba");
		INSTRUMENT.put(13, "Xylophone");
		INSTRUMENT.put(14, "Tubular_Bells");
		INSTRUMENT.put(15, "Dulcimer");
		INSTRUMENT.put(16, "Drawbar_Organ");
		INSTRUMENT.put(17, "Percussive_Organ");
		INSTRUMENT.put(18, "Rock_Organ");
		INSTRUMENT.put(19, "Church_Organ");
		INSTRUMENT.put(20, "Reed_Organ");
		INSTRUMENT.put(21, "Accordion");
		INSTRUMENT.put(22, "Harmonica");
		INSTRUMENT.put(23, "Tango_Accordion");
		INSTRUMENT.put(24, "Acoustic_Guitar_nylon");
		INSTRUMENT.put(25, "Acoustic_Guitar_steel");
		INSTRUMENT.put(26, "Electric_Guitar_jazz");
		INSTRUMENT.put(27, "Electric_Guitar_clean");
		INSTRUMENT.put(28, "Electric_Guitar_muted");
		INSTRUMENT.put(29, "Overdriven_Guitar");
		INSTRUMENT.put(30, "Distortion_Guitar");
		INSTRUMENT.put(31, "Guitar_harmonics");
		INSTRUMENT.put(32, "Acoustic_Bass");
		INSTRUMENT.put(33, "Electric_Bass_finger");
		INSTRUMENT.put(34, "Electric_Bass_pick");
		INSTRUMENT.put(35, "Fretless_Bass");
		INSTRUMENT.put(36, "Slap_Bass_1");
		INSTRUMENT.put(37, "Slap_Bass_2");
		INSTRUMENT.put(38, "Synth_Bass_1");
		INSTRUMENT.put(39, "Synth_Bass_2");
		INSTRUMENT.put(40, "Violin");
		INSTRUMENT.put(41, "Viola");
		INSTRUMENT.put(42, "Cello");
		INSTRUMENT.put(43, "Contrabass");
		INSTRUMENT.put(44, "Tremolo_Strings");
		INSTRUMENT.put(45, "Pizzicato_Strings");
		INSTRUMENT.put(46, "Orchestral_Harp");
		INSTRUMENT.put(47, "Timpani");
		INSTRUMENT.put(48, "String_Ensemble_1");
		INSTRUMENT.put(49, "String_Ensemble_2");
		INSTRUMENT.put(50, "SynthString_1");
		INSTRUMENT.put(51, "SynthString_2");
		INSTRUMENT.put(52, "Choir_Aahs");
		INSTRUMENT.put(53, "Voice_Oohs");
		INSTRUMENT.put(54, "Synth_Voice");
		INSTRUMENT.put(55, "Orchestra_Hit");
		INSTRUMENT.put(56, "Trumpet");
		INSTRUMENT.put(57, "Trombone");
		INSTRUMENT.put(58, "Tuba");
		INSTRUMENT.put(59, "Muted_Trumpet");
		INSTRUMENT.put(60, "French_Horn");
		INSTRUMENT.put(61, "Brass_Section");
		INSTRUMENT.put(62, "SynthBrass_1");
		INSTRUMENT.put(63, "SynthBrass_2");
		INSTRUMENT.put(64, "Soprano_Sax");
		INSTRUMENT.put(65, "Alto_Sax");
		INSTRUMENT.put(66, "Tenor_Sax");
		INSTRUMENT.put(67, "Baritone_Sax");
		INSTRUMENT.put(68, "Oboe");
		INSTRUMENT.put(69, "English_Horn");
		INSTRUMENT.put(70, "Basoon");
		INSTRUMENT.put(71, "Clarinet");
		INSTRUMENT.put(72, "Piccolo");
		INSTRUMENT.put(73, "Flute");
		INSTRUMENT.put(74, "Recorder");
		INSTRUMENT.put(75, "Pan_Flute");
		INSTRUMENT.put(76, "Blown_Bottle");
		INSTRUMENT.put(77, "Shakuhachi");
		INSTRUMENT.put(78, "Whistle");
		INSTRUMENT.put(79, "Ocarina");
		INSTRUMENT.put(80, "Lead_1_square");
		INSTRUMENT.put(81, "Lead_2_sawtooth");
		INSTRUMENT.put(82, "Lead_3_calliope");
		INSTRUMENT.put(83, "Lead_4_chiff");
		INSTRUMENT.put(84, "Lead_5_charang");
		INSTRUMENT.put(85, "Lead_6_voice");
		INSTRUMENT.put(86, "Lead_7_fifths");
		INSTRUMENT.put(87, "Lead_8_bass_and_lead");
		INSTRUMENT.put(88, "Pad_1_new_age");
		INSTRUMENT.put(89, "Pad_2_warm");
		INSTRUMENT.put(90, "Pad_3_polysynth");
		INSTRUMENT.put(91, "Pad_4_choir");
		INSTRUMENT.put(92, "Pad_5_bowed");
		INSTRUMENT.put(93, "Pad_6_metallic");
		INSTRUMENT.put(94, "Pad_7_halo");
		INSTRUMENT.put(95, "Pad_8_sweep");
		INSTRUMENT.put(96, "FX_1_rain");
		INSTRUMENT.put(97, "FX_2_soundtrack");
		INSTRUMENT.put(98, "FX_3_crystal");
		INSTRUMENT.put(99, "FX_4_atmosphere");
		INSTRUMENT.put(100, "FX_5_brightness");
		INSTRUMENT.put(101, "FX_6_goblins");
		INSTRUMENT.put(102, "FX_7_echoes");
		INSTRUMENT.put(103, "FX_8_sci_fi");
		INSTRUMENT.put(104, "Sitar");
		INSTRUMENT.put(105, "Banjo");
		INSTRUMENT.put(106, "Shamisen");
		INSTRUMENT.put(107, "Koto");
		INSTRUMENT.put(108, "Kalimba");
		INSTRUMENT.put(109, "Bag_pipe");
		INSTRUMENT.put(110, "Fiddle");
		INSTRUMENT.put(111, "Shanai");
		INSTRUMENT.put(112, "Tinkle_Bell");
		INSTRUMENT.put(113, "Agogo");
		INSTRUMENT.put(114, "Steel_Drums");
		INSTRUMENT.put(115, "Woodblock");
		INSTRUMENT.put(116, "Taiko_Drum");
		INSTRUMENT.put(117, "Melodic_Tom");
		INSTRUMENT.put(118, "Synth_Drum");
		INSTRUMENT.put(119, "Reverse_Cymbal");
		INSTRUMENT.put(120, "Guitar_Fret_Noise");
		INSTRUMENT.put(121, "Breath_Noise");
		INSTRUMENT.put(122, "Seashore");
		INSTRUMENT.put(123, "Bird_Tweet");
		INSTRUMENT.put(124, "Telephone_Ring");
		INSTRUMENT.put(125, "Helicopter");
		INSTRUMENT.put(126, "Applause");
		INSTRUMENT.put(127, "Gushot");
		INSTRUMENT.put(128, "Percussion");
	}// end INSTRUMENT

	static
	{
		EVENT_TYPE.put(0x80, "NoteOff");
		EVENT_TYPE.put(0x90, "NoteOn");
		EVENT_TYPE.put(0xA0, "KeyPressure");// POLYPHONIC AFTER TOUCH
		EVENT_TYPE.put(0xB0, "ControlChange");// FX,VIBRATO,SWITCHES,DEPTH,LOW_RES
		EVENT_TYPE.put(0xC0, "ProgramChange");// Voice Number(INSTRUMENT)
		EVENT_TYPE.put(0xD0, "ChannelAfterTouch");
		EVENT_TYPE.put(0xE0, "PitchBend");
		EVENT_TYPE.put(0xF0, "SysexEvent1");
		EVENT_TYPE.put(0xF7, "SysexEvent2");
		EVENT_TYPE.put(0xFF, "MetaEvent");
	}// end EVENT_TYPE

	static
	{
		META_EVENT.put(0x00, "Sequence");
		META_EVENT.put(0x01, "Text");
		META_EVENT.put(0x02, "Copyright");
		META_EVENT.put(0x03, "SequenceName");
		META_EVENT.put(0x04, "Instrument");
		META_EVENT.put(0x05, "Lyric");
		META_EVENT.put(0x06, "Marker");
		META_EVENT.put(0x07, "CuePoint");
		META_EVENT.put(0x08, "ProgramName");
		META_EVENT.put(0x09, "DeviceName");
		META_EVENT.put(0x20, "MIDIChannelPrefix");
		META_EVENT.put(0x21, "MIDIPort");
		META_EVENT.put(0x2F, "EndOfTrack");
		META_EVENT.put(0x51, "Tempo");
		META_EVENT.put(0x54, "SMPTEOffset");
		META_EVENT.put(0x58, "TimeSignature");
		META_EVENT.put(0x59, "KeySignature");
		META_EVENT.put(0x7F, "SequencerSpecificEvent");
	}

	static
	{
		CC.put(0, "BANK_SELECT_MSB");
		CC.put(1, "MODULATION_WHEEL");
		CC.put(2, "BREATH_CONTROLLER");
		CC.put(4, "FOOT_CONTROLLER");
		CC.put(5, "PORTAMENTO_TIME");// (Except SA and OrganFlutes)
		CC.put(6, "DATA_ENTRY_MSB");
		CC.put(7, "MAIN_VOLUME");
		CC.put(8, "BALANCE");
		CC.put(10, "PANPOT");// L64...C...R63
		CC.put(11, "EXPRESSION");// Expression is a percentage of volume
		CC.put(16, "GENERAL_PURPOSE_C");
		CC.put(32, "BANK_SELECT_LSB");
		CC.put(38, "DATA_ENTRY_LSB");
		CC.put(64, "SUSTAIN_DAMPER");// ON_OFF
		CC.put(65, "PORTAMENTO");// ON_OFF
		CC.put(66, "SOSTENUTO");// ON_OFF
		CC.put(67, "SOFT_PEDAL");// ON_OFF
		CC.put(68, "FOOT_SWITCH");// ON_OFF
		CC.put(69, "HOLD_NOTE");
		CC.put(70, "SOUND_VARIATION");// SOUND VARIATION
		CC.put(71, "HAMRONIC_CONTENT");// RESONANC Ealso(Timbre or Harmonics)
		CC.put(72, "RELEASE_TIME");
		CC.put(73, "ATTACK_TIME");
		CC.put(74, "BRIGHTNESS");
		CC.put(75, "DECAY_TIME");
		CC.put(76, "VIBRATO_RATE");
		CC.put(77, "VIBRATO_DEPTH");
		CC.put(78, "VIBRATO_DELAY");
		CC.put(80, "GPC_ARTICURATION_1");// legato
		CC.put(81, "GPC_ARTICURATION_2");// hold 2
		CC.put(84, "PORTAMENTO_CONTROL");
		CC.put(91, "FX1_REVERB");
		CC.put(92, "FX2_TREMOLO");
		CC.put(93, "FX3_CHORUS");
		CC.put(94, "FX4_VARIATION");
		CC.put(95, "FX5_PHASER");
		CC.put(96, "RPN_INCREMENT");
		CC.put(97, "RPN_DERCEMENT");
		CC.put(98, "RNPNL_LSB");
		CC.put(99, "RNPNL_MSB");
		CC.put(100, "RPN_LSB");
		CC.put(101, "RPN_MSB");
		CC.put(120, "ALL_SOUND_OFF");
		CC.put(121, "RESET_ALL_CONTROLLERS");
		CC.put(122, "LOCAL_CONTROL");
		CC.put(123, "ALL_NOTE_OFF");
		CC.put(124, "OMNI_OFF");
		CC.put(125, "OMNI_ON");
		CC.put(126, "MONO");
		CC.put(127, "POLY");
	}// end CC

	public static void SetQuarterNote(int QuarterNote)
	{
		MUtil.QuarterNote = QuarterNote;

		whole = QuarterNote * 4;
		base32 = whole / 32;
		base64 = whole / 64;

		WholeValue = 28 * MUtil.base32;
		DottedHalfValue = 20 * MUtil.base32;
		HalfValue = 14 * MUtil.base32;
		DottedQuarterValue = 10 * MUtil.base32;
		QuarterValue = 7 * MUtil.base32;
		DottedEighthValue = 5 * MUtil.base32;
		EighthValue = 6 * MUtil.base64;
		TripletValue = 5 * MUtil.base64;
		SixteenthValue = 3 * MUtil.base64;
	}// end SetQuarterNote

}// end MUtil