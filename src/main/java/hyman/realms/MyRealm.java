package hyman.realms;

import hyman.dao.UserDao;
import hyman.entity.Roles;
import hyman.entity.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import utils.DBUtil;

import java.sql.Connection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MyRealm extends AuthorizingRealm{

    private UserDao userDao = new UserDao();
    private User baseUser = null;

    @Override
    public String getName() {
        // 这个名字可以随便取
        return "myrealm";
    }

    /**
     * 为当前认证通过的用户提取其对应的角色及权限进行封装
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {

        // 接收已经认证通过的用户信息：用户名，密码
        String username = (String)principal.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Connection con = null;
        try {
            if(baseUser.getName().equals(username)){
                con = DBUtil.getConnection();
                // 设置角色，权限
                List<Roles> roles = userDao.getRoles(con,baseUser.getId());
                Set set = new LinkedHashSet();
                for(Roles roles1:roles){
                    set.add(roles1.getName());
                }
                info.setRoles(set);
                info.setStringPermissions(userDao.getPermis(con,roles));
                return info;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closeConnection();
        }
        return null;
    }

    /**
     * 为当前登录的用户进行身份认证封装
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 接收来自 subject 的用户信息：用户名，密码
        String username = (String)token.getPrincipal();
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            baseUser = userDao.getByname(con,username);
            if(baseUser!=null){
                // 封装认证信息，其内部会自动比对密码是否一致
                AuthenticationInfo info = new SimpleAuthenticationInfo(baseUser.getName(),baseUser.getPassword(),getName());
                return info;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closeConnection();
        }
        return null;
    }
}
