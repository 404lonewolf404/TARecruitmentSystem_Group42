# V3.5 招聘对话系统 - Part 1

## 功能说明
实现基本的一对一对话功能，TA和MO可以针对申请进行沟通�?

## 主要更新
- 新增Message模型和MessageDAO
- 新增MessageService服务�?
- 新增MessageServlet处理对话请求
- 新增conversation-simple.jsp对话页面
- 申请列表添加"对话"按钮
- 支持发送消息和查看历史

## 修改文件
- 新增：Message.java, MessageDAO.java, MessageService.java, MessageServlet.java
- 新增：conversation-simple.jsp, compile-message.bat
- 修改：applications.jsp（TA/MO添加对话按钮�?
- 修改：NotificationType.java（添加MESSAGE类型�?
- 数据：messages.csv

## 技术要�?
- 基于申请的一对一对话
- 权限验证（只有申请双方可对话�?
- 自动创建消息通知
