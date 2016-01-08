package example.com.coolweather.util;

import android.text.TextUtils;

import example.com.coolweather.model.City;
import example.com.coolweather.model.County;
import example.com.coolweather.model.Province;
import example.com.coolweather.sqline.CoolWeatherDB;

/**
 * 服务器返回的省市县数据都是“代号|城市,代号|城市”这种格式的，
 * 所以我们提供一个工具类来解析和处理这种数据
 * Created by Demon on 2016/1/7.
 */
public class Utility
{
    /**
     * 解析和处理服务器返回的省级数据
     * @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,
                                                               String response)
    {
        // 判断传过来的数据是否为空，这里传过来的数据格式应该是这样的：
        // 01|北京,02|上海,03|天津,04|重庆,05|黑龙江,06|吉林,07|......
        if (!TextUtils.isEmpty(response))
        {
            String[] allProvince = response.split(",");
            if (allProvince != null && allProvince.length >0)
            {
                for (String p : allProvince)
                {
                    // 这里需要处理转义字符
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析得到的数据存储到 Province表
                    coolWeatherDB.saveProvince(province);
                }
                return  true;
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param coolWeatherDB
     * @param response
     * @param provinceId 城市所属的省份Id
     * @return
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
                                                           String response, int provinceId)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allCity = response.split(",");
            if (allCity != null && allCity.length > 0)
            {
                for (String c : allCity)
                {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param coolWeatherDB
     * @param response
     * @param cityId 县所属的城市Id
     * @return
     */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                             String response, int cityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allCounty = response.split(",");
            if (allCounty != null && allCounty.length > 0)
            {
                for (String c : allCounty)
                {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
