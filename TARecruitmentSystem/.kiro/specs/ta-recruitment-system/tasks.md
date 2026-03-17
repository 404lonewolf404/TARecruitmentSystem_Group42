# 实现计划：TA招聘系统

## 概述

本实现计划将TA招聘系统分解为增量式的开发任务。每个任务都建立在前面任务的基础上，确保系统逐步构建并在每个阶段都能验证核心功能。

## 任务列表

- [x] 1. 搭建项目结构和核心配置
  - 创建标准的Java Web应用目录结构
  - 创建web.xml部署描述符
  - 创建基础的CSS和JavaScript文件
  - 创建data目录用于CSV文件存储
  - _需求：所有_

- [x] 2. 实现数据模型类
  - [x] 2.1 创建User类及UserRole枚举
    - 实现User类的所有字段和getter/setter方法
    - 实现equals()和hashCode()方法
    - _需求：1.1, 2.1_
  
  - [x] 2.2 创建Position类及PositionStatus枚举
    - 实现Position类的所有字段和getter/setter方法
    - 实现equals()和hashCode()方法
    - _需求：3.1_
  
  - [x] 2.3 创建Application类及ApplicationStatus枚举
    - 实现Application类的所有字段和getter/setter方法
    - 实现equals()和hashCode()方法
    - _需求：4.2_

- [x] 3. 实现CSV数据访问层
  - [x] 3.1 创建CSVDataStore接口
    - 定义通用的CRUD操作方法
    - _需求：7.1, 7.2, 7.3_
  
  - [x] 3.2 实现UserDAO类
    - 实现CSV文件读写逻辑
    - 实现findByEmail()方法
    - 实现authenticate()方法
    - 实现emailExists()方法
    - 处理文件不存在的情况（创建带标题的空文件）
    - _需求：1.1, 1.2, 1.3, 7.1, 7.5_
  
  - [ ] 3.3 为UserDAO编写属性测试

    - **属性19：CSV数据往返一致性**
    - **验证需求：7.1, 7.4**
  
  - [x] 3.4 实现PositionDAO类
    - 实现CSV文件读写逻辑
    - 实现findByMoId()方法
    - 实现findAllOpen()方法
    - _需求：3.1, 3.2, 4.1, 7.2_
  
  - [ ] 3.5 为PositionDAO编写属性测试

    - **属性19：CSV数据往返一致性**
    - **验证需求：7.2, 7.4**
  
  - [x] 3.6 实现ApplicationDAO类
    - 实现CSV文件读写逻辑
    - 实现findByTaId()方法
    - 实现findByPositionId()方法
    - 实现findByTaAndPosition()方法
    - 实现hasApplied()方法
    - _需求：4.2, 4.3, 4.4, 5.1, 7.3_
  
  - [ ]* 3.7 为ApplicationDAO编写属性测试
    - **属性19：CSV数据往返一致性**
    - **验证需求：7.3, 7.4**

- [x] 4. 检查点 - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户

- [x] 5. 实现业务逻辑层
  - [x] 5.1 实现AuthService类
    - 实现register()方法（包含邮箱唯一性验证）
    - 实现login()方法（包含密码验证）
    - 实现logout()方法
    - 实现isAuthenticated()方法
    - 实现getCurrentUser()方法
    - 使用简单的密码哈希（可以先用SHA-256，后续可升级为BCrypt）
    - _需求：1.1, 1.2, 1.3, 1.4, 1.5_
  
  - [ ]* 5.2 为AuthService编写属性测试
    - **属性1：用户注册创建账号**
    - **验证需求：1.1**
  
  - [ ]* 5.3 为AuthService编写属性测试
    - **属性2：邮箱唯一性**
    - **验证需求：1.2**
  
  - [ ]* 5.4 为AuthService编写属性测试
    - **属性4：错误凭证拒绝登录**
    - **验证需求：1.4**
  
  - [x] 5.5 实现PositionService类
    - 实现createPosition()方法（包含字段验证和UUID生成）
    - 实现getPositionsByMO()方法
    - 实现getAllOpenPositions()方法
    - 实现deletePosition()方法（级联删除申请）
    - _需求：3.1, 3.2, 3.3, 3.4, 3.5, 4.1_
  
  - [ ]* 5.6 为PositionService编写属性测试
    - **属性9：职位创建和查询**
    - **验证需求：3.1, 3.2**
  
  - [ ]* 5.7 为PositionService编写属性测试
    - **属性10：职位删除级联**
    - **验证需求：3.3**
  
  - [ ]* 5.8 为PositionService编写属性测试
    - **属性11：职位ID唯一性**
    - **验证需求：3.5**
  
  - [x] 5.9 实现ApplicationService类
    - 实现applyForPosition()方法（包含重复申请检查）
    - 实现withdrawApplication()方法
    - 实现getApplicationsByTA()方法
    - 实现getApplicationsByPosition()方法
    - 实现selectApplicant()方法（更新选中申请为SELECTED，其他为REJECTED）
    - _需求：4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3, 5.4_
  
  - [ ]* 5.10 为ApplicationService编写属性测试
    - **属性13：申请创建关联**
    - **验证需求：4.2**
  
  - [ ]* 5.11 为ApplicationService编写属性测试
    - **属性14：重复申请拒绝**
    - **验证需求：4.3**
  
  - [ ]* 5.12 为ApplicationService编写属性测试
    - **属性16：申请撤回移除**
    - **验证需求：4.5**
  
  - [ ]* 5.13 为ApplicationService编写属性测试
    - **属性18：选择申请者状态更新**
    - **验证需求：5.2, 5.3**
  
  - [x] 5.14 实现WorkloadService类
    - 实现calculateAllWorkloads()方法
    - 只计算状态为SELECTED的申请
    - 返回Map<User, Integer>表示每个TA的总工时
    - _需求：6.1, 6.2, 6.3_
  
  - [ ]* 5.15 为WorkloadService编写属性测试
    - **属性20：工作量计算正确性**
    - **验证需求：6.1, 6.2**

- [x] 6. 检查点 - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户

- [x] 7. 实现过滤器
  - [x] 7.1 实现AuthFilter类
    - 检查会话是否存在
    - 检查会话中是否有用户信息
    - 未认证用户重定向到登录页面
    - _需求：1.6, 8.1, 8.3_
  
  - [x] 7.2 实现RoleFilter类
    - 检查用户角色是否有权限访问请求的URI
    - 定义角色到URL的映射规则
    - 无权限返回403错误
    - _需求：8.2, 8.4_
  
  - [ ]* 7.3 为过滤器编写单元测试
    - 测试未认证访问重定向
    - 测试角色访问控制
    - _需求：1.6, 8.2_

- [x] 8. 实现认证相关的Servlet
  - [x] 8.1 实现AuthServlet类
    - 实现/auth/register端点（POST）
    - 实现/auth/login端点（POST）
    - 实现/auth/logout端点（GET）
    - 处理验证错误并设置错误消息
    - 登录成功后根据角色重定向到相应的dashboard
    - _需求：1.1, 1.2, 1.3, 1.4, 1.5_
  
  - [ ]* 8.2 为AuthServlet编写单元测试
    - 测试注册流程
    - 测试登录流程
    - 测试登出流程
    - _需求：1.1, 1.3, 1.5_

- [x] 9. 实现个人资料相关的Servlet
  - [x] 9.1 实现ProfileServlet类
    - 实现/profile端点（GET）- 显示个人资料
    - 实现/profile/update端点（POST）- 更新个人资料
    - 验证必填字段不为空
    - _需求：2.1, 2.2, 2.3, 2.4_
  
  - [ ]* 9.2 为ProfileServlet编写属性测试
    - **属性7：个人资料往返一致性**
    - **验证需求：2.1, 2.2, 2.3**
  
  - [ ]* 9.3 为ProfileServlet编写属性测试
    - **属性8：必填字段验证**
    - **验证需求：2.4**

- [x] 10. 实现职位相关的Servlet
  - [x] 10.1 实现PositionServlet类
    - 实现/positions端点（GET）- 查看所有开放职位（TA）
    - 实现/positions/my端点（GET）- 查看我的职位（MO）
    - 实现/positions/create端点（POST）- 创建职位（MO）
    - 实现/positions/delete端点（POST）- 删除职位（MO）
    - 验证必填字段不为空
    - _需求：3.1, 3.2, 3.3, 3.4, 4.1_
  
  - [ ]* 10.2 为PositionServlet编写单元测试
    - 测试职位创建
    - 测试职位查询
    - 测试职位删除
    - _需求：3.1, 3.2, 3.3_

- [x] 11. 实现申请相关的Servlet
  - [x] 11.1 实现ApplicationServlet类
    - 实现/applications/apply端点（POST）- 申请职位（TA）
    - 实现/applications/withdraw端点（POST）- 撤回申请（TA）
    - 实现/applications/my端点（GET）- 查看我的申请（TA）
    - 实现/applications/position端点（GET）- 查看职位申请列表（MO）
    - 实现/applications/select端点（POST）- 选择申请者（MO）
    - _需求：4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3_
  
  - [ ]* 11.2 为ApplicationServlet编写单元测试
    - 测试申请创建
    - 测试申请撤回
    - 测试申请查询
    - 测试选择申请者
    - _需求：4.2, 4.5, 5.2_

- [x] 12. 实现管理员相关的Servlet
  - [x] 12.1 实现AdminServlet类
    - 实现/admin/workload端点（GET）- 查看工作量报告
    - 调用WorkloadService获取数据
    - _需求：6.1, 6.2, 6.3_
  
  - [ ]* 12.2 为AdminServlet编写单元测试
    - 测试工作量报告生成
    - _需求：6.1_

- [x] 13. 检查点 - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户

- [x] 14. 实现公共JSP页面
  - [x] 14.1 创建login.jsp
    - 登录表单（邮箱、密码）
    - 显示错误消息
    - 链接到注册页面
    - _需求：1.3, 1.4_
  
  - [x] 14.2 创建register.jsp
    - 注册表单（姓名、邮箱、密码、角色、技能）
    - 显示错误消息
    - 链接到登录页面
    - _需求：1.1, 1.2_
  
  - [x] 14.3 创建error.jsp
    - 显示错误信息
    - 返回按钮
    - _需求：所有_

- [x] 15. 实现TA相关的JSP页面
  - [x] 15.1 创建ta-dashboard.jsp
    - 显示欢迎信息
    - 导航链接（个人资料、浏览职位、我的申请）
    - 登出按钮
    - _需求：1.5_
  
  - [x] 15.2 创建ta-profile.jsp
    - 显示和编辑个人资料表单
    - 保存按钮
    - _需求：2.1, 2.2, 2.3_
  
  - [x] 15.3 创建ta-positions.jsp
    - 显示所有开放职位列表
    - 每个职位显示标题、描述、要求、工时
    - 申请按钮
    - _需求：4.1, 4.2_
  
  - [x] 15.4 创建ta-applications.jsp
    - 显示我的申请列表
    - 显示职位信息和申请状态
    - 撤回按钮（仅PENDING状态）
    - _需求：4.4, 4.5_

- [x] 16. 实现MO相关的JSP页面
  - [x] 16.1 创建mo-dashboard.jsp
    - 显示欢迎信息
    - 导航链接（我的职位、创建职位）
    - 登出按钮
    - _需求：1.5_
  
  - [x] 16.2 创建mo-positions.jsp
    - 显示我的职位列表
    - 每个职位显示详细信息
    - 查看申请按钮
    - 删除职位按钮
    - _需求：3.2, 3.3_
  
  - [x] 16.3 创建mo-create-position.jsp
    - 创建职位表单（标题、描述、要求、工时）
    - 提交按钮
    - _需求：3.1, 3.4_
  
  - [x] 16.4 创建mo-applications.jsp
    - 显示特定职位的申请列表
    - 显示申请者信息（姓名、邮箱、技能）
    - 选择按钮
    - _需求：5.1, 5.2, 5.3_

- [x] 17. 实现Admin相关的JSP页面
  - [x] 17.1 创建admin-dashboard.jsp
    - 显示欢迎信息
    - 导航链接（工作量报告）
    - 登出按钮
    - _需求：1.5_
  
  - [x] 17.2 创建admin-workload.jsp
    - 显示所有TA的工作量表格
    - 显示TA姓名、邮箱、总工时
    - 按工时排序
    - _需求：6.1, 6.2, 6.3_

- [x] 18. 实现前端样式和交互
  - [x] 18.1 创建style.css
    - 定义全局样式
    - 定义表单样式
    - 定义表格样式
    - 定义按钮样式
    - 响应式设计
    - _需求：所有_
  
  - [x] 18.2 创建main.js
    - 表单验证（客户端）
    - 确认对话框（删除、撤回等操作）
    - 动态UI交互
    - _需求：所有_

- [x] 19. 配置web.xml
  - [x] 19.1 配置所有Servlet映射
    - AuthServlet -> /auth/*
    - ProfileServlet -> /profile/*
    - PositionServlet -> /positions/*
    - ApplicationServlet -> /applications/*
    - AdminServlet -> /admin/*
    - _需求：所有_
  
  - [x] 19.2 配置过滤器映射
    - AuthFilter -> /ta/*, /mo/*, /admin/*
    - RoleFilter -> /ta/*, /mo/*, /admin/*
    - _需求：1.6, 8.2_
  
  - [x] 19.3 配置欢迎文件和会话超时
    - 设置login.jsp为欢迎文件
    - 设置会话超时为30分钟
    - _需求：8.3_

- [x] 20. 最终检查点 - 集成测试
  - [ ]* 20.1 编写端到端集成测试
    - 测试完整的用户注册和登录流程
    - 测试完整的职位创建、申请、选择流程
    - 测试工作量报告生成
    - _需求：所有_
  
  - [x] 20.2 手动测试所有功能
    - 确保所有页面正常显示
    - 确保所有功能正常工作
    - 确保错误处理正确
    - 如有问题请询问用户

## 注意事项

- 标记为`*`的任务是可选的，可以跳过以加快MVP开发
- 每个任务都引用了具体的需求以便追溯
- 检查点确保增量验证
- 属性测试验证通用正确性属性
- 单元测试验证特定示例和边缘情况
