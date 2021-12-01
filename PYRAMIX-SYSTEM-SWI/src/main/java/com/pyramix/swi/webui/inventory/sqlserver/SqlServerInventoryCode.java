package com.pyramix.swi.webui.inventory.sqlserver;

public enum SqlServerInventoryCode {
	AB, AC, AL, BR, CC, CL, CS, FM, GL, HC, OT, PP, RB, RD, RG, ST;
	
	public Long toId(int value) {
		
		switch (value) {
		// AB - ANGLE BAR
		case 0: return new Long(38);
		// AC - ALUMINIZED
		case 1: return new Long(1);
		// AL - ALUMINIUM
		case 2: return new Long(1);
		// BR - BRASS
		case 3: return new Long(1);
		// CC - SPCC
		case 4: return new Long(58);
		// CL - CANAL C
		case 5: return new Long(1);
		// CS - PPGL
		case 6: return new Long(1);
		// FM - FORMICA
		case 7: return new Long(1);
		// GL - GL
		case 8: return new Long(1);
		// HC - SPHC
		case 9: return new Long(1);
		// OT - OTHERS
		case 10: return new Long(1);
		// PP - PIPE
		case 11: return new Long(1);
		// RB - ROUNDBAR
		case 12: return new Long(1);
		// RD - RD
		case 13: return new Long(1);
		// RG - RENG30
		case 14: return new Long(1);
		// ST - STAINLESS
		case 15: return new Long(1);
		
		default:
			return null;
		}
		
		
	}
}
