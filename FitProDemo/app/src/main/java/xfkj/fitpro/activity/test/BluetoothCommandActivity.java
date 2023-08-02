package xfkj.fitpro.activity.test;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.utils.FitProSpUtils;

import java.util.Arrays;
import java.util.Random;

import xfkj.fitpro.R;
import xfkj.fitpro.base.NewBaseActivity;
import xfkj.fitpro.databinding.ActivityBluetoothCommandBinding;
import xfkj.fitpro.utils.weather.Weather2Response;
import xfkj.fitpro.utils.weather.WeatherForecastResponse;
import xfkj.fitpro.utils.weather.WeatherProxy;

public class BluetoothCommandActivity extends NewBaseActivity<ActivityBluetoothCommandBinding> {

    int weatherType = 0;
    private Weather2Response mWeather2Model;
    private WeatherForecastResponse mWeather3Model;

    @Override
    public void initData(Bundle savedInstanceState) {
        String content = ResourceUtils.readAssets2String("weather.json");
        String content2 = ResourceUtils.readAssets2String("weather1.json");
        mWeather2Model = GsonUtils.fromJson(content, Weather2Response.class);
        mWeather3Model = GsonUtils.fromJson(content2, WeatherForecastResponse.class);
    }

    @Override
    public void initListener() {

    }

    public void onMBtnWeatherClicked(View view) {
        int[] minMax = getRandomTmp();
        senWeatherData((byte) minMax[1], (byte) minMax[0]);
    }

    private int[] getRandomTmp() {
        int[] tmps = new int[2];
        int type1 = new Random().nextInt(2);
        int type2 = new Random().nextInt(2);
        int tmp1 = new Random().nextInt(50);
        int tmp2 = new Random().nextInt(50);
        int max, min;
        if (tmp1 == tmp2) {
            tmp1 = tmp1 + 1;
        }

        if (type1 == 0) {
            tmp1 = 0 - tmp1;
        }

        if (type2 == 0) {
            tmp2 = 0 - tmp2;
        }

        max = (tmp1 > tmp2 ? tmp1 : tmp2);
        min = (tmp1 < tmp2 ? tmp1 : tmp2);
        tmps[0] = min;
        tmps[1] = max;
        return tmps;
    }


    public void onMBtnWeatherClicked2(View view) {
        String tmp1 = binding.edtTmp1.getText().toString();
        String tmp2 = binding.edtTmp2.getText().toString();
        if (StringUtils.isTrimEmpty(tmp1)) {
            ToastUtils.showShort("请输入天气一");
            return;
        }

        if (StringUtils.isTrimEmpty(tmp2)) {
            ToastUtils.showShort("请输入天气二");
            return;
        }

        int tmp1Num;
        int tmp2Num;
        try {
            tmp1Num = Integer.valueOf(tmp1);
            tmp2Num = Integer.valueOf(tmp2);
        } catch (NumberFormatException e) {
            ToastUtils.showShort("请输入合法的天气");
            return;
        }
        senWeatherData((byte) tmp1Num, (byte) tmp2Num);
    }

    private void senWeatherData(byte tmp1, byte tmp2) {
        if (weatherType > 18) weatherType = 0;
        byte[] value = {tmp1, tmp2, (byte) (weatherType++), 0};
        ToastUtils.showShort(Arrays.toString(value));
        mWeather3Model.setTmpMin(tmp1 + "");
        mWeather3Model.setTmpMax(tmp2 + "");
        mWeather3Model.setCondCodeDay(weatherType + "");
        write(mWeather3Model, "模拟天气1");
    }

    private void write(WeatherForecastResponse data, String description) {
        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }

        if (!FitProSpUtils.isSupportWeather()) {
            ToastUtils.showShort("不支持天气");
            return;
        }

        WeatherProxy.syncWeather(data, true);
    }

    public void onMBtnWeatherClicked22(View view) {
        String tmp1 = binding.edtTmp1.getText().toString();
        String tmp2 = binding.edtTmp2.getText().toString();
        if (StringUtils.isTrimEmpty(tmp1)) {
            ToastUtils.showShort("请输入天气一");
            return;
        }

        if (StringUtils.isTrimEmpty(tmp2)) {
            ToastUtils.showShort("请输入天气二");
            return;
        }

        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }

        int tmp1Num;
        int tmp2Num;
        try {
            tmp1Num = Integer.valueOf(tmp1);
            tmp2Num = Integer.valueOf(tmp2);
        } catch (NumberFormatException e) {
            ToastUtils.showShort("请输入合法的天气");
            return;
        }
        sendWeahter2Moni(tmp1Num, tmp2Num);
    }

    public void onMBtnWeatherClicked221(View view) {
        sendWeahter2Moni(-1000, -1000);
    }

    private void sendWeahter2Moni(int tmp1, int tmp2) {
        if (weatherType > 18) weatherType = 0;
        weatherType++;
        if (mWeather2Model != null && !CollectionUtils.isEmpty(mWeather2Model.getList())) {
            for (Weather2Response.ListDTO listDTO : mWeather2Model.getList()) {
                int[] minMax = getRandomTmp();
                if (tmp1 == -1000 && tmp2 == -1000) {
                    tmp1 = minMax[0];
                    tmp2 = minMax[1];
                }
                listDTO.setTmpMin(tmp1 + "");
                listDTO.setTmpMax(tmp2 + "");
                listDTO.setCondCodeDay(weatherType + "");
                listDTO.setCondCodeNight(weatherType + "");
                Log.i(TAG, "data:" + listDTO);
            }
            if (FitProSpUtils.isSupport3DaysWeather()) {//判断是否支持多天气
                WeatherProxy.sync3DaysWeather(mWeather2Model, true);
            } else {
                ToastUtils.showShort("不支持多天气");
            }
        } else {
            ToastUtils.showShort("天气不存在");
        }
    }
}