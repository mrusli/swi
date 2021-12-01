package com.pyramix.swi.webui.inventory;

public interface InventoryControl {

	public static final int NEW_INVENTORY_MAX_ROW = 7;

	// for "NEW" inventory - accessing the listbox column
	public static final int IDX_KODE 		= 0;
	public static final int IDX_KETEBALAN 	= 1;
	public static final int IDX_LEBAR 		= 2;
	public static final int IDX_PANJANG 	= 3;
	public static final int IDX_QTY_SL 		= 4;
	public static final int IDX_QTY_KG 		= 5;
	public static final int IDX_PACKING		= 6;
	public static final int IDX_LOKASI 		= 7;
	public static final int IDX_NO_COIL 	= 8;
	public static final int IDX_STATUS		= 9;
}
