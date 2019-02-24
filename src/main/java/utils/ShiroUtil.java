package utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

/**
 * shiro 原理流程：
 * SecurityManager（ from SecurityManagerFactory）--->
 * SecurityUtils.setSecurityManager  --->
 * SecurityUtils.getSubject() --->  subject（登录用户主体）
 *
 * UsernamePasswordToken（name，password）--->
 * subject.login（UsernamePasswordToken）
 */
public class ShiroUtil {
    public static Subject login(String confile,String name,String password){

        Factory<SecurityManager> factory = new IniSecurityManagerFactory(confile);
        SecurityManager securityManager = factory.getInstance();

        // 将 securityManager 绑定到 SecurityUtils工具类中
        SecurityUtils.setSecurityManager(securityManager);

        /**
         * 得到当前执行的用户，即认证主体 Subject，包含有两项：
         * principals：身份，可以是用户名，邮件，手机号码等，用于标识一个登录主体身份。
         * credentials：凭证，常见有密码，数字证书等。
         */
        Subject currentUser = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(name,password);

        try {
            // 登录身份验证
            currentUser.login(token);
            System.out.println("登录成功！");
        }catch (AuthenticationException e){
            System.out.println("登录失败！");
            e.printStackTrace();
        }
        return currentUser;
    }
}
