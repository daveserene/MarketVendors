package com.shop.Adapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.shop.it.RowData;
import com.shop.it.R;

public class MyPricesAdapter extends ArrayAdapter<RowData>{
	
	LayoutInflater mInflater;
	HashMap<String, Integer> listviewImages;

	public MyPricesAdapter(Context context, int resource,
			int textViewResourceId, List<RowData> objects, LayoutInflater inflate, HashMap<String, Integer> listImages) {
		super(context, resource, textViewResourceId, objects);
		mInflater = inflate;
		listviewImages = listImages;
		
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		TextView listCommodityName = null;
		TextView listMarketName = null;
		TextView listCommodityPrice = null;
		ImageView commodityImage = null;

		RowData rowData = getItem(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.activity_my_prices_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		listCommodityName = holder.getName();
		listCommodityName.setText(rowData.commodityName);

		listMarketName = holder.getMarket();
		listMarketName.setText(rowData.marketName);

		listCommodityPrice = holder.getPrice();
		listCommodityPrice.setText(rowData.commodityPrice+"/=");

		commodityImage = holder.getImage();
		String productString = listCommodityName.getText().toString().toLowerCase();

		//Loop through loaded images and compare with product name to determine which one to load

		if (listviewImages.containsKey(productString)) {
			commodityImage.setBackgroundResource(listviewImages.get(productString));		
		}


		return convertView;
	}

	public class ViewHolder {
		private View mRow;
		private TextView ctyName = null;
		private TextView mktName = null;
		private TextView ctyPrice = null;
		private ImageView ctyImage = null;

		public ViewHolder(View row) {
			mRow = row;
		}

		public TextView getName() {
			if (null == ctyName) {
				ctyName = (TextView) mRow.findViewById(R.id.commodityTitle);
			}
			return ctyName;
		}

		public TextView getMarket() {
			if (null == mktName) {
				mktName = (TextView) mRow.findViewById(R.id.marketTitle);
			}
			return mktName;
		}

		public TextView getPrice() {
			if (null == ctyPrice) {
				ctyPrice = (TextView) mRow.findViewById(R.id.commodityPrice);
			}
			return ctyPrice;
		}

		public ImageView getImage() {
			if (null == ctyImage) {
				ctyImage = (ImageView) mRow.findViewById(R.id.myPricesImage);
			}
			return ctyImage;
		}
	}

}