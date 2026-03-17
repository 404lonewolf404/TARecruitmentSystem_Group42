# TA招聘系统 - 申请管理版

基于Java Servlet的助教招聘管理系统。

## 功能说明

本配置在基础版上新增了完整的申请管理功能，支持TA申请职位和MO审核申请。

---

## 功能说明

### 用户管理
- **注册**：支持TA和MO两种角色注册，邮箱唯一性验证
- **登录**：SHA-256密码加密，登录后根据角色跳转
- **登出**：清除会话
- **权限控制**：AuthFilter拦截未登录访问，RoleFilter实现严格的角色隔离

### 职位管理
- **MO功能**：创建职位、查看我的职位列表、删除职位
- **TA功能**：浏览所有开放状态的职位及详细信息

### 申请管理
- **TA功能**：
  - 申请职位
  - 查看我的申请列表
  - 撤回申请（仅PENDING状态可撤回）
- **MO功能**：
  - 查看职位的申请列表
  - 选择申请者（自动拒绝该职位的其他申请）
- **状态管理**：
  - PENDING（待审核）
  - SELECTED（已选中）
  - REJECTED（已拒绝）
  - WITHDRAWN（已撤回）

### 数据存储
- **CSV文件**：users.csv、positions.csv、applications.csv
- **自动创建**：首次运行自动创建数据文件和目录

---

## 技术架构

### 后端（Java）
- **Model**：User, Position, Application, UserRole(TA/MO), PositionStatus(OPEN/CLOSED), ApplicationStatus
- **DAO**：UserDAO, PositionDAO, ApplicationDAO（CSV读写、CRUD操作）
- **Service**：AuthService（注册/登录/认证）、PositionService（职位管理）、ApplicationService（申请管理）
- **Servlet**：AuthServlet（/auth/*）、PositionServlet（/ta/*, /mo/*）、ApplicationServlet（/ta/applications/*, /mo/applications/*）
- **Filter**：AuthFilter（登录验证）、RoleFilter（角色权限验证）

### 前端（JSP）
- **公共页面**：login.jsp, register.jsp, error.jsp
- **TA页面**：ta/positions.jsp（职位列表）、ta/applications.jsp（我的申请）
- **MO页面**：mo/positions.jsp（我的职位）、mo/create-position.jsp（创建职位）、mo/applications.jsp（申请审核）

---

## 快速开始

### 环境要求
- JDK 11+
- Apache Tomcat 10.x

### 部署步骤

1. **添加依赖**
```bash
mkdir lib
# 下载 jakarta.servlet-api-5.0.0.jar 到 lib 目录
```

2. **编译**
```bash
javac -encoding UTF-8 -d WEB-INF/classes -cp "lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/model/*.java src/com/bupt/tarecruitment/dao/*.java src/com/bupt/tarecruitment/service/*.java src/com/bupt/tarecruitment/filter/*.java src/com/bupt/tarecruitment/servlet/*.java
```

3. **部署**
```bash
# 复制项目到 Tomcat/webapps/TARecruitmentSystem
# 启动Tomcat
# 访问 http://localhost:8080/TARecruitmentSystem/
```

---

## 测试账号

| 角色 | 邮箱 | 密码 |
|------|------|------|
| TA | ta@test.com | 123456 |
| MO | mo@test.com | 123456 |

---

## 访问路径

### 公共
- `/auth/login` - 登录
- `/auth/register` - 注册
- `/auth/logout` - 登出

### TA
- `/ta/positions` - 浏览职位
- `/ta/applications` - 我的申请
- `/ta/applications/apply` - 申请职位（POST）
- `/ta/applications/withdraw` - 撤回申请（POST）

### MO
- `/mo/positions` - 我的职位
- `/mo/positions/create` - 创建职位（POST）
- `/mo/positions/delete` - 删除职位（POST）
- `/mo/applications` - 查看申请
- `/mo/applications/select` - 选择申请者（POST）

---

## 项目结构

```
TARecruitmentSystem/
├── src/com/bupt/tarecruitment/    # Java源码
│   ├── model/                      # 实体类
│   ├── dao/                        # 数据访问
│   ├── service/                    # 业务逻辑
│   ├── filter/                     # 过滤器
│   └── servlet/                    # 控制器
├── WEB-INF/
│   ├── jsp/                        # 视图
│   ├── classes/                    # 编译文件（不提交到Git）
│   └── web.xml                     # 配置
├── data/                           # CSV数据
├── css/                            # 样式
├── js/                             # 脚本
├── .gitignore                      # Git忽略配置
└── README.md                       # 本文件
```

---

## 相关文档

- [CODE_GUIDE.md](CODE_GUIDE.md) - 代码文件详细说明
- [GIT_TUTORIAL.md](GIT_TUTORIAL.md) - Git使用教程

---

**维护者**：TA Recruitment System Team
