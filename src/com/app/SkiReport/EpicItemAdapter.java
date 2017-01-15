package com.app.SkiReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.Global;
import com.app.Utils.ResolutionSet;

import java.util.ArrayList;

public class EpicItemAdapter extends BaseAdapter {
	Context			mContext = null;
	EpicDirtActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<STParkInfo> m_parkInfos = new ArrayList<STParkInfo>();

    public EpicItemAdapter(EpicDirtActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STParkInfo> parkData)
    {
        m_parkInfos = parkData;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
    	if (m_parkInfos == null)
    		return 0;

        return m_parkInfos.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficient to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.parklistitem, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLParkListItemMain));
        } else {
        }

        STParkInfo parkInfo = null;
        if (m_parkInfos != null)
        {
            parkInfo = m_parkInfos.get(position);
        }

        // Bind the data efficiently with the holder.
        if (parkInfo != null)
        {
            String strTemp = "";
            boolean bFahren = AppData.GetTemperState(mContext);
            if (bFahren == false)
                strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(parkInfo.curTemp)) + mContext.getString(R.string.park_tempc);
            else
                strTemp = Integer.toString((int)parkInfo.curTemp) + mContext.getString(R.string.park_tempf);

            ((TextView) convertView.findViewById(R.id.txtName)).setText(parkInfo.name);
            ((TextView)convertView.findViewById(R.id.txtParkWeatherStr)).setText(parkInfo.weatherStr + " " + strTemp);

            int resourceId = Global.GetResourceIdFromState(parkInfo.iconstate);
            if (resourceId != -1)
                ((ImageView)convertView.findViewById(R.id.imgParkWeather)).setImageResource(resourceId);

            int dirtresourceId = Global.GetDirtResourceIdFromState(parkInfo.icondirt);
            if (dirtresourceId != -1)
                ((ImageView)convertView.findViewById(R.id.imgParkState)).setImageResource(dirtresourceId);
            else
                ((ImageView)convertView.findViewById(R.id.imgParkState)).setImageResource(-1);
        }else
        {
            ((TextView)convertView.findViewById(R.id.txtName)).setText("");
            ((TextView)convertView.findViewById(R.id.txtParkWeatherStr)).setText("");
        }

        RelativeLayout llParkItem = (RelativeLayout)convertView.findViewById(R.id.RLParkListItemMain);
        llParkItem.setTag(parkInfo.uid);
        llParkItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//mActivity.showDetailActivity((Integer)v.getTag());
				//mContext.startActivity(new Intent(mContext, BankDetailActivity.class));
                mActivity.showInfoActivity((Integer)v.getTag());
			}
        });
        return convertView;
    }

}