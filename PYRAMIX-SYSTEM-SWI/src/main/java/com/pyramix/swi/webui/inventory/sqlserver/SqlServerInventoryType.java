package com.pyramix.swi.webui.inventory.sqlserver;

public enum SqlServerInventoryType {

	OT_304_01, ST_304_02, ST_304_03, ST_304_04, ST_304_05, ST_304_06, ST_304_07, ST_304_08, ST_304_09, ST_316_10,
	ST_316_11, ST_420_12, ST_430_13, ST_430_14, ST_430_15, ST_430_16, ST_430_17,
	AL_18, AL_19, AL_20, AL_21, AL_22,
	AC_23,
	AL_24,
	AB_25, AB_26, AB_27,
	BR_28,
	CL_29,
	RA_30,
	FM_31, FM_32, FM_33,
	GL_34, OT_35, OT_36, OT_37,
	CS_38, CS_39,
	AL_40,
	CS_41,
	OT_42, OT_43, OT_44, OT_45, OT_46, OT_47, OT_48, OT_49, OT_50, OT_51, OT_52,
	PP_53,
	CS_54, CS_55, CS_56, CS_57,
	OT_58,
	RA_59,
	RG_60,
	PP_61,
	RB_62,
	HC_63, CC_64, CC_65, CC_66, HC_67, HC_68,
	OT_69, 
	ST_70, 
	OT_71, OT_72, OT_73; 
	
	public Long toId(int value) {
		
		switch (value) {
		// 304 SCH80	28
		case 0: return new Long(28);
		// 304-1F	18
		case 1: return new Long(18);
		// 304-2B	86
		case 2: return new Long(86);
		// 304-2B N4%	29
		case 3: return new Long(29);
		// 304-2B N8% 30
		case 4: return new Long(30);
		// 304-2B-DDQ 20
		case 5: return new Long(20);
		// 304-3F-PVC 21
		case 6: return new Long(21);
		// 304-BA 22
		case 7: return new Long(22);
		// 304-BA-PVC 31
		case 8: return new Long(31);
		// 304-HL 32
		case 9: return new Long(32);
		// 316-1F 13
		case 10: return new Long(13);	
		// 316-L 33
		case 11: return new Long(33);
		// 420-J2-2B 23
		case 12: return new Long(23);
		// 430-2B 24
		case 13: return new Long(24);
		// 430-2B N8% 34
		case 14: return new Long(34);
		// 430-2B-DDQ 35
		case 15: return new Long(35);
		// 430-BA 25
		case 16: return new Long(25);
		// 430-BA-PVC 26
		case 17: return new Long(26);
		// AL-BORDES 8
		case 18: return new Long(8);
		// AL-BORDES2 36
		case 19: return new Long(36);
		// AL-BUNGA 9
		case 20: return new Long(9);
		// AL-FOIL 37
		case 21: return new Long(37);
		// AL-POLOS 82
		case 22: return new Long(82);
		// ALSTAR 4
		case 23: return new Long(4);
		// ALUMINIUM 5
		case 24: return new Long(5);
		// ANGEL BAR 38
		case 25: return new Long(38);
		// ANGEL BAR ( HC) 39
		case 26: return new Long(39);
		// ANGEL BAR (ST) 40
		case 27: return new Long(40);
		// BRASS 6
		case 28: return new Long(6);
		// CANAL C75 79
		case 29: return new Long(79);		
		// FLASHING 97
		case 30: return new Long(97);
		// FORM-UD5 41
		case 31: return new Long(41);
		// FORM-UG5 42
		case 32: return new Long(42);
		// FORM-UG6 43
		case 33: return new Long(43);
		// GALVALUM 76
		case 34: return new Long(76);
		// GALVALUM 90
		case 35: return new Long(90);
		// GALVANIL 44
		case 36: return new Long(44);
		// GALVANIS 10
		case 37: return new Long(10);
		// HIGH GLOSS BLUE 98
		case 38: return new Long(98);
		// HIGH GLOSS RED 99
		case 39: return new Long(99);
		// KULIT JERUK 69
		case 40: return new Long(69);
		// LOW GLOSS BLUE 93
		case 41: return new Long(93);
		// PERFO 304 45
		case 42: return new Long(45);
		// PERFO 316 46
		case 43: return new Long(46);
		// PERFO 430 47
		case 44: return new Long(47);
		// PERFO SPCC 48
		case 45: return new Long(48);
		// PERFO304 45
		case 46: return new Long(45);
		// PIPA 50
		case 47: return new Long(50);
		// PIPA ORNAMEN 51
		case 48: return new Long(51);
		// PIPA SCH10 52
		case 49: return new Long(52);
		// PIPA SCH20 53
		case 50: return new Long(53);
		// PIPA SCH40 54
		case 51: return new Long(54);
		// PIPE 11
		case 52: return new Long(11);
		// PIPE 11
		case 53: return new Long(11);
		// PPGL BLUE 72
		case 54: return new Long(72);
		// PPGL GREEN 73
		case 55: return new Long(73);
		// PPGL RED 71
		case 56: return new Long(71);
		// PPGL WOOD 100
		case 57: return new Long(100);
		// PVC 55
		case 58: return new Long(55);
		// RABUNG 96
		case 59: return new Long(96);
		// RENG30 92
		case 60: return new Long(92);
		// ROUND BAR 56
		case 61: return new Long(56);
		// ROUND BAR 56
		case 62: return new Long(56);
		// SAPH 440 57
		case 63: return new Long(57);
		// SPCC 58
		case 64: return new Long(58);
		// SPCC-SD 1
		case 65: return new Long(1);
		// SPCC-UN 59
		case 66: return new Long(59);
		// SPHC 2
		case 67: return new Long(2);
		// SPHC-PO 3
		case 68: return new Long(3);
		// ST-BORDES 60
		case 69: return new Long(60);
		// ST-BORDES 60
		case 70: return new Long(60);
		// ST-PERFO 61
		case 71: return new Long(61);
		// STRIP 304 62
		case 72: return new Long(62);
		// TEMBAGA 63
		case 73: return new Long(63);
		default:
			return null;
		}
	}
	
	public String toString(int value) {
		
		switch (value) {
		// 304 SCH80
		case 0: return "304 SCH80";
		// 304-1F
		case 1: return "304-1F";
		// 304-2B
		case 2: return "304-2B";
		// 304-2B N4%
		case 3: return "304-2B N4%";
		// 304-2B N8%
		case 4: return "304-2B N8%";
		// 304-2B-DDQ
		case 5: return "304-2B-DDQ";
		// 304-3F-PVC
		case 6: return "304-3F-PVC";
		// 304-BA
		case 7: return "304-BA";
		// 304-BA-PVC
		case 8: return "304-BA-PVC";
		// 304-HL
		case 9: return "304-HL";
		// 316-1F
		case 10: return "316-1F";	
		// 316-L
		case 11: return "316-L";
		// 420-J2-2B
		case 12: return "420-J2-2B";
		// 430-2B
		case 13: return "430-2B";
		// 430-2B N8%
		case 14: return "430-2B N8%";
		// 430-2B-DDQ
		case 15: return "430-2B-DDQ";
		// 430-BA
		case 16: return "430-BA";
		// 430-BA-PVC
		case 17: return "430-BA-PVC";
		// AL-BORDES
		case 18: return "AL-BORDES";
		// AL-BORDES2
		case 19: return "AL-BORDES2";
		// AL-BUNGA
		case 20: return "AL-BUNGA";
		// AL-FOIL
		case 21: return "AL-FOIL";
		// AL-POLOS
		case 22: return "AL-POLOS";
		// ALSTAR
		case 23: return "ALSTAR";
		// ALUMINIUM
		case 24: return "ALUMINIUM";
		// ANGEL BAR
		case 25: return "ANGEL BAR";
		// ANGEL BAR ( HC)
		case 26: return "ANGEL BAR ( HC)";
		// ANGEL BAR (ST)
		case 27: return "ANGEL BAR (ST)";
		// BRASS
		case 28: return "BRASS";
		// CANAL C75
		case 29: return "CANAL C75";		
		// FLASHING
		case 30: return "FLASHING";
		// FORM-UD5
		case 31: return "FORM-UD5";
		// FORM-UG5
		case 32: return "FORM-UG5";
		// FORM-UG6
		case 33: return "FORM-UG6";
		// GALVALUM
		case 34: return "GALVALUM";
		// GALVALUM
		case 35: return "GALVALUM";
		// GALVANIL
		case 36: return "GALVANIL";
		// GALVANIS
		case 37: return "GALVANIS";
		// HIGH GLOSS BLUE
		case 38: return "HIGH GLOSS BLUE";
		// HIGH GLOSS RED
		case 39: return "HIGH GLOSS RED";
		// KULIT JERUK
		case 40: return "KULIT JERUK";
		// LOW GLOSS BLUE
		case 41: return "LOW GLOSS BLUE";
		// PERFO 304
		case 42: return "PERFO 304";
		// PERFO 316
		case 43: return "PERFO 316";
		// PERFO 430
		case 44: return "PERFO 430";
		// PERFO SPCC
		case 45: return "PERFO SPCC";
		// PERFO304
		case 46: return "PERFO304";
		// PIPA
		case 47: return "PIPA";
		// PIPA ORNAMEN
		case 48: return "PIPA ORNAMEN";
		// PIPA SCH10
		case 49: return "PIPA SCH10";
		// PIPA SCH20
		case 50: return "PIPA SCH20";
		// PIPA SCH40
		case 51: return "PIPA SCH40";
		// PIPE
		case 52: return "PIPE";
		// PIPE
		case 53: return "PIPE";
		// PPGL BLUE
		case 54: return "PPGL BLUE";
		// PPGL GREEN
		case 55: return "PPGL GREEN";
		// PPGL RED
		case 56: return "PPGL RED";
		// PPGL WOOD
		case 57: return "PPGL WOOD";
		// PVC
		case 58: return "PVC";
		// RABUNG
		case 59: return "RABUNG";
		// RENG30
		case 60: return "RENG30";
		// ROUND BAR
		case 61: return "ROUND BAR";
		// ROUND BAR
		case 62: return "ROUND BAR";
		// SAPH 440
		case 63: return "SAPH 440";
		// SPCC
		case 64: return "SPCC";
		// SPCC-SD
		case 65: return "SPCC-SD";
		// SPCC-UN
		case 66: return "SPCC-UN";
		// SPHC
		case 67: return "SPHC";
		// SPHC-PO
		case 68: return "SPHC-PO";
		// ST-BORDES
		case 69: return "ST-BORDES";
		// ST-BORDES
		case 70: return "ST-BORDES";
		// ST-PERFO
		case 71: return "ST-PERFO";
		// STRIP 304
		case 72: return "STRIP 304";
		// TEMBAGA
		case 73: return "TEMBAGA";

		default:
			return null;
		}
		
	}
	
}
