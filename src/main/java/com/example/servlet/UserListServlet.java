package com.example.servlet;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.util.MyBatisUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 用户列表 Servlet：通过 MyBatis 查询 user 表并以 HTML 表格展示
 * 访问路径：/users
 */
@WebServlet("/users")
public class UserListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">");
        out.println("<title>用户列表 - MyBatis Demo</title>");
        out.println("<style>");
        out.println("body { font-family: 'Microsoft YaHei', sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; color: #333; }");
        out.println("h1 { color: #2c6fb5; border-bottom: 2px solid #e0e0e0; padding-bottom: 10px; }");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        out.println("th, td { border: 1px solid #ddd; padding: 10px 14px; text-align: left; }");
        out.println("th { background: #2c6fb5; color: #fff; }");
        out.println("tr:nth-child(even) { background: #f7f9fc; }");
        out.println("a { color: #2c6fb5; text-decoration: none; }");
        out.println(".err { color: #c0392b; background: #fdecea; padding: 12px; border-radius: 4px; }");
        out.println("</style></head><body>");

        out.println("<h1>用户列表</h1>");
        out.println("<p><a href=\"" + req.getContextPath() + "/\">← 返回首页</a></p>");

        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.findAll();

            if (users == null || users.isEmpty()) {
                out.println("<p class=\"err\">user 表中暂无数据。</p>");
            } else {
                out.println("<table>");
                out.println("<tr><th>ID</th><th>用户名</th><th>密码</th><th>邮箱</th></tr>");
                for (User u : users) {
                    out.println("<tr>");
                    out.println("<td>" + u.getId() + "</td>");
                    out.println("<td>" + u.getUsername() + "</td>");
                    out.println("<td>" + u.getPassword() + "</td>");
                    out.println("<td>" + u.getEmail() + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
                out.println("<p style=\"color:#666;\">共 " + users.size() + " 条记录。</p>");
            }
        } catch (Exception e) {
            out.println("<div class=\"err\">查询失败：" + e.getMessage() + "</div>");
            out.println("<pre style=\"background:#f5f5f5;padding:10px;overflow:auto;\">");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</body></html>");
    }
}
