package example.com.coolweather.model;

/**
 * 省份的实现
 *
 * Created by Demon on 2016/1/7.
 */
public class Province
{
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getProvinceName()
    {
        return provinceName;
    }

    public void setProvinceName(String provinceName)
    {
        this.provinceName = provinceName;
    }

    public String getProvinceCode()
    {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode)
    {
        this.provinceCode = provinceCode;
    }

    private int id;
    private String provinceName;
    private String provinceCode;




}
