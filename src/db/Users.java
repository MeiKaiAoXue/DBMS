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

    //�û����������¼
    public User getUserByLogin(String username,String password){
        User user = null;
        int index = isExist(username);
        if(index!=-1){//�û�������
            //�������
            if (users.get(index).isPasswordRight(password)){
                user = users.get(index);
            }else{
                System.out.println("�������");
            }
        }else{
            System.out.println("�û���������");
        }
        return user;
    }

    //username�Ƿ����
    public int isExist(String username){
        for(int i=0;i< users.size();i++){
            if(username.equals(users.get(i).getName())){
                return i;
            }
        }
        return -1;
    }
}
