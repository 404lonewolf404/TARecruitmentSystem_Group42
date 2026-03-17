# 设计文档

## 概述

TA招聘系统是一个基于Java Servlet/JSP的Web应用程序，使用Apache Tomcat 10.1.28作为服务器，采用CSV文件进行数据持久化。系统采用MVC（Model-View-Controller）架构模式，将业务逻辑、数据访问和用户界面分离。

### 技术栈
- **前端**: HTML5, CSS3, 原生JavaScript
- **后端**: Java Servlet 6.0, JSP 3.1
- **服务器**: Apache Tomcat 10.1.28
- **数据存储**: CSV文件
- **会话管理**: HttpSession

## 架构

### 整体架构

系统采用三层架构：

```
表示层 (Presentation Layer)
    ↓
业务逻辑层 (Business Logic Layer)
    ↓
数据访问层 (Data Access Layer)
    ↓
CSV文件存储
```

### MVC模式

- **Model（模型）**: Java Bean类表示数据实体（User, Position, Application）
- **View（视图）**: JSP页面负责UI渲染
- **Controller（控制器）**: Servlet处理HTTP请求和业务逻辑

## 组件和接口

### 1. 数据模型（Model）

#### User类
```java
public class User {
    private String userId;        // 唯一标识符（UUID）
    private String name;          // 用户姓名
    private String email;         // 邮箱（唯一）
    private String password;      // 密码（应加密存储）
    private UserRole role;        // 角色：TA, MO, ADMIN
    private String skills;        // TA技能（仅TA角色使用）
    private Date createdAt;       // 创建时间
}

public enum UserRole {
    TA, MO, ADMIN
}
```

#### Position类
```java
public class Position {
    private String positionId;    // 唯一标识符（UUID）
    private String moId;          // 发布者MO的userId
    private String title;         // 职位标题
    private String description;   // 职位描述
    private String requirements;  // 职位要求
    private int hours;            // 工作时长（小时/周）
    private PositionStatus status; // 状态：OPEN, CLOSED
    private Date createdAt;       // 创建时间
}

public enum PositionStatus {
    OPEN, CLOSED
}
```

#### Application类
```java
public class Application {
    private String applicationId; // 唯一标识符（UUID）
    private String taId;          // 申请者TA的userId
    private String positionId;    // 申请的职位ID
    private ApplicationStatus status; // 状态：PENDING, SELECTED, REJECTED, WITHDRAWN
    private Date appliedAt;       // 申请时间
}

public enum ApplicationStatus {
    PENDING,    // 待审核
    SELECTED,   // 已选中
    REJECTED,   // 已拒绝
    WITHDRAWN   // 已撤回
}
```

### 2. 数据访问层（DAO）

#### CSVDataStore接口
所有DAO类的基础接口，提供CSV文件的读写操作。

```java
public interface CSVDataStore<T> {
    List<T> loadAll() throws IOException;
    void saveAll(List<T> items) throws IOException;
    void add(T item) throws IOException;
    void update(T item) throws IOException;
    void delete(String id) throws IOException;
    T findById(String id);
}
```

#### UserDAO类
```java
public class UserDAO implements CSVDataStore<User> {
    private static final String FILE_PATH = "data/users.csv";
    
    public User findByEmail(String email);
    public User authenticate(String email, String password);
    public boolean emailExists(String email);
}
```

#### PositionDAO类
```java
public class PositionDAO implements CSVDataStore<Position> {
    private static final String FILE_PATH = "data/positions.csv";
    
    public List<Position> findByMoId(String moId);
    public List<Position> findAllOpen();
}
```

#### ApplicationDAO类
```java
public class ApplicationDAO implements CSVDataStore<Application> {
    private static final String FILE_PATH = "data/applications.csv";
    
    public List<Application> findByTaId(String taId);
    public List<Application> findByPositionId(String positionId);
    public Application findByTaAndPosition(String taId, String positionId);
    public boolean hasApplied(String taId, String positionId);
}
```

### 3. 业务逻辑层（Service）

#### AuthService类
处理用户认证和授权逻辑。

```java
public class AuthService {
    private UserDAO userDAO;
    
    public User register(String name, String email, String password, UserRole role, String skills);
    public User login(String email, String password);
    public void logout(HttpSession session);
    public boolean isAuthenticated(HttpSession session);
    public User getCurrentUser(HttpSession session);
}
```

#### PositionService类
处理职位相关的业务逻辑。

```java
public class PositionService {
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;
    
    public Position createPosition(String moId, String title, String description, 
                                   String requirements, int hours);
    public List<Position> getPositionsByMO(String moId);
    public List<Position> getAllOpenPositions();
    public void deletePosition(String positionId);
}
```

#### ApplicationService类
处理申请相关的业务逻辑。

```java
public class ApplicationService {
    private ApplicationDAO applicationDAO;
    private PositionDAO positionDAO;
    private UserDAO userDAO;
    
    public Application applyForPosition(String taId, String positionId);
    public void withdrawApplication(String applicationId);
    public List<Application> getApplicationsByTA(String taId);
    public List<Application> getApplicationsByPosition(String positionId);
    public void selectApplicant(String applicationId);
}
```

#### WorkloadService类
处理工作量统计逻辑。

```java
public class WorkloadService {
    private ApplicationDAO applicationDAO;
    private PositionDAO positionDAO;
    private UserDAO userDAO;
    
    public Map<User, Integer> calculateAllWorkloads();
}
```

### 4. 控制器层（Servlet）

#### AuthServlet
- `/register` (POST) - 用户注册
- `/login` (POST) - 用户登录
- `/logout` (GET) - 用户登出

#### ProfileServlet
- `/profile` (GET) - 查看个人资料
- `/profile/update` (POST) - 更新个人资料

#### PositionServlet
- `/positions` (GET) - 查看职位列表
- `/positions/create` (POST) - 创建职位（MO）
- `/positions/delete` (POST) - 删除职位（MO）
- `/positions/my` (GET) - 查看我的职位（MO）

#### ApplicationServlet
- `/applications/apply` (POST) - 申请职位（TA）
- `/applications/withdraw` (POST) - 撤回申请（TA）
- `/applications/my` (GET) - 查看我的申请（TA）
- `/applications/position` (GET) - 查看职位申请列表（MO）
- `/applications/select` (POST) - 选择申请者（MO）

#### AdminServlet
- `/admin/workload` (GET) - 查看工作量报告（Admin）

### 5. 视图层（JSP）

#### 公共页面
- `login.jsp` - 登录页面
- `register.jsp` - 注册页面
- `error.jsp` - 错误页面

#### TA页面
- `ta-dashboard.jsp` - TA仪表板
- `ta-profile.jsp` - TA个人资料
- `ta-positions.jsp` - 浏览职位
- `ta-applications.jsp` - 我的申请

#### MO页面
- `mo-dashboard.jsp` - MO仪表板
- `mo-positions.jsp` - 我的职位
- `mo-create-position.jsp` - 创建职位
- `mo-applications.jsp` - 查看申请

#### Admin页面
- `admin-dashboard.jsp` - Admin仪表板
- `admin-workload.jsp` - 工作量报告

### 6. 过滤器（Filter）

#### AuthFilter
拦截所有受保护的请求，验证用户是否已登录。

```java
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) {
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // 重定向到登录页面
        } else {
            chain.doFilter(request, response);
        }
    }
}
```

#### RoleFilter
验证用户是否有权限访问特定资源。

```java
public class RoleFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) {
        User user = (User) session.getAttribute("user");
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        
        // 根据URI和用户角色判断是否允许访问
        if (hasPermission(user.getRole(), requestURI)) {
            chain.doFilter(request, response);
        } else {
            // 返回403错误
        }
    }
}
```

## 数据模型

### CSV文件格式

#### users.csv
```
userId,name,email,password,role,skills,createdAt
uuid1,张三,zhangsan@example.com,hashedPassword,TA,Java|Python,2026-03-06T10:00:00
uuid2,李四,lisi@example.com,hashedPassword,MO,,2026-03-06T10:05:00
```

#### positions.csv
```
positionId,moId,title,description,requirements,hours,status,createdAt
uuid3,uuid2,Java助教,协助Java课程教学,熟悉Java编程,10,OPEN,2026-03-06T11:00:00
```

#### applications.csv
```
applicationId,taId,positionId,status,appliedAt
uuid4,uuid1,uuid3,PENDING,2026-03-06T12:00:00
```

### 数据关系

```
User (role=MO) --1:N--> Position
User (role=TA) --1:N--> Application
Position --1:N--> Application
```

## 正确性属性

*属性是一个特征或行为，应该在系统的所有有效执行中保持为真——本质上是关于系统应该做什么的正式声明。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*


### 属性列表

**属性1：用户注册创建账号**
*对于任何*有效的用户注册信息（姓名、邮箱、密码、角色），注册后应该能够通过邮箱找到该用户账号
**验证需求：1.1**

**属性2：邮箱唯一性**
*对于任何*已注册的邮箱，尝试使用相同邮箱再次注册应该被拒绝
**验证需求：1.2**

**属性3：登录创建会话**
*对于任何*已注册用户，使用正确的凭证登录后应该创建包含用户信息的会话
**验证需求：1.3, 8.1**

**属性4：错误凭证拒绝登录**
*对于任何*已注册用户，使用错误的密码尝试登录应该被拒绝
**验证需求：1.4**

**属性5：登出终止会话**
*对于任何*已登录用户，登出后会话应该被终止，再次访问受保护页面应该被拒绝
**验证需求：1.5, 8.3**

**属性6：未认证访问重定向**
*对于任何*受保护的页面，未登录状态下访问应该被重定向到登录页面
**验证需求：1.6**

**属性7：个人资料往返一致性**
*对于任何*TA个人资料，创建后更新再查看应该显示更新后的信息
**验证需求：2.1, 2.2, 2.3**

**属性8：必填字段验证**
*对于任何*空的或仅包含空白字符的必填字段（姓名、邮箱），提交应该被拒绝
**验证需求：2.4, 3.4**

**属性9：职位创建和查询**
*对于任何*MO创建的职位，该MO查询自己的职位列表时应该包含该职位
**验证需求：3.1, 3.2**

**属性10：职位删除级联**
*对于任何*有申请的职位，删除职位后所有相关申请也应该被删除
**验证需求：3.3**

**属性11：职位ID唯一性**
*对于任何*创建的多个职位，每个职位的ID应该是唯一的
**验证需求：3.5**

**属性12：开放职位可见性**
*对于任何*状态为OPEN的职位，TA查看职位列表时应该能看到该职位
**验证需求：4.1**

**属性13：申请创建关联**
*对于任何*TA和职位，申请后应该能够通过TA ID和职位ID找到该申请记录
**验证需求：4.2**

**属性14：重复申请拒绝**
*对于任何*TA和职位，如果已经申请过，再次申请应该被拒绝
**验证需求：4.3**

**属性15：申请查询完整性**
*对于任何*TA的所有申请，查询时应该返回所有申请记录及其当前状态
**验证需求：4.4**

**属性16：申请撤回移除**
*对于任何*申请，撤回后该申请记录应该不再存在
**验证需求：4.5**

**属性17：职位申请列表完整性**
*对于任何*职位的所有申请，MO查询时应该返回所有申请者及其个人资料信息
**验证需求：5.1**

**属性18：选择申请者状态更新**
*对于任何*职位的申请，当MO选择一个申请者时，该申请状态应该变为SELECTED，其他所有申请状态应该变为REJECTED
**验证需求：5.2, 5.3**

**属性19：CSV数据往返一致性**
*对于任何*数据实体（用户、职位、申请），保存到CSV文件后重新加载应该得到等价的数据
**验证需求：7.1, 7.2, 7.3, 7.4**

**属性20：工作量计算正确性**
*对于任何*TA，其工作量应该等于所有状态为SELECTED的申请对应职位的工时总和
**验证需求：6.1, 6.2**

**属性21：工作量报告完整性**
*对于任何*工作量报告，应该包含所有TA的姓名、邮箱和总工时
**验证需求：6.3**

**属性22：角色访问控制**
*对于任何*用户，只能访问与其角色相应的页面，访问其他角色的页面应该被拒绝
**验证需求：8.2, 8.4**

## 错误处理

### 异常类型

1. **ValidationException**: 输入验证失败
   - 空字段
   - 无效格式
   - 重复数据

2. **AuthenticationException**: 认证失败
   - 错误的凭证
   - 会话过期
   - 未授权访问

3. **DataAccessException**: 数据访问错误
   - CSV文件读写失败
   - 数据格式错误
   - 文件不存在

4. **BusinessLogicException**: 业务逻辑错误
   - 重复申请
   - 删除不存在的记录
   - 状态转换无效

### 错误处理策略

1. **Servlet层**: 捕获所有异常，设置错误消息到request attribute，转发到error.jsp
2. **Service层**: 抛出业务异常，由Servlet层处理
3. **DAO层**: 抛出数据访问异常，由Service层包装后抛出
4. **JSP层**: 显示友好的错误消息

### 错误响应格式

```java
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    private String timestamp;
}
```

## 测试策略

### 双重测试方法

系统将采用单元测试和基于属性的测试相结合的方法：

- **单元测试**: 验证特定示例、边缘情况和错误条件
- **基于属性的测试**: 验证所有输入的通用属性
- 两者互补，共同提供全面的覆盖

### 单元测试

使用JUnit 5进行单元测试，重点关注：

1. **DAO层测试**
   - CSV文件读写
   - 数据查询方法
   - 边缘情况（空文件、格式错误）

2. **Service层测试**
   - 业务逻辑验证
   - 异常处理
   - 特定场景（如选择申请者后其他申请状态变化）

3. **Servlet层测试**
   - HTTP请求处理
   - 会话管理
   - 重定向和转发

### 基于属性的测试

使用jqwik（Java的基于属性测试库）进行基于属性的测试：

**配置**:
- 每个属性测试最少运行100次迭代
- 每个测试必须引用设计文档中的属性
- 标签格式：`@Tag("Feature: ta-recruitment-system, Property N: [属性文本]")`

**测试重点**:
1. **往返属性**: CSV序列化/反序列化
2. **不变量**: 唯一性约束、状态一致性
3. **幂等性**: 重复操作的结果
4. **错误条件**: 无效输入的处理

**示例**:
```java
@Property
@Tag("Feature: ta-recruitment-system, Property 19: CSV数据往返一致性")
void csvRoundTripConsistency(@ForAll User user) {
    userDAO.add(user);
    User loaded = userDAO.findById(user.getUserId());
    assertEquals(user, loaded);
}
```

### 集成测试

测试多个组件的交互：
- 完整的用户注册和登录流程
- 职位创建、申请、选择的完整流程
- 数据持久化和加载

### 测试数据管理

- 使用独立的测试CSV文件
- 每个测试前清理数据
- 使用测试fixture提供一致的测试数据

## 部署架构

### 目录结构

```
webapps/TARecruitmentSystem/
├── WEB-INF/
│   ├── web.xml                 # 部署描述符
│   ├── classes/                # 编译后的Java类
│   │   ├── com/
│   │   │   └── bupt/
│   │   │       └── tarecruitment/
│   │   │           ├── model/
│   │   │           ├── dao/
│   │   │           ├── service/
│   │   │           ├── servlet/
│   │   │           └── filter/
│   └── lib/                    # 第三方库（如果需要）
├── data/                       # CSV数据文件
│   ├── users.csv
│   ├── positions.csv
│   └── applications.csv
├── css/                        # 样式文件
│   └── style.css
├── js/                         # JavaScript文件
│   └── main.js
├── login.jsp
├── register.jsp
├── error.jsp
└── [其他JSP文件]
```

### web.xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
         https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
    
    <display-name>TA Recruitment System</display-name>
    
    <!-- Filters -->
    <filter>
        <filter-name>AuthFilter</filter-name>
        <filter-class>com.bupt.tarecruitment.filter.AuthFilter</filter-class>
    </filter>
    
    <filter-mapping>
        <filter-name>AuthFilter</filter-name>
        <url-pattern>/ta/*</url-pattern>
        <url-pattern>/mo/*</url-pattern>
        <url-pattern>/admin/*</url-pattern>
    </filter-mapping>
    
    <!-- Servlets -->
    <servlet>
        <servlet-name>AuthServlet</servlet-name>
        <servlet-class>com.bupt.tarecruitment.servlet.AuthServlet</servlet-class>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>AuthServlet</servlet-name>
        <url-pattern>/auth/*</url-pattern>
    </servlet-mapping>
    
    <!-- Welcome file -->
    <welcome-file-list>
        <welcome-file>login.jsp</welcome-file>
    </welcome-file-list>
    
    <!-- Session timeout (30 minutes) -->
    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>
</web-app>
```

### 安全考虑

1. **密码安全**: 使用BCrypt或类似算法对密码进行哈希处理
2. **会话安全**: 设置HttpOnly和Secure标志
3. **输入验证**: 所有用户输入必须验证和清理
4. **CSRF保护**: 使用token防止跨站请求伪造
5. **XSS防护**: 对输出进行HTML转义

### 性能优化

1. **CSV缓存**: 在内存中缓存CSV数据，定期同步到文件
2. **延迟加载**: 只在需要时加载数据
3. **连接池**: 虽然不使用数据库，但可以考虑文件访问的同步机制
4. **静态资源**: CSS和JS文件使用浏览器缓存

## 实现注意事项

1. **CSV格式**: 使用UTF-8编码，处理特殊字符（逗号、引号、换行）
2. **并发控制**: 使用文件锁或同步机制防止并发写入冲突
3. **数据备份**: 定期备份CSV文件
4. **日志记录**: 记录关键操作和错误信息
5. **国际化**: 考虑中英文界面支持
