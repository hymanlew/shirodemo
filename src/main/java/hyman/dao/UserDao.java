package hyman.dao;

import hyman.entity.Roles;
import hyman.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class UserDao {

    public User getByname(Connection con, String name) throws Exception{
        User user = null;
        String sql = "select * from users where username=?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1,name);
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("username"));
            user.setPassword(rs.getString("password"));
        }
        return user;
    }

    public List<Roles> getRoles(Connection con, Integer userid) throws Exception {
        List<Roles> list = new ArrayList<>();
        Roles roles = null;
        String sql = "select * from user_roles where user_id=?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setInt(1,userid);
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            String sql1 = "select * from roles where id=?";
            PreparedStatement statement1 = con.prepareStatement(sql1);
            statement1.setInt(1,rs.getInt("role_id"));
            ResultSet rs1 = statement1.executeQuery();
            if(rs1.next()){
                roles = new Roles();
                roles.setId(rs1.getInt("id"));
                roles.setName(rs1.getString("name"));
                list.add(roles);
            }
        }
        return list;
    }

    public Set<String> getPermis(Connection con, List<Roles> roles) throws Exception {
        Set set = new LinkedHashSet();
        Roles role = null;
        Iterator iterator = roles.iterator();
        if(iterator.hasNext()){
            role = (Roles) iterator.next();
            String sql = "select * from permission where role_id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1,role.getId());
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                set.add(rs.getString("name"));
            }
        }
        return set;
    }
}
