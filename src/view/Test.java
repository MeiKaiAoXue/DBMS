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
            String cmd = readSQL().trim(); // cmd前后没有空格，没有分号
            if (cmd.equals("exit"))
                break;

            Analyse analyse = new Analyse(cmd); // 将该语句传入解析器
            String sql = analyse.getSql(); // 解析器初始化时会将传入的sql语句格式化，这里再get回来更新

            // CREATE TABLE语句
            if (sql.charAt(0) == 'C' || sql.charAt(0) == 'c') {
                analyse.createTable();
            }

            // ALTER TABLE语句
            if (sql.charAt(0) == 'A' || sql.charAt(0) == 'a') {
                analyse.alterTable();
            }

            // DROP TABLE语句
            if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'T') || (sql.charAt(0) == 'd' && sql.charAt(5) == 't')) {
                analyse.dropTable();
            }

            // INSERT INTO语句
            if (sql.charAt(0) == 'I' || sql.charAt(0) == 'i') {
                analyse.insertInto();
            }

            // UPDATE语句
            if (sql.charAt(0) == 'U' || sql.charAt(0) == 'u') {
                analyse.update();
            }

            // DELETE语句
            if ((sql.charAt(0) == 'D' && sql.charAt(1) == 'E') || (sql.charAt(0) == 'd' && sql.charAt(1) == 'e')) {
                analyse.delete();
            }

            // SELECT语句
            if (sql.charAt(0) == 'S' || sql.charAt(0) == 's') {
                analyse.select();
            }

//            // CREATE USER语句
//            if ((sql.charAt(0) == 'C' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'c' && sql.charAt(5) == 'u')) {
//                analyse.createUser();
//            }
//
//            // DROP USER语句
//            if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'd' && sql.charAt(5) == 'u')) {
//                analyse.dropUser();
//            }
//
//            // GRANT语句
//            if (sql.charAt(0) == 'G' || sql.charAt(0) == 'g') {
//                analyse.grant();
//            }
//
//            // REVOKE语句
//            if (sql.charAt(0) == 'R' || sql.charAt(0) == 'r') {
//                analyse.revoke();
//            }
        }
    }

    // 读入sql语句，并返回一个字符串，这个字符串中没有分号
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
        Analyse analyse = new Analyse(cmd); // 将该语句传入解析器
        String sql = analyse.getSql(); // 解析器初始化时会将传入的sql语句格式化，这里再get回来更新

        // CREATE TABLE语句
        if (sql.charAt(0) == 'C' || sql.charAt(0) == 'c') {
            analyse.createTable();
        }

        // ALTER TABLE语句
        if (sql.charAt(0) == 'A' || sql.charAt(0) == 'a') {
            analyse.alterTable();
        }

        // DROP TABLE语句
        if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'T') || (sql.charAt(0) == 'd' && sql.charAt(5) == 't')) {
            analyse.dropTable();
        }

        // INSERT INTO语句
        if (sql.charAt(0) == 'I' || sql.charAt(0) == 'i') {
            analyse.insertInto();
        }

        // UPDATE语句
        if (sql.charAt(0) == 'U' || sql.charAt(0) == 'u') {
            analyse.update();
        }

        // DELETE语句
        if ((sql.charAt(0) == 'D' && sql.charAt(1) == 'E') || (sql.charAt(0) == 'd' && sql.charAt(1) == 'e')) {
            analyse.delete();
        }

        // SELECT语句
        if (sql.charAt(0) == 'S' || sql.charAt(0) == 's') {
            analyse.select();
        }

//            // CREATE USER语句
//            if ((sql.charAt(0) == 'C' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'c' && sql.charAt(5) == 'u')) {
//                analyse.createUser();
//            }
//
//            // DROP USER语句
//            if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'd' && sql.charAt(5) == 'u')) {
//                analyse.dropUser();
//            }
//
//            // GRANT语句
//            if (sql.charAt(0) == 'G' || sql.charAt(0) == 'g') {
//                analyse.grant();
//            }
//
//            // REVOKE语句
//            if (sql.charAt(0) == 'R' || sql.charAt(0) == 'r') {
//                analyse.revoke();
//            }
    }
}
