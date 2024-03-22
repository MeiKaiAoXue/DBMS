package view;

import java.io.IOException;

import logic.Analyse;

public class Test {

    public static void main(String[] args) {
       test();
    }

    public static void test(){
        System.out.println("#########DBMS#########");
        while (true) {
            String cmd = readSQL().trim(); // cmdǰ��û�пո�û�зֺ�
            if (cmd.equals("exit"))
                break;

            Analyse analyse = new Analyse(cmd); // ������䴫�������
            String sql = analyse.getSql(); // ��������ʼ��ʱ�Ὣ�����sql����ʽ����������get��������

            // CREATE TABLE���
            if (sql.charAt(0) == 'C' || sql.charAt(0) == 'c') {
                analyse.createTable();
            }

            // ALTER TABLE���
            if (sql.charAt(0) == 'A' || sql.charAt(0) == 'a') {
                analyse.alterTable();
            }

            // DROP TABLE���
            if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'T') || (sql.charAt(0) == 'd' && sql.charAt(5) == 't')) {
                analyse.dropTable();
            }

            // INSERT INTO���
            if (sql.charAt(0) == 'I' || sql.charAt(0) == 'i') {
                analyse.insertInto();
            }

            // UPDATE���
            if (sql.charAt(0) == 'U' || sql.charAt(0) == 'u') {
                analyse.update();
            }

            // DELETE���
            if ((sql.charAt(0) == 'D' && sql.charAt(1) == 'E') || (sql.charAt(0) == 'd' && sql.charAt(1) == 'e')) {
                analyse.delete();
            }

            // SELECT���
            if (sql.charAt(0) == 'S' || sql.charAt(0) == 's') {
                analyse.select();
            }

//            // CREATE USER���
//            if ((sql.charAt(0) == 'C' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'c' && sql.charAt(5) == 'u')) {
//                analyse.createUser();
//            }
//
//            // DROP USER���
//            if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'd' && sql.charAt(5) == 'u')) {
//                analyse.dropUser();
//            }
//
//            // GRANT���
//            if (sql.charAt(0) == 'G' || sql.charAt(0) == 'g') {
//                analyse.grant();
//            }
//
//            // REVOKE���
//            if (sql.charAt(0) == 'R' || sql.charAt(0) == 'r') {
//                analyse.revoke();
//            }
        }
    }

    // ����sql��䣬������һ���ַ���������ַ�����û�зֺ�
    public static String readSQL() {
        String sql = "";
        try {
            char ch = 0;
            do {
                ch = (char) System.in.read();
                if (ch == ';')
                    break;
                sql += ch;
            } while (ch != ';');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sql;
    }

    public static void testForGui(String str){
        String cmd = str.replaceAll(";","").trim();
        Analyse analyse = new Analyse(cmd); // ������䴫�������
        String sql = analyse.getSql(); // ��������ʼ��ʱ�Ὣ�����sql����ʽ����������get��������

        // CREATE TABLE���
        if (sql.charAt(0) == 'C' || sql.charAt(0) == 'c') {
            analyse.createTable();
        }

        // ALTER TABLE���
        if (sql.charAt(0) == 'A' || sql.charAt(0) == 'a') {
            analyse.alterTable();
        }

        // DROP TABLE���
        if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'T') || (sql.charAt(0) == 'd' && sql.charAt(5) == 't')) {
            analyse.dropTable();
        }

        // INSERT INTO���
        if (sql.charAt(0) == 'I' || sql.charAt(0) == 'i') {
            analyse.insertInto();
        }

        // UPDATE���
        if (sql.charAt(0) == 'U' || sql.charAt(0) == 'u') {
            analyse.update();
        }

        // DELETE���
        if ((sql.charAt(0) == 'D' && sql.charAt(1) == 'E') || (sql.charAt(0) == 'd' && sql.charAt(1) == 'e')) {
            analyse.delete();
        }

        // SELECT���
        if (sql.charAt(0) == 'S' || sql.charAt(0) == 's') {
            analyse.select();
        }

//            // CREATE USER���
//            if ((sql.charAt(0) == 'C' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'c' && sql.charAt(5) == 'u')) {
//                analyse.createUser();
//            }
//
//            // DROP USER���
//            if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'd' && sql.charAt(5) == 'u')) {
//                analyse.dropUser();
//            }
//
//            // GRANT���
//            if (sql.charAt(0) == 'G' || sql.charAt(0) == 'g') {
//                analyse.grant();
//            }
//
//            // REVOKE���
//            if (sql.charAt(0) == 'R' || sql.charAt(0) == 'r') {
//                analyse.revoke();
//            }
    }
}
