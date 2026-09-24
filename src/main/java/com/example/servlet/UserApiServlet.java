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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 JSON API Servlet（第01章第2节 ApiFox 接口测试用）
 *
 * 接口列表：
 *   GET /user/findAll              - 查询所有用户
 *   GET /user/findById?id=1        - 根据 ID 查询用户
 *   GET /user/findByUsername?username=张三 - 根据用户名查询
 *
 * 统一响应格式：
 *   { "code": 200, "message": "查询成功", "data": [...] }
 *   { "code": 500, "message": "错误信息", "data": null }
 */
@WebServlet("/user/*")
public class UserApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 获取路径：/user/findAll → findAll，/user/findById → findById
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        try {
            String json = handleRequest(pathInfo, req);
            out.println(json);
        } catch (Exception e) {
            out.println(buildErrorJson(e.getMessage()));
        }
    }

    private String handleRequest(String pathInfo, HttpServletRequest req) throws Exception {
        String action = pathInfo.replace("/", "");

        switch (action) {
            case "findAll":
                return handleFindAll();
            case "findById":
                return handleFindById(req);
            case "findByUsername":
                return handleFindByUsername(req);
            default:
                return buildErrorJson("未知接口：" + action + "，可用接口：findAll / findById / findByUsername");
        }
    }

    private String handleFindAll() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.findAll();
            return buildSuccessJson("查询成功", users);
        }
    }

    private String handleFindById(HttpServletRequest req) {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            return buildErrorJson("缺少参数：id");
        }
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.findById(Integer.parseInt(idParam));
            if (user == null) {
                return buildErrorJson("用户不存在：id=" + idParam);
            }
            return buildSuccessJson("查询成功", user);
        }
    }

    private String handleFindByUsername(HttpServletRequest req) {
        String username = req.getParameter("username");
        if (username == null || username.isEmpty()) {
            return buildErrorJson("缺少参数：username");
        }
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.findByUsernamePre(username);
            if (user == null) {
                return buildErrorJson("用户不存在：username=" + username);
            }
            return buildSuccessJson("查询成功", user);
        }
    }

    // ==================== JSON 构建工具方法 ====================

    /** 成功响应：{code:200, message, data} */
    private String buildSuccessJson(String message, Object data) {
        return "{\"code\":200,\"message\":\"" + escape(message) + "\",\"data\":" + toJson(data) + "}";
    }

    /** 失败响应：{code:500, message, data:null} */
    private String buildErrorJson(String message) {
        return "{\"code\":500,\"message\":\"" + escape(message) + "\",\"data\":null}";
    }

    /** 简单 JSON 序列化（只处理 User 对象和 List<User>） */
    private String toJson(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }

        if (obj instanceof User) {
            User u = (User) obj;
            // 只输出常用字段，跳过 createTime/updateTime 避免格式问题
            return "{"
                    + "\"id\":" + u.getId() + ","
                    + "\"username\":\"" + escape(u.getUsername()) + "\","
                    + "\"password\":\"" + escape(u.getPassword()) + "\","
                    + "\"email\":\"" + escape(u.getEmail()) + "\""
                    + "}";
        }

        return "\"" + escape(obj.toString()) + "\"";
    }

    /** JSON 字符串转义 */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
