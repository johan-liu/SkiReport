package com.app.SkiReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.app.STInfo.STCommentInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.ResolutionSet;

import java.util.ArrayList;

public class ParkCommentItemAdapter extends BaseAdapter {
	Context			mContext = null;
    ParkInfoActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<STCommentInfo> m_commentInfos;

    public ParkCommentItemAdapter(ParkInfoActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STCommentInfo> parkData)
    {
        m_commentInfos = parkData;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
    	if (m_commentInfos == null)
    		return 0;

        return m_commentInfos.size();
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
            convertView = mInflater.inflate(R.layout.parkcommentitem, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLCommentListItemMain));
        } else {
        }

        STCommentInfo parkInfo = null;
        if (m_commentInfos != null)
        {
            parkInfo = m_commentInfos.get(position);
        }

        // Bind the data efficiently with the holder.
        if (parkInfo != null)
        {
            ((TextView)convertView.findViewById(R.id.txtComment)).setText(parkInfo.comment);
            ((TextView)convertView.findViewById(R.id.txtDate)).setText(parkInfo.datetime);
            ((TextView)convertView.findViewById(R.id.txtUserInfo)).setText(parkInfo.userinfo);
        }else
        {
            ((TextView)convertView.findViewById(R.id.txtComment)).setText("");
            ((TextView)convertView.findViewById(R.id.txtDate)).setText("");
            ((TextView)convertView.findViewById(R.id.txtUserInfo)).setText("");
        }

        RelativeLayout rlComment = (RelativeLayout)convertView.findViewById(R.id.RLCommentListItemMain);
        rlComment.setTag(parkInfo);
        rlComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.showCommentDialog((STCommentInfo)view.getTag());
            }
        });

        return convertView;
    }
}