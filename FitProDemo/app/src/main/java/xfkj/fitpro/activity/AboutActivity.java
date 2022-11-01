package xfkj.fitpro.activity;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.util.ArrayList;

import xfkj.fitpro.R;
import xfkj.fitpro.adapter.SettingAdapter;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.view.SettingMenuItem;

public class AboutActivity extends BaseActivity {

    private RecyclerView rlv;
    private String versioin = "";
    private ArrayList<SettingMenuItem> mData;
    private SettingAdapter adapter;

    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle(getString(R.string.about), AboutActivity.this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void initValues() {
        versioin = SDKTools.getLocalVersionName(this);
        mData = new ArrayList<>();
    }

    @Override
    protected void initViews() {
        rlv = findViewById(R.id.rlv);
        mData.add(new SettingMenuItem(R.string.app_version, getString(R.string.app_version), versioin, -1, R.drawable.app_version, 1, false, null));
        adapter = new SettingAdapter(this, mData);
        rlv.setLayoutManager(new LinearLayoutManager(this));
        rlv.setAdapter(adapter);
    }

    @Override
    protected void setViewsListener() {

    }

    @Override
    protected void setViewsFunction() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
