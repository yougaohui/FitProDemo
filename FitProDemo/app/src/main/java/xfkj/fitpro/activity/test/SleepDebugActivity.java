package xfkj.fitpro.activity.test;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.NewBaseActivity;
import xfkj.fitpro.databinding.ActivitySleepDebugBinding;
import xfkj.fitpro.db.SqliteDBAcces;

@SuppressLint("Range")
public class SleepDebugActivity extends NewBaseActivity<ActivitySleepDebugBinding> {

    MyAdapter myAdapter;

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle("睡眠数据");
        myAdapter = new MyAdapter(new ArrayList<>());
        binding.list.setAdapter(myAdapter);
        binding.list.setLayoutManager(new LinearLayoutManager(mContext));

        SqliteDBAcces DBAccess = MyApplication.DBAcces;
        Cursor cursor = DBAccess.Query("select * from Sleep group by LongDate order by LongDate asc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int stype = Integer.valueOf(cursor.getString(cursor.getColumnIndex("SleepTypes")));
                long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(longDate);
                myAdapter.sleepIteList.add(new SleepItem(calendar.getTime(), stype));
            }
            cursor.close();
            myAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initListener() {

    }


    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        List<SleepItem> sleepIteList;

        public MyAdapter(List<SleepItem> sleepIteList) {
            this.sleepIteList = sleepIteList;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_debug_sleep, null);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.tvDate.setText(TimeUtils.date2String(sleepIteList.get(position).date));
            holder.tvSleepStatus.setText("睡眠状态:" + holder.tvSleepStatus);
        }

        @Override
        public int getItemCount() {
            return CollectionUtils.size(sleepIteList);
        }

        public List<SleepItem> getSleepIteList() {
            return sleepIteList;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        TextView tvSleepStatus;
        TextView tvDate;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSleepStatus = itemView.findViewById(R.id.tv_sleep_status);
        }
    }


    private class SleepItem {
        Date date;
        int status;//清醒

        public SleepItem(Date date, int status) {
            this.date = date;
            this.status = status;
        }

        public Date getDate() {
            return date;
        }

        public int getStatus() {
            return status;
        }
    }
}