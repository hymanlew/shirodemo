package hyman.demo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public class MyRealm implements Realm{

    @Override
    public String getName() {
        return "myrealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        // 仅支持UsernamePasswordToken类型的Token
        return token instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 接收来自 subject 的用户信息：用户名，密码
        String username = (String)token.getPrincipal();
        String password = new String((char[])token.getCredentials());

        // 在实际环境中，调用数据库数据
        if(!"hyman".equals(username)) {
            throw new UnknownAccountException(); //如果用户名错误
        }
        if(!"hyman".equals(password)) {
            throw new IncorrectCredentialsException(); //如果密码错误
        }
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}
