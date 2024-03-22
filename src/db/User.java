package db;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String password;
    private String[][] limit = new String[10][5];    //最多记录十张表的权限信息
    private int limitNum = 0;
    //  ----eg----
    //表名    INSERT  UPDATE  DELETE  SELECT
    //course    0       0       0       1
    //0代表权限不受限，1代表权限受限

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        //初始化权限信息，为0即不受限
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                if (j == 0) {
                    limit[i][j] = "#NULL";
                } else {
                    limit[i][j] = "0";
                }
            }
        }
    }

    //查询该用户limit中是否有某表名的权限
    public int findLimitByTablename(String tablename) {
        for (int i = 0; i < 10; i++) {
            if (limit[i][0].equals(tablename)) {
                return i;
            }
        }
        return -1;
    }

    //插入限制
    public boolean insertLimit(int tableIndex) {
        if (limit[tableIndex][1].equals("0")) {
            System.out.println("调用了insertLimit返回为false");
            return false;    //没有插入限制
        } else {
            System.out.println("调用了insertLimit返回为true");
            return true;
        }
    }

    //更新限制
    public boolean updateLimit(int tableIndex) {
        if (limit[tableIndex][2].equals("0")) {
            return false;    //没有更新限制
        } else {
            return true;
        }
    }

    //删除限制
    public boolean deleteLimit(int tableIndex) {
        if (limit[tableIndex][3].equals("0")) {
            return false;    //没有删除限制
        } else {
            return true;
        }
    }

    //查询限制
    public boolean selectLimit(int tableIndex) {
        if (limit[tableIndex][4].equals("0")) {
            return false;    //没有查询限制
        } else {
            return true;
        }
    }

    //增加限制（收回权限）
    public int addlimit(String tablename, String operation) {
        //先看看limit里是不是已经存在了tablename
        int index = findLimitByTablename(tablename);
        if (index == -1) {
            index = limitNum;
            limitNum += 1;
        }
        switch (operation) {
            case "INSERT":
                limit[index][0] = tablename;
                limit[index][1] = "1";
                System.out.println(tablename+"所在下标"+index);
                break;
            case "UPDATE":
                limit[index][0] = tablename;
                limit[index][2] = "1";
                break;
            case "DELETE":
                limit[index][0] = tablename;
                limit[index][3] = "1";
                break;
            case "SELECT":
                limit[index][0] = tablename;
                limit[index][4] = "1";
                break;
            case "ALL":
                limit[index][0] = tablename;
                limit[index][1] = "1";
                limit[index][2] = "1";
                limit[index][3] = "1";
                limit[index][4] = "1";
                break;
            default:
                return -1;
        }
        return 0;
    }

    //删除限制（授予权限）
    public int dellimit(String tablename, String operation) {
        //先看看limit里是不是已经存在了tablename
        int index = findLimitByTablename(tablename);
        if (index == -1) {
            index = limitNum;
            limitNum += 1;
        }
        switch (operation) {
            case "INSERT":
                limit[index][0] = tablename;
                limit[index][1] = "0";
                break;
            case "UPDATE":
                limit[index][0] = tablename;
                limit[index][2] = "0";
                break;
            case "DELETE":
                limit[index][0] = tablename;
                limit[index][3] = "0";
                break;
            case "SELECT":
                limit[index][0] = tablename;
                limit[index][4] = "0";
                break;
            case "ALL":
                limit[index][0] = tablename;
                limit[index][1] = "0";
                limit[index][2] = "0";
                limit[index][3] = "0";
                limit[index][4] = "0";
                break;
            default:
                return -1;
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public boolean isPasswordRight(String password) {
        if (password.equals(this.password)) {
            return true;
        } else {
            return false;
        }
    }
}
