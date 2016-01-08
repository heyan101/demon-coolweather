package example.com.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import example.com.coolweather.R;
import example.com.coolweather.model.City;
import example.com.coolweather.model.County;
import example.com.coolweather.model.Province;
import example.com.coolweather.sqline.CoolWeatherDB;
import example.com.coolweather.util.HttpCallbackListener;
import example.com.coolweather.util.HttpUtil;
import example.com.coolweather.util.Utility;

/**
 * Created by Demon on 2016/1/7.
 */
public class ChooseAreaActivity extends Activity
{
    public static final int LEVEL_PROVINCE = 0; // 省
    public static final int LEVEL_CITY = 1;     // 市
    public static final int LEVEL_COUNTY = 2;   // 县

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        // 初始化 ArrayAdapter ,将它设为 ListView 的适配器
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        // 获取 CoolWeatherDB 的实例
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        // 给 ListView 设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (currentLevel == LEVEL_PROVINCE)
                {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY)
                {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();  // 加载省级数据
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces()
    {
        // 从数据库中读取省级数据,没读到就从服务器(中国天气网)上查询数据
        provinceList = coolWeatherDB.loadProvince();
        if (provinceList.size() > 0)
        {
            dataList.clear();
            for (Province province : provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else
        {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities()
    {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0)
        {
            dataList.clear();
            for (City city : cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else
        {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties()
    {
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0)
        {
            dataList.clear();
            for (County county : countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else
        {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据
     * @param code 某个县的市级代号或者某个市的省级代号
     * @param type 要查询的类型
     */
    private void queryFromServer(final String code, final String type)
    {
        String address = "";
        if (!TextUtils.isEmpty(code))
        {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else
        {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                boolean result = false;
                if ("province".equals(type))
                {
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                } else if ("city".equals(type))
                {
                    result = Utility.handleCitiesResponse(coolWeatherDB, response,
                            selectedProvince.getId());
                } else if ("county".equals(type))
                {
                    result = Utility.handleCountiesResponse(coolWeatherDB, response,
                            selectedCity.getId());
                }
                if (result)
                {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            closeProgressDialog();
                            if ("province".equals(type))
                            {
                                queryProvinces();
                            } else if ("city".equals(type))
                            {
                                queryCities();
                            } else if ("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e)
            {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "Load Failed.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog()
    {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading ...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog()
    {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    public void onBackPressed()
    {
        if (currentLevel == LEVEL_COUNTY)
        {
            queryCities();
        } else if (currentLevel == LEVEL_CITY)
        {
            queryProvinces();
        } else
        {
            finish();
        }
    }
}
