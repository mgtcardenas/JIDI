import java.util.Hashtable;

public class MUtil
{
	public static final String[] SCALE = {"A", "Bb", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};

	//region MIDI Events
	public static final int EventNoteOff         = 0x80;
	public static final int EventNoteOn          = 0x90;
	public static final int EventKeyPressure     = 0xA0;
	public static final int EventControlChange   = 0xB0;
	public static final int EventProgramChange   = 0xC0;
	public static final int EventChannelPressure = 0xD0;
	public static final int EventPitchBend       = 0xE0;
	public static final int SysexEvent1          = 0xF0;
	public static final int SysexEvent2          = 0xF7;
	public static final int MetaEvent            = 0xFF;
	//endregion MIDI Events

	//region Meta Events
	public static final int MetaEventSequence               =  0x0;
	public static final int MetaEventText                   =  0x1;
	public static final int MetaEventCopyright              =  0x2;
	public static final int MetaEventSequenceName           =  0x3;
	public static final int MetaEventInstrument             =  0x4;
	public static final int MetaEventLyric                  =  0x5;
	public static final int MetaEventMarker                 =  0x6;
	public static final int MetaEventCuePoint               =  0x7;
	public static final int MetaEventProgramName            =  0x8;
	public static final int MetaEventDeviceName             =  0x9;
	public static final int MetaEventMIDIChannelPrefix      = 0x20;
	public static final int MetaEventMIDIPort               = 0x21;
	public static final int MetaEventEndOfTrack             = 0x2F;
	public static final int MetaEventTempo                  = 0x51;
	public static final int MetaEventSMPTEOffset            = 0x54;
	public static final int MetaEventTimeSignature          = 0x58;
	public static final int MetaEventKeySignature           = 0x59;
	public static final int MetaEventSequencerSpecificEvent = 0x7F;
	//endregion Meta Events

	//region INSTRUMENT_NAME
	public static final Hashtable<Integer, String> INSTRUMENT_NAME = new Hashtable<Integer, String>();

	static
	{
		INSTRUMENT_NAME.put(  0, "Acoustic_Grand_Piano" );
		INSTRUMENT_NAME.put(  1, "Bright_Acoustic_Piano");
		INSTRUMENT_NAME.put(  2, "Electric_Grand_Piano" );
		INSTRUMENT_NAME.put(  3, "Honky_tonk_Piano"     );
		INSTRUMENT_NAME.put(  4, "Electric_Piano_1"     );
		INSTRUMENT_NAME.put(  5, "Electric_Piano_2"     );
		INSTRUMENT_NAME.put(  6, "Harpsichord"          );
		INSTRUMENT_NAME.put(  7, "Clavi"                );
		INSTRUMENT_NAME.put(  8, "Celesa"               );
		INSTRUMENT_NAME.put(  9, "Glockenspiel"         );
		INSTRUMENT_NAME.put( 10, "Music_Box"            );
		INSTRUMENT_NAME.put( 11, "Vibraphone"           );
		INSTRUMENT_NAME.put( 12, "Marimba"              );
		INSTRUMENT_NAME.put( 13, "Xylophone"            );
		INSTRUMENT_NAME.put( 14, "Tubular_Bells"        );
		INSTRUMENT_NAME.put( 15, "Dulcimer"             );
		INSTRUMENT_NAME.put( 16, "Drawbar_Organ"        );
		INSTRUMENT_NAME.put( 17, "Percussive_Organ"     );
		INSTRUMENT_NAME.put( 18, "Rock_Organ"           );
		INSTRUMENT_NAME.put( 19, "Church_Organ"         );
		INSTRUMENT_NAME.put( 20, "Reed_Organ"           );
		INSTRUMENT_NAME.put( 21, "Accordion"            );
		INSTRUMENT_NAME.put( 22, "Harmonica"            );
		INSTRUMENT_NAME.put( 23, "Tango_Accordion"      );
		INSTRUMENT_NAME.put( 24, "Acoustic_Guitar_nylon");
		INSTRUMENT_NAME.put( 25, "Acoustic_Guitar_steel");
		INSTRUMENT_NAME.put( 26, "Electric_Guitar_jazz" );
		INSTRUMENT_NAME.put( 27, "Electric_Guitar_clean");
		INSTRUMENT_NAME.put( 28, "Electric_Guitar_muted");
		INSTRUMENT_NAME.put( 29, "Overdriven_Guitar"    );
		INSTRUMENT_NAME.put( 30, "Distortion_Guitar"    );
		INSTRUMENT_NAME.put( 31, "Guitar_harmonics"     );
		INSTRUMENT_NAME.put( 32, "Acoustic_Bass"        );
		INSTRUMENT_NAME.put( 33, "Electric_Bass_finger" );
		INSTRUMENT_NAME.put( 34, "Electric_Bass_pick"   );
		INSTRUMENT_NAME.put( 35, "Fretless_Bass"        );
		INSTRUMENT_NAME.put( 36, "Slap_Bass_1"          );
		INSTRUMENT_NAME.put( 37, "Slap_Bass_2"          );
		INSTRUMENT_NAME.put( 38, "Synth_Bass_1"         );
		INSTRUMENT_NAME.put( 39, "Synth_Bass_2"         );
		INSTRUMENT_NAME.put( 40, "Violin"               );
		INSTRUMENT_NAME.put( 41, "Viola"                );
		INSTRUMENT_NAME.put( 42, "Cello"                );
		INSTRUMENT_NAME.put( 43, "Contrabass"           );
		INSTRUMENT_NAME.put( 44, "Tremolo_Strings"      );
		INSTRUMENT_NAME.put( 45, "Pizzicato_Strings"    );
		INSTRUMENT_NAME.put( 46, "Orchestral_Harp"      );
		INSTRUMENT_NAME.put( 47, "Timpani"              );
		INSTRUMENT_NAME.put( 48, "String_Ensemble_1"    );
		INSTRUMENT_NAME.put( 49, "String_Ensemble_2"    );
		INSTRUMENT_NAME.put( 50, "SynthString_1"        );
		INSTRUMENT_NAME.put( 51, "SynthString_2"        );
		INSTRUMENT_NAME.put( 52, "Choir_Aahs"           );
		INSTRUMENT_NAME.put( 53, "Voice_Oohs"           );
		INSTRUMENT_NAME.put( 54, "Synth_Voice"          );
		INSTRUMENT_NAME.put( 55, "Orchestra_Hit"        );
		INSTRUMENT_NAME.put( 56, "Trumpet"              );
		INSTRUMENT_NAME.put( 57, "Trombone"             );
		INSTRUMENT_NAME.put( 58, "Tuba"                 );
		INSTRUMENT_NAME.put( 59, "Muted_Trumpet"        );
		INSTRUMENT_NAME.put( 60, "French_Horn"          );
		INSTRUMENT_NAME.put( 61, "Brass_Section"        );
		INSTRUMENT_NAME.put( 62, "SynthBrass_1"         );
		INSTRUMENT_NAME.put( 63, "SynthBrass_2"         );
		INSTRUMENT_NAME.put( 64, "Soprano_Sax"          );
		INSTRUMENT_NAME.put( 65, "Alto_Sax"             );
		INSTRUMENT_NAME.put( 66, "Tenor_Sax"            );
		INSTRUMENT_NAME.put( 67, "Baritone_Sax"         );
		INSTRUMENT_NAME.put( 68, "Oboe"                 );
		INSTRUMENT_NAME.put( 69, "English_Horn"         );
		INSTRUMENT_NAME.put( 70, "Basoon"               );
		INSTRUMENT_NAME.put( 71, "Clarinet"             );
		INSTRUMENT_NAME.put( 72, "Piccolo"              );
		INSTRUMENT_NAME.put( 73, "Flute"                );
		INSTRUMENT_NAME.put( 74, "Recorder"             );
		INSTRUMENT_NAME.put( 75, "Pan_Flute"            );
		INSTRUMENT_NAME.put( 76, "Blown_Bottle"         );
		INSTRUMENT_NAME.put( 77, "Shakuhachi"           );
		INSTRUMENT_NAME.put( 78, "Whistle"              );
		INSTRUMENT_NAME.put( 79, "Ocarina"              );
		INSTRUMENT_NAME.put( 80, "Lead_1_square"        );
		INSTRUMENT_NAME.put( 81, "Lead_2_sawtooth"      );
		INSTRUMENT_NAME.put( 82, "Lead_3_calliope"      );
		INSTRUMENT_NAME.put( 83, "Lead_4_chiff"         );
		INSTRUMENT_NAME.put( 84, "Lead_5_charang"       );
		INSTRUMENT_NAME.put( 85, "Lead_6_voice"         );
		INSTRUMENT_NAME.put( 86, "Lead_7_fifths"        );
		INSTRUMENT_NAME.put( 87, "Lead_8_bass_and_lead" );
		INSTRUMENT_NAME.put( 88, "Pad_1_new_age"        );
		INSTRUMENT_NAME.put( 89, "Pad_2_warm"           );
		INSTRUMENT_NAME.put( 90, "Pad_3_polysynth"      );
		INSTRUMENT_NAME.put( 91, "Pad_4_choir"          );
		INSTRUMENT_NAME.put( 92, "Pad_5_bowed"          );
		INSTRUMENT_NAME.put( 93, "Pad_6_metallic"       );
		INSTRUMENT_NAME.put( 94, "Pad_7_halo"           );
		INSTRUMENT_NAME.put( 95, "Pad_8_sweep"          );
		INSTRUMENT_NAME.put( 96, "FX_1_rain"            );
		INSTRUMENT_NAME.put( 97, "FX_2_soundtrack"      );
		INSTRUMENT_NAME.put( 98, "FX_3_crystal"         );
		INSTRUMENT_NAME.put( 99, "FX_4_atmosphere"      );
		INSTRUMENT_NAME.put(100, "FX_5_brightness"      );
		INSTRUMENT_NAME.put(101, "FX_6_goblins"         );
		INSTRUMENT_NAME.put(102, "FX_7_echoes"          );
		INSTRUMENT_NAME.put(103, "FX_8_sci_fi"          );
		INSTRUMENT_NAME.put(104, "Sitar"                );
		INSTRUMENT_NAME.put(105, "Banjo"                );
		INSTRUMENT_NAME.put(106, "Shamisen"             );
		INSTRUMENT_NAME.put(107, "Koto"                 );
		INSTRUMENT_NAME.put(108, "Kalimba"              );
		INSTRUMENT_NAME.put(109, "Bag_pipe"             );
		INSTRUMENT_NAME.put(110, "Fiddle"               );
		INSTRUMENT_NAME.put(111, "Shanai"               );
		INSTRUMENT_NAME.put(112, "Tinkle_Bell"          );
		INSTRUMENT_NAME.put(113, "Agogo"                );
		INSTRUMENT_NAME.put(114, "Steel_Drums"          );
		INSTRUMENT_NAME.put(115, "Woodblock"            );
		INSTRUMENT_NAME.put(116, "Taiko_Drum"           );
		INSTRUMENT_NAME.put(117, "Melodic_Tom"          );
		INSTRUMENT_NAME.put(118, "Synth_Drum"           );
		INSTRUMENT_NAME.put(119, "Reverse_Cymbal"       );
		INSTRUMENT_NAME.put(120, "Guitar_Fret_Noise"    );
		INSTRUMENT_NAME.put(121, "Breath_Noise"         );
		INSTRUMENT_NAME.put(122, "Seashore"             );
		INSTRUMENT_NAME.put(123, "Bird_Tweet"           );
		INSTRUMENT_NAME.put(124, "Telephone_Ring"       );
		INSTRUMENT_NAME.put(125, "Helicopter"           );
		INSTRUMENT_NAME.put(126, "Applause"             );
		INSTRUMENT_NAME.put(127, "Gushot"               );
		INSTRUMENT_NAME.put(128, "Percussion"           );
	}// end INSTRUMENT_NAME
	//endregion INSTRUMENT_NAME

	//region CONTROL_CHANGE
	public static final Hashtable<Integer, String> CC = new Hashtable<Integer, String>();

	static
	{
		CC.put(  0, "BANK_SELECT_MSB"      );
		CC.put(  1, "MODULATION_WHEEL"     );
		CC.put(  2, "BREATH_CONTROLLER"    );
		CC.put(  4, "FOOT_CONTROLLER"      );
		CC.put(  5, "PORTAMENTO_TIME"      ); // (Except SA and OrganFlutes)
		CC.put(  6, "DATA_ENTRY_MSB"       );
		CC.put(  7, "MAIN_VOLUME"          );
		CC.put(  8, "BALANCE"              );
		CC.put( 10, "PANPOT"               ); // L64...C...R63
		CC.put( 11, "EXPRESSION"           ); // Expression is a percentage of volume
		CC.put( 16, "GENERAL_PURPOSE_C"    );
		CC.put( 32, "BANK_SELECT_LSB"      );
		CC.put( 38, "DATA_ENTRY_LSB"       );
		CC.put( 64, "SUSTAIN_DAMPER"       ); // ON_OFF
		CC.put( 65, "PORTAMENTO"           ); // ON_OFF
		CC.put( 66, "SOSTENUTO"            ); // ON_OFF
		CC.put( 67, "SOFT_PEDAL"           ); // ON_OFF
		CC.put( 68, "FOOT_SWITCH"          ); // ON_OFF
		CC.put( 69, "HOLD_NOTE"            );
		CC.put( 70, "SOUND_VARIATION"      ); // SOUND VARIATION
		CC.put( 71, "HAMRONIC_CONTENT"     ); // RESONANC Ealso(Timbre or Harmonics)
		CC.put( 72, "RELEASE_TIME"         );
		CC.put( 73, "ATTACK_TIME"          );
		CC.put( 74, "BRIGHTNESS"           );
		CC.put( 75, "DECAY_TIME"           );
		CC.put( 76, "VIBRATO_RATE"         );
		CC.put( 77, "VIBRATO_DEPTH"        );
		CC.put( 78, "VIBRATO_DELAY"        );
		CC.put( 80, "GPC_ARTICURATION_1"   ); // legato
		CC.put( 81, "GPC_ARTICURATION_2"   ); // hold 2
		CC.put( 84, "PORTAMENTO_CONTROL"   );
		CC.put( 91, "FX1_REVERB"           );
		CC.put( 92, "FX2_TREMOLO"          );
		CC.put( 93, "FX3_CHORUS"           );
		CC.put( 94, "FX4_VARIATION"        );
		CC.put( 95, "FX5_PHASER"           );
		CC.put( 96, "RPN_INCREMENT"        );
		CC.put( 97, "RPN_DERCEMENT"        );
		CC.put( 98, "RNPNL_LSB"            );
		CC.put( 99, "RNPNL_MSB"            );
		CC.put(100, "RPN_LSB"              );
		CC.put(101, "RPN_MSB"              );
		CC.put(120, "ALL_SOUND_OFF"        );
		CC.put(121, "RESET_ALL_CONTROLLERS");
		CC.put(122, "LOCAL_CONTROL"        );
		CC.put(123, "ALL_NOTE_OFF"         );
		CC.put(124, "OMNI_OFF"             );
		CC.put(125, "OMNI_ON"              );
		CC.put(126, "MONO"                 );
		CC.put(127, "POLY"                 );
	}// end CC
	//endregion CONTROL_CHANGE

	//region QUARTER_NOTE
	public static int QuarterNote        = 0;
	public static int whole              = 0;
	public static int base32             = 0;
	public static int base64             = 0;
	public static int WholeValue;
	public static int DottedHalfValue;
	public static int HalfValue;
	public static int DottedQuarterValue;
	public static int QuarterValue;
	public static int DottedEighthValue;
	public static int EighthValue;
	public static int TripletValue;
	public static int SixteenthValue;

	public static void setQuarterNote(int QuarterNote)
	{
		MUtil.QuarterNote  = QuarterNote;

		whole              = QuarterNote *  4;
		base32             = whole       / 32;
		base64             = whole       / 64;

		WholeValue         = 28          * MUtil.base32;
		DottedHalfValue    = 20          * MUtil.base32;
		HalfValue          = 14          * MUtil.base32;
		DottedQuarterValue = 10          * MUtil.base32;
		QuarterValue       =  7          * MUtil.base32;
		DottedEighthValue  =  5          * MUtil.base32;
		EighthValue        =  6          * MUtil.base64;
		TripletValue       =  5          * MUtil.base64;
		SixteenthValue     =  3          * MUtil.base64;
	}// end setQuarterNote
	//endregion QUARTER_NOTE
}//end MUtil - class