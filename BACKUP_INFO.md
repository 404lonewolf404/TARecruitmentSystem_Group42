# V3.1 通知系统

## 功能说明
实现了完整的通知系统，支持申请状态变化、新申请、申请撤回等通知�?

## 主要更新
- 新增NotificationType枚举（APPLICATION_STATUS_CHANGED等）
- 新增NotificationService服务�?
- 新增NotificationServlet处理通知请求
- 新增notifications.jsp页面（TA/MO/Admin三个版本�?
- 导航栏添加通知入口和未读徽�?
- 支持标记已读和删除通知

## 修改文件
- 新增：NotificationType.java, NotificationService.java, NotificationServlet.java
- 新增：notifications.jsp�?个版本）
- 修改：所有dashboard页面（添加通知链接�?
- 数据：notifications.csv

## 技术要�?
- 通知自动创建（申请状态变化时�?
- 未读数量实时显示
- 支持批量标记已读
