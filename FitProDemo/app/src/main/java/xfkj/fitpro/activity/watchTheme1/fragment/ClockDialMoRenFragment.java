package xfkj.fitpro.activity.watchTheme1.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legend.bluetooth.fitprolib.model.WatchThemeResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xfkj.fitpro.R;
import xfkj.fitpro.activity.watchTheme1.BaseClockDialActivity;
import xfkj.fitpro.adapter.ClockDialListAdapter;
import xfkj.fitpro.base.NewBaseFragment;
import xfkj.fitpro.view.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockDialMoRenFragment extends NewBaseFragment {

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;

    ClockDialListAdapter mAdapter;
    private List<WatchThemeResponse> mWatchThemes;

    public static NewBaseFragment newInstance() {
        return new ClockDialMoRenFragment();
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_clock_dial_mo_ren;
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
            ((BaseClockDialActivity)getActivity()).loadDetailsData(theme.getId(),false);
        });
    }

    @Override
    public void setData(Object object) {
        mWatchThemes = (List<WatchThemeResponse>) object;
    }
}
