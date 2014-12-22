package com.shop.it;

public class RowData {
	public int rowId;
	public String vendorName;
	public String marketName;
	public String commodityName;
	public String commodityPrice;
	public String unit;
	public String imageurl;

	RowData(int id, String vname, String mName, String cName, String price, String quantity) {
		rowId = id;
		vendorName = vname;
		marketName = mName;
		commodityName = cName;
		commodityPrice = price;
		unit = quantity;

	}

	@Override
	public String toString() {
		return rowId + " " + vendorName + " " + marketName + " "+ commodityName+ " "+ commodityPrice;
	}
}
