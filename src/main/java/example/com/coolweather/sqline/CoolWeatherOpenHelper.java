package example.com.coolweather.sqline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Demon on 2016/1/6.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper
{
    /**
     * Province () 表建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province (" +
            "id integer primary key autoincrement, " +
            "province_name text, " +  // 省份的名称
            "province_code text)";    // 省份的代号

    /**
     * City(城市)表建表语句
     */
    public static final String CREATE_CITY = "create table City (" +
            "id integer primary key autoincrement, " +
            "city_name text, " +
            "city_code text，" +
            "province_id integer)";

    /**
     * County (县) 表建表语句
     */
    public static final String CREATE_COUNTY = "create table County (" +
            "id integer primary key autoincrement, " +
            "county_name text, " +
            "county_code text, " +
            "city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name,
                                 SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // 创建三张表
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
