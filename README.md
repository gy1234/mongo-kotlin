mongodb基本使用mongoDao
mongodb数据落地，使用增量更新
数据更新，使用对象copy方式做差异化比对
使用JMH测试对象copy的两种方式性能：a. 深度拷贝，b. json序列化
项目采用Maven构建，源代码使用Kotlin和Java混合编写（因为JMH测试代码需要使用Java编写）
