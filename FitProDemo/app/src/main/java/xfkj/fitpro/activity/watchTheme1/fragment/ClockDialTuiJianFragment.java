package xfkj.fitpro.activity.watchTheme1.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legend.bluetooth.fitprolib.model.WatchThemeResponse;

import java.util.ArrayList;
import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.activity.watchTheme1.BaseClockDialActivity;
import xfkj.fitpro.adapter.ClockDialListAdapter;
import xfkj.fitpro.base.NewBaseFragment;
import xfkj.fitpro.view.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockDialTuiJianFragment extends NewBaseFragment {

    RecyclerView mRecyclerView;

    ClockDialListAdapter mAdapter;
    private List<WatchThemeResponse> mWatchThemes;

    public static NewBaseFragment newInstance() {
        return new ClockDialTuiJianFragment();
    }

    @Override
    protected void createView(View view) {
        super.createView(view);
        mRecyclerView =  view.findViewById(R.id.RecyclerView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_clock_dial_tui_jian;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (mWatchThemes == null) {
            mWatchThemes = new ArrayList<>();
        }
        mAdapter = new ClockDialListAdapter(mWatchThemes);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(1, 1, 1, 1));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        mAdapter.setOnItemClickListener((view, viewType, data, position) -> {
            WatchThemeResponse theme = (WatchThemeResponse) data;
            ((BaseClockDialActivity)getActivity()).loadDetailsData(theme.getId(),true);
        });
    }

    @Override
    public void setData(Object object) {
        mWatchThemes = (List<WatchThemeResponse>) object;
    }
}
