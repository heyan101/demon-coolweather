# demon-coolweather

/**
 * 项目名称：天气预报
 */
 一、项目需求:
	1. 可以罗列出全国所有的省、市、县。
	2. 可以查看全国任意城市的天气信息。
	3. 可以自由地切换城市，去查看其他城市的天气。
	4. 提供手动更新以及后台自动更新天气的功能。

二、项目分析:
	1. 获取全国省市县的数据信息，以及获取每个城市的天气信息,只需要调用下面接口就可以:
		①、获得省份的信息
			http://www.weather.com.cn/data/list3/city.xml  (这个是中国天气网提供的免费 API 接口)
			服务器会返回我们一段文本信息，其中包含了中国所有的省份名称以及省级代号，如下所示：
			01|北京,02|上海,03|天津,04|重庆,05|黑龙江,06|吉林,07|......
		②、获得城市的信息
			http://www.weather.com.cn/data/list3/city05.xml  (这里的05 就是上面的省份重庆的代号)
			1901|南京 ,1902|无锡 ,1903|镇江 ,1904|苏州 ,1905|南通 ,1906|......
		③、获得县的信息
			http://www.weather.com.cn/data/list3/city1904.xml
			190401|苏 州 ,190402| 常 熟 ,190403| 张 家 港 ,190404|昆 山 ,190405|......
		④、查看具体的天气信息
			比如说昆山的县级代号是 190404，那么访问如下地址：
			http://www.weather.com.cn/data/list3/city190404.xml
			这时服务器返回的数据非常简短：
			190404|101190404
			其中，后半部分的 101190404 就是昆山所对应的天气代号了。这个时候再去访问查询天
			气接口，将相应的天气代号填入即可，接口地址如下：
			http://www.weather.com.cn/data/cityinfo/101190404.html
			这样，服务器就会把昆山当前的天气信息以 JSON 格式返回给我们了，如下所示：
			{"weatherinfo":
				{"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃",
				"weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
			}
			city 表示城市名，cityid 表示城市对应的天气代号，temp1 和 temp2 表示气温是几度
			到几度，weather 表示今日天气信息的描述，img1 和 img2 表示今日天气对应的图片，
			ptime 表示天气发布的时间
			
	2. 项目源码结构图
	|-- example.com.coolweather
		|-- activity											用于存放所有活动相关的代码
		|-- model												用于存放所有模型相关的代码
			|-- Provider.java									数据库表 Provider 的实体类
			|-- City.java										数据库表 City 的实体类
			|-- County.java										数据库表 County 的实体类
		|-- receiver											用于存放所有广播接收器相关的代码
		|-- service												用于存放所有服务相关的代码
		|-- sqline												用于存放所有数据库相关的代码
			|-- CoolWeatherOpenHelper.java						数据库表的创建
			|-- CoolWeatherDB.java								把一些常用的数据库操作封装起来
		|-- util												用于存放所有工具相关的代码
			|-- HttpUtil										和服务器的交互
	|-- res
		|-- layout
三、具体实现步骤

	/***************          编写数据库表 和对数据库表的基本操作，以及各表的实体类的实现          ***************/
	
	1.  在 CoolWeatherOpenHelper 类中建立三张表，Province(省)、City(市)、County(县)，三张表的建表语句分别如下：
		Province：
		create table Province (
			id integer primary key autoincrement,
			province_name text,
			province_code text)
		其中 id 是自增长主键，province_name 表示省名，province_code 表示省级代号
		
		City：
		create table City (
			id integer primary key autoincrement,
			city_name text,
			city_code text,
			province_id integer)
		其中 id 是自增长主键，city_name 表示城市名，city_code 表示市级代号，province_id 是
		City 表关联 Province 表的外键
		
		County：
		create table County (
			id integer primary key autoincrement,
			county_name text,
			county_code text,
			city_id integer)
		其中 id 是自增长主键，county_name 表示县名，county_code 表示县级代号，city_id 是
		County 表关联 City 表的外键
		
	2.  在 model 模块下为每个表建立一个实体类，接着我们还需要创建一个 CoolWeatherDB 类，类的基本功能有：
		private CoolWeatherDB(Context context);										// 将构造方法私有化,保证全局范围内只会有一个CoolWeatherDB 的实例
		public synchronized static CoolWeatherDB getInstance(Context context); 		// 获取CoolWeatherDB的实例
		public void savaProvince(Province province); 								// 将Province实例存储到数据库
		public List<Province> loadProvince(); 										// 从数据库读取全国所有的省份信息
		public void saveCity(City city); 											// 将City实例存储到数据库
		public List<City> loadCities(int provinceId); 								// 从数据库读取某省下所有的城市信息
		public void savaCounty(County county) 										// 将County实例存储到数据库
		public List<County> loadCounties(int cityId); 								// 从数据库读取某城市下所有的县信息
		
	/***************                             遍历全国省市县数据                             ***************/
		
	3.  全国所有省市县的数据都是从服务器端获取到的，因此这里和服务器的交互是必不可少的，所以我们可以在 util 包下先增加一个 HttpUtil 类,类的基本功能有：
		public static void sendHttpRequest(final String address, final HttpCallbackListener listener); // 用 HttpURLConnection 类向服务端发送 GET 请求
		
		HttpUtil 类中使用到了 HttpCallbackListener 接口来回调服务返回的结果，因此我们还需要在 util 包下添加一个接口，如下所示:
		public interface HttpCallbackListener {
			void onFinish(String response);
			void onError(Exception e);
		}
		另外服务器返回的省市县数据都是“代号|城市,代号|城市”这种格式的，提供一个工具类来解析和处理这种数据。在 util 包下新建一个 Utility 类,类的基本功能有：
		public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response); 				// 解析和处理服务器返回的省级数据
		public synchronized static boolean handleCitysResponse(CoolWeatherDB coolWeatherDB,String response, int provinceId); 	// 解析和处理服务器返回的城市数据
		public synchronized static boolean handleCountysResponse(CoolWeatherDB coolWeatherDB,String response, int cityId); 		// 解析和处理服务器返回的县  数据
		
	/***************                             界面的实现                             ***************/
	4.  在 res/layout 目录中新建choose_area.xml 布局,代码如下所示：
		<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
			android:layout_width="match_parent"
			android:layout_height="match_parent"
			android:orientation="vertical" >
			<!-- 定义了一个 50dp 高的头布局,TextView 用于显示标题内容,ListView用来存放省市县的数据 -->
			<RelativeLayout
				android:layout_width="match_parent"
				android:layout_height="50dp"
				android:background="#484E61" >
				
			<TextView
				android:id="@+id/title_text"
				android:layout_width="wrap_content"
				android:layout_height="wrap_content"
				android:layout_centerInParent="true"
				android:textColor="#fff"
				android:textSize="24sp" />
			</RelativeLayout>
			
			<ListView
				android:id="@+id/list_view"
				android:layout_width="match_parent"
				android:layout_height="match_parent" >
			</ListView>
		</LinearLayout>
		
	5. 	最关键的一步，我们需要编写用于遍历省市县数据的活动,在 activity 包下新建 ChooseAreaActivity 继承自 Activity,这里有很多方法就不写出来了，程序里写了注释
	
	6. 	接下来我们只需要配置 AndroidManifest.xml 文件，就可以让程序先跑起来了，修改如下所示：
		<manifest xmlns:android="http://schemas.android.com/apk/res/android"
		package="com.coolweather.app"
		android:versionCode="1"
		android:versionName="1.0" >
			……
			<uses-permission android:name="android.permission.INTERNET" />
			<application
				android:allowBackup="true"
				android:icon="@drawable/ic_launcher"
				android:label="@string/app_name"
				android:theme="@style/AppTheme" >
				
				<activity
				android:name="com.coolweather.app.activity.ChooseAreaActivity"
				android:label="@string/app_name" >
				
				<intent-filter>
					<action android:name="android.intent.action.MAIN" />
					<category android:name="android.intent.category.LAUNCHER" />
				</intent-filter>
				
				</activity>
			</application>
		</manifest>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	