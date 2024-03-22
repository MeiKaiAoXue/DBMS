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


    // 用object以整体的形式写入文件，读取的时候还是以object的形式读取,不用关心文件的layout了
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
