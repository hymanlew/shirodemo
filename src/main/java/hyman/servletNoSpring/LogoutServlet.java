package hyman.servletNoSpring;

import org.apache.shiro.SecurityUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 直接调用 Subject.logout 即可，退出成功后转发 / 重定向到相应页面即可。
        SecurityUtils.getSubject().logout();
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    /**
     * Shiro 也提供了 logout 拦截器用于退出，其是 org.apache.shiro.web.filter.authc.LogoutFilter 类型的实例，我们可以在
     * shiro_web.ini 配置文件中通过如下配置完成退出：
     *
     * [main]
     * logout.redirectUrl=/login
     * [urls]
     * /logout2=logout
     *
     * 通过 logout.redirectUrl 指定退出后重定向的地址；通过 /logout2=logout 指定退出 url 是 /logout2。这样当我们登录
     * 成功后然后访问 /logout2 即可退出。
     */
}