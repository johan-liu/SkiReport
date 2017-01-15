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
import com.app.ImgLoader.ImageLoader;
import com.app.STInfo.STNewsInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.Global;
import com.app.Utils.ResolutionSet;
import java.util.ArrayList;
import java.util.List;

public class ParkNewsItemAdapter extends BaseAdapter {
	Context			mContext = null;
    EpicDirtActivity mActivity = null;
    private LayoutInflater mInflater;

    private List<STNewsInfo> m_newsInfos;

    public ParkNewsItemAdapter(EpicDirtActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<STNewsInfo> parkData)
    {
        m_newsInfos = parkData;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
    	if (m_newsInfos == null)
    		return 0;

        return m_newsInfos.size();
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
        if (ImageLoader.imageLoader == null)
        {
            ImageLoader.imageLoader = new ImageLoader();
        }

        ImageLoader.imageLoader.setFileCacheDir(mContext.getCacheDir());

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.parknewsitem, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLParkNewsItemMain));
        } else {
        }

        STNewsInfo newsInfo = null;
        if (m_newsInfos != null)
        {
            newsInfo = m_newsInfos.get(position);
        }

        // Bind the data efficiently with the holder.
        if (newsInfo != null)
        {
            ((TextView)convertView.findViewById(R.id.txtTitle)).setText(newsInfo.title);
            ImageView imgUrl = (ImageView)convertView.findViewById(R.id.imgNews);
            ImageLoader.imageLoader.DisplayImage(newsInfo.imageUrl, imgUrl);
        }else
        {
            ((TextView)convertView.findViewById(R.id.txtTitle)).setText("");
        }

        RelativeLayout llItem = (RelativeLayout)convertView.findViewById(R.id.RLParkNewsItemMain);
        llItem.setTag(newsInfo.link);
        llItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//mActivity.showDetailActivity((Integer)v.getTag());
				//mContext.startActivity(new Intent(mContext, BankDetailActivity.class));
                mActivity.gotoUrl((String)v.getTag());
			}
             });
        return convertView;
    }

}