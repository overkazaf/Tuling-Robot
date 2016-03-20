#Tulingrobot-readme.md

###DataBase     数据库， SQLite
	+ history    -- 记录聊天内容
	+ events     -- 记录闹铃事件
	
###TulingSDK    图灵API调用
    + Chatting        聊天
    + Weather         天气
    + News            新闻
    + Express         快递
    + Flight          航班
    
###Native Android    原生安卓系统调用
    + Alarm           设置闹铃
=================================
    * Diag            拨号  
    * Mail            邮箱相关， 可与MailClient搭配， See https://github.com/overkazaf/MailClient
     
      
    
    
##Tables/DataModels        数据模型，数据库表定义, 用于辅助功能扩展
`` history  ``

| 字段           | 类型          | 描述   |
| ------------- |:-------------:| -----|
| _id			|  INTEGER      |  自增主键  |
| type			|  INTEGER      |  0为问，1为答  |
| msg_type		|  INTEGER      |  1文本， 2为链接，3为新闻等  |
| content	    |  TEXT         |  历史消息内容  |
| date          |  TEXT         |  yyyy-MM-dd hh:mm:ss   |



`` events  ``

| 字段           | 类型          | 描述   |
| ------------- |:-------------:| -----|
| id			|  INTEGER      |  自增  |
| info	        |  TEXT         |  事件描述  |
| register_date	|  TEXT         |  事件设置时间  |
| warn_date	    |  TEXT         |  事件提醒   |



##View              视图

    MainActivity         聊天的交互界面
=========================================
    HistoryActivity      聊天历史查询界面
	EventsActivity       事件历史查询界面


##Controller        控制器，用于控制模型与视图间的交互

  * TuringApiManager  -- 图灵全局控制器， 用于控制整个聊天应用， 分发子任务
	+ getHistoryInstance     获取历史实例
	+ getEventsInstance      获取事件实例
	+ getRobotViewInstance   获取聊天视图实例 
	
  * MainActivity -- 主视图类
    + initApplication   初始化主程序
    + initClickEvents   初始化点击事件
    + initTuling        初始化聊天机器人实例
    + initListView      读取聊天信息列表
    + checkNetworkState 检查网络状态是否可用
    + getNetworkType    获取当前的网络类型
    + doRequest         发出聊天请求，由绑定的MyHttpRequestWatcher实例监听返回的结果
	+ appendQuestion    添加问题到视图
	+ appendAnswer      添加回答到视图
	+ updateHistory     更新聊天历史，写数据库
	+ updateEvents      更新事件历史，写数据库
	+ displayAlarm      显示闹铃设置界面
	+ isAvailableCommand  判断输入的命令是否可用，命令关键字可自定义，目前只开发了闹钟的设置功能。  （命令关键字："alarm", "提醒", "闹钟", "闹铃", "备忘"）
        
  * HistoryActivity --  聊天历史类， 用于进行聊天历史相关的增删改查操作
    + initActivity     初始化聊天历史界面
    + initClickEvents  初始化按钮点击事件
    + add              新增聊天信息
	+ del              删除聊天信息
	+ mod              修改聊天信息
	+ find             查询聊天信息
		
  * EventsActivity  --  事件类， 用于进行Android原生事件的调用，以及事件的记录等
    + initActivity    初始化提醒事件历史界面
    + initClickEvents 初始化按钮点击事件
	+ add             新增事件
	+ del             删除事件
	+ mod             修改事件
	+ find            查询事件
	+ call            调用
	
  * ClockActivity  -- 闹钟提醒的视图
    + onCreate    初始化时直接显示设置的提醒事件

  * DBConfig -- 数据库配置类
    + dbName            数据库名称
    + historyTableName  聊天历史数据库表名
    + eventsTableName   提醒事件数据库表名
    + version           数据库版本号
  
  * DBOpenHelper  -- 本地SQLite数据库的助理类

##Decorator         装饰器，用于处理Tuling SDK返回的信息
`由于图灵API在新闻和航班的查询结果中， 默认只返回一个list或url， 这里可以加工一些， 取前几条相关新闻返回到View中`

MyBasePublicElement -- 基类的公共元素类， 在com.tuling.robot.listeners内的几个类会继承于此类， 共用
 +  mMyHandler -- 全局控制器， 用于处理API请求成功的回调
    - constructUrl  构造链接类消息
    - constructText 构造文本类信息
    - constructNews 构造新闻类消息
    
 +  mTuringApiManager  --  图灵API管理器
    - requestTuringAPI   根据提问信息，封装成合法的API请求， 由图灵的SDK提供
    - setRequestWatcher  绑定请求的监听器
    
  