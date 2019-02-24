package hyman.demo;

import org.apache.shiro.subject.Subject;
import org.junit.Test;
import utils.ShiroUtil;

import java.util.ArrayList;
import java.util.List;

public class RealmTest {

    // 编程式授权：基于角色的访问控制
    @Test
    public void testRoles(){
        Subject currenUser = ShiroUtil.login("classpath:shiro_role.ini","hyman","hyman");
        //Subject currenUser = ShiroUtil.login("classpath:shiro_role.ini","test","test");
        String result = currenUser.hasRole("role2")?"有角色 1":"没有角色 1";
        System.out.println(result);

        List<String> list = new ArrayList<>();
        list.add("role2");
        list.add("role3");

        // 其内部实现是，一个一个角色进行判断，即 for 循环调用 hasRole 方法。
        boolean[] booleans = currenUser.hasRoles(list);
        result = booleans[0]?"有角色 2":"无角色 2";

        System.out.println(result);
        System.out.println(booleans[1]?"有角色 3":"无角色 3");

        // 只有所有角色都为 true 时，才为 true
        result = currenUser.hasAllRoles(list)?"有相关角色":"没有相关角色";
        System.out.println(result);

        currenUser.logout();
    }

    @Test
    public void checkRoles(){
        Subject currentUser = ShiroUtil.login("classpath:shiro_role.ini","hyman","hyman");

        // 该方法没有返回值，只有当没有相关角色权限时才会抛出异常。
        currentUser.checkRole("role2");
        //  Subject does not have role [role3]
        currentUser.checkRole("role3");

        currentUser.logout();
    }


    // 编程式授权：基于权限的访问控制
    @Test
    public void isPermitted(){
        //Subject currenUser = ShiroUtil.login("classpath:shiro_permission.ini","hyman","hyman");
        Subject currenUser = ShiroUtil.login("classpath:shiro_permission.ini","test","test");
        String result = currenUser.isPermitted("user:select")?"有查询权限":"没有查询权限";

        // 如果在数据库中是没有定义相关权限，则全部角色都会没有权限
        System.out.println(result);

        result = currenUser.isPermitted("user:update")?"有更新权限":"没有更新权限";
        System.out.println(result);

        List<String> list = new ArrayList<>();
        list.add("user:select");
        list.add("user:update");
        // 输出是数组类型的字符串，所以不能用
        System.out.println(list.toString());

        // isPermittedAll() 参数是多个字符串，其返回值与 hasAllRoles 相同。
        boolean[] booleans = currenUser.isPermitted("user:select","user:update");
        System.out.println(booleans[0]?"有查询权限":"没有查询权限");
        System.out.println(booleans[1]?"有更新权限":"没有更新权限");

        // checkPermission() 也与 checkRole() 相同。
        currenUser.logout();
    }

    // 注解式授权：
    public void method(){
        /**
         * @RequiresAuthentication 要求当前 subject 已经在当前的 session中被验证通过才能被访问和调用。
         *              即：SecurityUtils.getSubject().isAuthenticated()。
         *
         * @RequiresGuest 要求当前的 subject是一个‘guest’，即他们必须是在之前的 session中没有被验证或
         *              被记住才能被访问或调用。即 SecurityUtils.getSubject()..getPrincipals() = null。
         *
         * @RequiresPermissions("user:select") 要求当前的 subject拥有相关权限才能访问。一个权限（user:select）,
         *              多个权限（value = {"11","22"}）。
         *
         * @RequiresRoles("hyman") 要求当前登录的主体拥有相关的角色才能访问。如果没有，则会抛 authorizationexception
         *              异常。多个角色（value = {"hyman","test"}）。
         *
         * @RequiresUser 它需要当前的登录主体是一个应用程序用户才能访问和调用。应用程序用户是指：拥有已知身份，或在当前
         *              session 中由于通过验证被确认，或在之前的 session 中的 rememberme服务被记住。
         */
    }

   public static void main(String[] args) {
       /**
        *
        role=*:view
        role=user:*:1
        如 “user:view” 等价于 “user:view:*” ；而 “organization” 等价于 “organization:*” 或者 “organization:*:*” 。可以
        这么理解，这种方式实现了前缀匹配。

        即首先根据用户名找到角色，然后根据角色再找到权限；即角色是权限集合；Shiro 同样不进行权限的维护，需要我们通过 Realm
        返回相应的权限信息。只需要维护“用户— —角色”之间的关系即可。

        即用户 - 角色之间是多对多关系，角色 - 权限之间是多对多关系；且用户和权限之间通过角色建立关系；在系统中验证时通过权限
        验证，角色只是权限集合，即所谓的显示角色；其实权限应该对应到资源（如菜单、URL、页面按钮、Java方法等）中，即应该将权限
        字符串存储到资源实体中，但是目前为了简单化，直接提取一个权限表。

        用户实体包括：编号 (id)、用户名 (username)、密码(password)、盐 (salt)、是否锁定 (locked)；是否锁定用于封禁用户使用，
        其实最好使用 Enum 字段存储，可以实现更复杂的用户状态实现。 角色实体包括：、编号 (id)、角色标识符（role）、描述
        （description）、是否可用（available）；其中角色标识符用于在程序中进行隐式角色判断的，描述用于以后再前台界面显示的、
        是否可用表示角色当前是否激活。
        权限实体包括：编号（id）、权限标识符（permission）、描述（description）、是否可用（available）；含义和角色实体类似。

        另外还有两个关系实体：用户 - 角色实体（用户编号、角色编号，且组合为复合主键）；角色 - 权限实体（角色编号、权限编号，
        且组合为复合主键）。

        性能问题：
        通配符匹配方式比字符串相等匹配来说是更复杂的，因此需要花费更长时间，但是一般系统的权限不会太多，且可以配合缓存来提供其
        性能，如果这样性能还达不到要求我们可以实现位操作算法实现性能更好的权限匹配。另外实例级别的权限验证如果数据量太大也不建
        议使用，可能造成查询权限及匹配变慢。可以考虑比如在sql查询时加上权限字符串之类的方式在查询时就完成了权限匹配。
        */
   }
}
