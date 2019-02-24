package hyman.servletNoSpring;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import sun.font.TrueTypeFont;
import utils.CryptograpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet{

    private static final long serialVersionUID = 1L;

    /**
     * 当访问 shiro_web.ini中配置好的 url路径时，就会经过这里，然后再经过身份验证。
     * 即 http://localhost:8081/admin 没有登录时，会自动访问 /login（shiro_web.ini 配置文件），然后自动跳到
     * login.jsp（doget 方法）。
     *
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("Login doGet！");
        // 转发
        req.getRequestDispatcher("login.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Login doPost！");
        String name = req.getParameter("name");
        String pass = req.getParameter("password");

        // 它是根据自定义的用户配置文件，来得到认证主体的。
        //Subject subject = ShiroUtil.login("","","");

        // 默认是根据 web.xml 中的 shiro 配置文件，来得到认证主体。
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, CryptograpUtil.md5(pass,"hyman"));
        try {
            // 记住我功能，默认有效期为一周。不建议使用这个（即 shiro框架自带的），要自己封装 cookie（用原始 js）。
            //if(subject.isRemembered()){
            //    System.out.println("已经记住当前用户！");
            //}else {
            //    token.setRememberMe(true);
            //}

            // login 方法会调用 realm 进行用户验证
            subject.login(token);
            Session session = subject.getSession();

            /**
             * SessionId，访问主机，session有效期（默认 30 分钟），即：
             * sessionID：959CE46B297142CA84EC74BDD53FD193
             * host：0:0:0:0:0:0:0:1（代理 IP，在实际中是真实的主机 IP）
             * timeAble：1800000（单位是毫秒）
             */
            System.out.println("sessionID："+session.getId());
            System.out.println("host："+session.getHost());
            System.out.println("timeAble："+session.getTimeout());

            /**
             *
             session.getStartTimestamp();
             session.getLastAccessTime();
             获取会话的启动时间及最后访问时间；如果是 JavaSE 应用需要自己定期调用 session.touch() 去更新最后访问时
             间；如果是 Web 应用，每次进入 ShiroFilter 都会自动调用 session.touch() 来更新最后访问时间。

             session.touch();
             session.stop();&nbsp;
             更新会话最后访问时间及销毁会话；当 Subject.logout() 时会自动调用 stop 方法来销毁会话。如果在 web 中，
             调用 javax.servlet.http.HttpSession. invalidate() 也会自动调用 Shiro Session.stop 方法进行销毁 Shiro 的会话。

             session.setAttribute("key", "123");
             Assert.assertEquals("123", session.getAttribute("key"));
             session.removeAttribute("key");
             设置 / 获取 / 删除会话属性；在整个会话范围内都可以对这些属性进行操作。
             Shiro 提供的会话可以用于 JavaSE/JavaEE 环境，不依赖于任何底层容器，可以独立使用，是完整的会话模块。
             */

            resp.sendRedirect("success.jsp");
        }catch (Exception e){
            e.printStackTrace();
            req.setAttribute("error","用户信息错误！");
            // 转发
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }
}
