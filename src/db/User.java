package db;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String password;
    private String[][] limit = new String[10][5];    //����¼ʮ�ű��Ȩ����Ϣ
    private int limitNum = 0;
    //  ----eg----
    //����    INSERT  UPDATE  DELETE  SELECT
    //course    0       0       0       1
    //0����Ȩ�޲����ޣ�1����Ȩ������

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        //��ʼ��Ȩ����Ϣ��Ϊ0��������
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

    //��ѯ���û�limit���Ƿ���ĳ������Ȩ��
    public int findLimitByTablename(String tablename) {
        for (int i = 0; i < 10; i++) {
            if (limit[i][0].equals(tablename)) {
                return i;
            }
        }
        return -1;
    }

    //��������
    public boolean insertLimit(int tableIndex) {
        if (limit[tableIndex][1].equals("0")) {
            System.out.println("������insertLimit����Ϊfalse");
            return false;    //û�в�������
        } else {
            System.out.println("������insertLimit����Ϊtrue");
            return true;
        }
    }

    //��������
    public boolean updateLimit(int tableIndex) {
        if (limit[tableIndex][2].equals("0")) {
            return false;    //û�и�������
        } else {
            return true;
        }
    }

    //ɾ������
    public boolean deleteLimit(int tableIndex) {
        if (limit[tableIndex][3].equals("0")) {
            return false;    //û��ɾ������
        } else {
            return true;
        }
    }

    //��ѯ����
    public boolean selectLimit(int tableIndex) {
        if (limit[tableIndex][4].equals("0")) {
            return false;    //û�в�ѯ����
        } else {
            return true;
        }
    }

    //�������ƣ��ջ�Ȩ�ޣ�
    public int addlimit(String tablename, String operation) {
        //�ȿ���limit���ǲ����Ѿ�������tablename
        int index = findLimitByTablename(tablename);
        if (index == -1) {
            index = limitNum;
            limitNum += 1;
        }
        switch (operation) {
            case "INSERT":
                limit[index][0] = tablename;
                limit[index][1] = "1";
                System.out.println(tablename+"�����±�"+index);
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

    //ɾ�����ƣ�����Ȩ�ޣ�
    public int dellimit(String tablename, String operation) {
        //�ȿ���limit���ǲ����Ѿ�������tablename
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
