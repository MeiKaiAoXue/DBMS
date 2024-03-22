package db;

import java.io.*;


public class FileIO {

    private static final String DBname="database";
    private static final String Usersname="users";


    public static Database readDatabase(){
        try {
            FileInputStream fis = new FileInputStream(DBname);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Database database = (Database) ois.readObject();
            ois.close();
            fis.close();
            return database;
        } catch (FileNotFoundException e) {

        } catch (ClassNotFoundException e) {

        } catch (IOException e) {

        }
        return null;
    }

    public static Users readUsers(){
        try {
            FileInputStream fis = new FileInputStream(Usersname);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Users users = (Users) ois.readObject();
            ois.close();
            fis.close();
            return users;
        } catch (FileNotFoundException e) {

        } catch (ClassNotFoundException e) {

        } catch (IOException e) {

        }
        return null;
    }

    public static void writeDatabase(Database database){
        try {
            FileOutputStream fos = new FileOutputStream(DBname);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(database);
            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // ��object���������ʽд���ļ�����ȡ��ʱ������object����ʽ��ȡ,���ù����ļ���layout��
    public static void writeUsers(Users users){
        try {
            FileOutputStream fos = new FileOutputStream(Usersname);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(users);
            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
