package xfkj.fitpro.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xfkj.fitpro.R;
import xfkj.fitpro.view.SettingMenuItem;

/**
 * 设置菜单适配器
 * Created by PQQ on 2017-12-5.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.HolderView> implements View.OnClickListener
{
    private Context mContext;
    public ArrayList<SettingMenuItem> mData;
    private OnItemClickListener listener;

    public interface OnItemClickListener
    {
        void OnItemClick(View view, int position);

    }

    public SettingAdapter(Context context, ArrayList<SettingMenuItem> mdata)
    {
        mContext = context;
        mData = mdata;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mData.get(position).MenuType;
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup parent, int viewType)
    {

        if (viewType == 1 || viewType == 2)
        {
            //常规节点
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_setting, parent, false);
            view.setOnClickListener(this);
            return new HolderView((view));
        } else
        {
            //置空
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_space, parent, false);
            return new HolderView((view));
        }
    }

    @Override
    public void onBindViewHolder(HolderView holder, int position)
    {
        holder.setData(mData.get(position));
        holder.setPosition(position);
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener l)
    {
        this.listener = l;
    }

    @Override
    public void onClick(View v)
    {
        if (listener == null)
            return;
        listener.OnItemClick(v, (Integer) v.getTag());
    }

    class HolderView extends RecyclerView.ViewHolder
    {
        private TextView labName;
        private TextView labVersion;
        private ImageView iv;
        private ImageView iv_Left;
        private View hline;

        public HolderView(View itemView)
        {
            super(itemView);
            labName = itemView.findViewById(R.id.labMenuName);
            labVersion = itemView.findViewById(R.id.labVersion);
            iv = itemView.findViewById(R.id.iv);
            iv_Left = itemView.findViewById(R.id.iv_Left);
            hline = itemView.findViewById(R.id.hline);
        }

        public void setData(SettingMenuItem data)
        {
            if (data.MenuType == 1 || data.MenuType == 2)
            {
                labName.setText(data.Name);
                if (data.Resource == -1){
                    iv.setVisibility(View.INVISIBLE);
                }else{
                    iv.setVisibility(View.VISIBLE);
                    iv.setImageResource(data.Resource);
                }
                labVersion.setText(data.NameInfo);
                iv_Left.setImageResource(data.BgResource);
                hline.setVisibility(data.isHasDivision == true ? View.VISIBLE : View.GONE);
                if (data.MenuType == 2)
                    labName.setTextColor(Color.RED);
            }
        }

        public void setPosition(int position)
        {
            this.itemView.setTag(position);
        }
    }
}
