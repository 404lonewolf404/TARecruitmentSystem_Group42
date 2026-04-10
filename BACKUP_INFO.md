# V3.6 职位编辑功能

## 功能说明
补齐CRUD中的"U"（Update）功能，允许MO编辑已创建的职位�?

## 主要更新
- 新增edit-position.jsp编辑页面
- PositionService添加updatePosition方法
- PositionServlet添加编辑处理逻辑
- 职位列表添加"编辑职位"按钮
- 权限验证（只能编辑自己的职位�?

## 修改文件
- 新增：edit-position.jsp
- 修改：PositionService.java, PositionServlet.java
- 修改：positions.jsp（MO添加编辑按钮�?

## 技术要�?
- 可编辑：标题、描述、要求、时长、名额、截止日�?
- 不可编辑：职位ID、MO ID、状态、创建时�?
- 表单验证和错误提�?
- 与create-position保持一致的UI
