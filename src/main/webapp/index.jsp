<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>MyBatis Demo</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 720px; margin: 60px auto; padding: 0 20px; color: #333; }
        h1 { color: #2c6fb5; border-bottom: 2px solid #e0e0e0; padding-bottom: 10px; }
        .card { background: #f7f9fc; border: 1px solid #e0e0e0; border-radius: 8px; padding: 20px; margin-top: 20px; }
        a.btn { display: inline-block; background: #2c6fb5; color: #fff; text-decoration: none;
                 padding: 10px 20px; border-radius: 4px; margin-top: 10px; }
        a.btn:hover { background: #1e5a9a; }
        code { background: #eef; padding: 2px 6px; border-radius: 3px; }
    </style>
</head>
<body>
    <h1>MyBatis Demo 首页</h1>
    <div class="card">
        <p>项目已成功部署到 Tomcat 10.x（Jakarta EE）。</p>
        <p>点击下方按钮，通过 MyBatis 查询 <code>user</code> 表：</p>
        <a class="btn" href="${pageContext.request.contextPath}/users">查看用户列表 →</a>
    </div>
    <div class="card">
        <p style="font-size: 13px; color: #666;">
            上下文路径：<code>${pageContext.request.contextPath}</code><br>
            数据库：<code>mybatis_db.user</code>
        </p>
    </div>
</body>
</html>
