package com.example.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLConfigBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * MyBatis 工具类：单例 SqlSessionFactory + 获取 SqlSession
 *
 * 关键：
 *   1. 使用 MybatisXMLConfigBuilder 解析配置（内部用 MybatisConfiguration），
 *      这样继承 BaseMapper 的接口方法才能被 MP 自动注册实现。
 *   2. 编程式注册分页拦截器，确保 selectPage 生效。
 */
public class MyBatisUtil {

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try (InputStream is = Resources.getResourceAsStream("mybatis-config.xml")) {
            // 1. 用 MP 的 MybatisXMLConfigBuilder 解析 XML
            MybatisXMLConfigBuilder parser = new MybatisXMLConfigBuilder(is);
            MybatisConfiguration configuration = (MybatisConfiguration) parser.parse();

            // 2. 注册 MyBatis-Plus 分页拦截器
            MybatisPlusInterceptor mpInterceptor = new MybatisPlusInterceptor();
            mpInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
            configuration.addInterceptor(mpInterceptor);

            // 3. 构建 SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        } catch (IOException e) {
            throw new RuntimeException("加载 mybatis-config.xml 失败", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public static SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    public static SqlSession openSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }
}
