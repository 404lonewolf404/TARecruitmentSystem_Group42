# V3.2 职位截止日期管理

## 功能说明
为职位添加申请截止日期功能，支持自动过期控制和剩余天数显示�?

## 主要更新
- Position模型添加deadline字段
- 创建职位时可设置截止日期（可选）
- 显示剩余天数（颜色编码：�?�?红）
- 过期职位自动停止接受申请
- 前后端双重验�?

## 修改文件
- 修改：Position.java（添加deadline相关方法�?
- 修改：PositionDAO.java, PositionService.java
- 修改：ApplicationServlet.java（添加过期检查）
- 修改：create-position.jsp, positions.jsp（MO/TA�?
- 数据：positions.csv�?个测试职位）

## 技术要�?
- 🟢 绿色�?天以�?
- 🟡 黄色�?-7�?
- 🔴 红色�?天以下或已过�?
- 无截止日期可一直申�?
