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
	|-- res
		|-- layout
三、具体实现步骤
	1. 在 CoolWeatherOpenHelper 类中建立三张表，Province(省)、City(市)、County(县)，三张表的建表语句分别如下：
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
	2. 在 model 模块下为每个表建立一个实体类，接着我们还需要创建一个 CoolWeatherDB 类，这个类将会把一些常用的数据库操作封装起来，以方便我们后面使用
	3.
	4.
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	