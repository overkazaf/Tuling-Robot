#Tulingrobot-readme.md

###DataBase     数据库， SQLite
	+ robot.history    -- 记录聊天内容
	+ robot.events     -- 记录闹铃事件
	
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
| id			|  INTEGER      |  自增  |
| type			|  INTEGER      |  0为问，1为答  |
| content	    |  TEXT         |  历史消息内容  |
| date          |  TEXT         | YYYY-MM-DD HH:MM:SS.SSS   |

`` events  ``

| 字段           | 类型          | 描述   |
| ------------- |:-------------:| -----|
| id			|  INTEGER      |  自增  |
| type			|  INTEGER      |  事件类型  |
| param	        |  TEXT         |  事件参数  |
| info	        |  TEXT         |  事件额外信息   |
| date          |  TEXT         | YYYY-MM-DD HH:MM:SS.SSS   |



##View              视图

    MainActivity         聊天的交互界面
=========================================
    HistoryActivity      聊天历史查询界面
	EventsActivity       事件历史查询界面


##Controller        控制器，用于控制模型与视图间的交互

  * Turing  -- 图灵全局控制器， 用于控制整个聊天应用， 分发子任务
	+ getHistoryInstance     获取历史实例
	+ getEventsInstance      获取事件实例
	+ getRobotViewInstance   获取聊天视图实例 
	
  * RobotView -- 视图类
	+ loadHistory      读取聊天历史记录
    + loadEvents       读取事件记录
	+ addMessage       添加信息到视图
	+ delMessage       从视图中删除信息
    + clear            清空当前面板的聊天信息
        
  * History --  历史类， 用于进行聊天历史相关的增删改查操作
    + add           新增聊天信息
	+ del           删除聊天信息
	+ mod           修改聊天信息
	+ find          查询聊天信息
		
  * Events  --  事件类， 用于进行Android原生事件的调用，以及事件的记录等
	+ add           新增事件
	+ del           删除事件
	+ mod           修改事件
	+ find          查询事件
	+ call          调用

  * DB  --    数据库类， 用于连接数据库，创建数据表，进行具体的数据库操作等
    + config      配置
    + init        初始化
    + connect     连接
    + disconnect  断开连接
	+ execute     执行SQL

##Decorator         装饰器，用于处理Tuling SDK返回的信息
`由于图灵API在新闻和航班的查询结果中， 默认只返回一个url， 这里可以加工一些， 取前几条相关新闻返回到View中`
	TuringDecorator
	+ decoFlight        修饰Flight信息， 并返回JSON数据
	+ decoNews			 修饰News信息， 并返回JSON数据