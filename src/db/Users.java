package db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Users implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<User> users;
    public Users(){
        users = new ArrayList();
        users.add(new User("root","123456"));
    }
    public List<User> getUsers(){
        return users;
    }

    //用户名和密码登录
    public User getUserByLogin(String username,String password){
        User user = null;
        int index = isExist(username);
        if(index!=-1){//用户名存在
            //检查密码
            if (users.get(index).isPasswordRight(password)){
                user = users.get(index);
            }else{
                System.out.println("密码错误");
            }
        }else{
            System.out.println("用户名不存在");
        }
        return user;
    }

    //username是否存在
    public int isExist(String username){
        for(int i=0;i< users.size();i++){
            if(username.equals(users.get(i).getName())){
                return i;
            }
        }
        return -1;
    }
}
