package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.utils.DeleteFileUtil.deleteDirectory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.view.MyListView;

public class LogsActivity extends BaseActivity {


    private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
    private MyListView listView;
    List<String> lists;
    LogsAdapter adapter;
    @Override
    protected void setActivityTitle() {
        initTitle();
        ImageView del_btn = setTitle("日志",LogsActivity.this,R.drawable.close);
        del_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LogsActivity.this);
                dialog.setTitle(getString(R.string.tips_txt));
                dialog.setMessage("是否清空所有日志");
                dialog.setNeutralButton(getString(R.string.cancel_txt), null);
                dialog.setPositiveButton(getString(R.string.confirm_txt), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //文件缓存多清理几次
                        deleteDirectory(SDKTools.RootPath+"/Logcat");
                        lists = new ArrayList();
                        adapter = new LogsAdapter(lists);
                        listView.setAdapter(adapter);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_logs);
    }

    @Override
    protected void initValues() {
        lists = new ArrayList();
    }

    @Override
    protected void initViews() {
        listView = findViewById(R.id.logs_list_view);
        lists =  Txt();
        if (lists.isEmpty()){
            return;
        }
        adapter = new LogsAdapter(lists);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setViewsListener() {

    }

    @Override
    protected void setViewsFunction() {

    }


    public List<String> Txt() {
        //将读出来的一行行数据使用List存储
        String filePath = SDKTools.RootPath+"/Logcat/"+"logcat-" + simpleDateFormat1.format(new Date()) + ".log";

        List newList=new ArrayList<String>();
        try {
            File file = new File(filePath);
            int count = 0;//初始化 key值
            if (file.isFile() && file.exists()) {//文件存在
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    if (!"".equals(lineTxt)) {
                        String reds = lineTxt.split("\\+")[0];  //java 正则表达式
                        newList.add(count, reds);
                        count++;
                    }
                }
                isr.close();
                br.close();
            }else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newList;
    }


    public class LogsAdapter extends BaseAdapter
    {
        private List<String> list;//数据源

        public LogsAdapter(List list)
        {
            this.list = list;
        }

        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public Object getItem(int position)
        {
            return list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View view2, ViewGroup viewGroup)
        {
            View view = LayoutInflater.from(LogsActivity.this).inflate(R.layout.logs_item, viewGroup, false);
            TextView detail = (TextView) view.findViewById(R.id.details);//获取该布局内的文本视图
            detail.setText(list.get(position).toString());
            return view;
        }
    }

}
