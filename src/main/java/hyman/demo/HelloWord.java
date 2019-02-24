package hyman.demo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;

/**
 * shiro 验证流程（查看图片）：
 * Subject（login(token)）---> securityManager --->  Authenticator（访问策略）--->  Realm,JDBC Realm
 *
 * Realm：指域，shiro 从 realm 中获取验证数据。它分很多种类，如：jdbc realm，indi realm，text realm。在本例中，是指在
 *         shiro.ini 中固定的用户密码信息数据（属于 text realm）。
 *
 * 权限认证核心要素：
 * 权限认证：即访问控制，即在应用中控制谁能访问哪些资源。其最核心的三个要素是：权限，角色，用户。
 *
 * 权限：即操作资源的权利，例如访问页面，及对页面的修改。
 * 角色：是权限的集合，一个角色可以包含多种权限。
 * 用户：在 shiro 中，代表访问系统的用户，即 subject 认证主体。
 *
 * 授权：分为编程式，注解式，jsp 标签授权三种。
 * 编程式：分为基于角色的访问控制，基于权限的访问控制。（DemoTest 类）
 * 注解式：（DemoTest 类）。
 * jsp 标签授权：（DemoTest.jsp）。
 */
public class HelloWord {

    public static void main(String[] args) {
        /**
         * 读取配置文件，初始化 securimanager 工厂，使用固定的用户信息
         * factory 安全实例（securimanager）工厂，securimanager 管理当前登录用户，
         */
        //Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");

        // 使用数据库连接，得到用户信息
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:jdbc_realm.ini");
        SecurityManager securityManager = factory.getInstance();

        // 将 securityManager 绑定到 SecurityUtils工具类中
        SecurityUtils.setSecurityManager(securityManager);

        /**
         * 得到当前执行的用户，即认证主体 Subject，包含有两项：
         * principals：身份，可以是用户名，邮件，手机号码等，用于标识一个登录主体身份。
         * credentials：凭证，常见有密码，数字证书等。
         */
        Subject currentUser = SecurityUtils.getSubject();

        /**
         * 创建 token 令牌，即用户名/密码
         *
         * 这里必须要注意，username,password 字段是固定死的，所以在数据库中的表名及列名也都是固定的。
         * 即（users ，username，password）
         */
        UsernamePasswordToken token = new UsernamePasswordToken("hyman","hyman");

        try {
            // 登录身份验证
            currentUser.login(token);
            System.out.println("登录成功！");
        }catch (AuthenticationException e){
            /**
             * 如果身份验证失败请捕获 AuthenticationException 或其子类，常见的如：
             * DisabledAccountException（禁用的帐号）、
             * LockedAccountException（锁定的帐号）、
             * UnknownAccountException（错误的帐号）、
             * ExcessiveAttemptsException（登录失败次数过多）、
             * IncorrectCredentialsException （错误的凭证）、
             * ExpiredCredentialsException（过期的凭证）等，
             *
             * 具体请查看其继承关系；对于页面的错误消息展示，最好使用如 “用户名 / 密码错误” 而不是 “用户名错误”/“密码错误”，
             * 防止一些恶意用户非法扫描帐号库；
             */
            System.out.println("登录失败！");
            e.printStackTrace();
        }
        // 断言用户已经登录
        Assert.assertEquals(true,currentUser.isAuthenticated());

        // 最后调用 subject.logout 退出，其会自动委托给 SecurityManager.logout 方法退出。
        currentUser.logout();
    }

    /**
     * securityManager.realms=$myRealm1,$myRealm2
     *
     * 如果在 shiro 配置文件中如果声明了多个 realm，则 securityManager 会按照 realms 指定的顺序进行身份认证。如果删
     * 除 “securityManager.realms=$myRealm1,$myRealm2”，则 securityManager 会按照 realm 声明的顺序进行使用（即无
     * 需设置 realms 属性，它会自动发现）。
     * 当我们显示指定 realm 后，其他没有指定的 realm 将被忽略，如“securityManager.realms=$myRealm1”，那么 myRealm2
     * 不会被自动设置进去。
     */
}
