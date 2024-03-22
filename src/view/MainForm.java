package view;

import db.User;
import db.Users;
import logic.Analyse;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static db.FileIO.readUsers;

public class MainForm implements ActionListener {


    private User user;
    private JPanel root;
    private JTextArea sqlTextArea;
    private JTextArea consoleTextArea;
    private JTextArea resultTextArea;
    private JButton runButton;
    private JLabel label1;
    private JLabel userLabel;
    private JScrollPane consoleScroll;

    //定义菜单有关的一些对象
    JMenuBar menuBar;//菜单栏
    JMenu createTableMenu;//设置窗体背景颜色的菜单
    JMenuItem[] createTableMenuItems;//设置窗体背景颜色的菜单项数组
    JMenu deleteTableMenu;//设置窗体背景颜色的菜单
    JMenuItem[] deleteTableMenuItems;//设置窗体背景颜色的菜单项数组
    JMenu alterTableMenu;//设置窗体背景颜色的菜单
    JMenuItem[] alterTableMenuItems;//设置窗体背景颜色的菜单项数组
    JMenu insertMenu1;//设置窗体背景颜色的菜单
    JMenuItem[] insertMenuItems1;//设置窗体背景颜色的菜单项数组
    JMenu insertMenu2;//设置窗体背景颜色的菜单
    JMenuItem[] insertMenuItems2;//设置窗体背景颜色的菜单项数组
    JMenu insertMenu3;//设置窗体背景颜色的菜单
    JMenuItem[] insertMenuItems3;//设置窗体背景颜色的菜单项数组
    JMenu updateMenu;//设置窗体背景颜色的菜单
    JMenuItem[] updateMenuItems;//设置窗体背景颜色的菜单项数组
    JMenu deleteMenu;//设置窗体背景颜色的菜单
    JMenuItem[] deleteMenuItems;//设置窗体背景颜色的菜单项数组
    JMenu selectMenu;//设置窗体背景颜色的菜单
    JMenuItem[] selectMenuItems;//设置窗体背景颜色的菜单项数组
    JMenu userMenu;//设置窗体背景颜色的菜单
    JMenuItem[] userMenuItems;//设置窗体背景颜色的菜单项数组
    JMenu limitMenu;//设置窗体背景颜色的菜单
    JMenuItem[] limitMenuItems;//设置窗体背景颜色的菜单项数组

    public MainForm(User user,JFrame frame) {
        this.user = user;

        userLabel.setText("当前用户：" + user.getName());

        //JTextArea自动换行
        consoleTextArea.setLineWrap(true);
        consoleTextArea.setWrapStyleWord(true);

        initMenu();
        //窗体设置菜单栏
        frame.setJMenuBar(menuBar);

        //执行
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = sqlTextArea.getText();
                processSql(sql);
            }
        });
    }

    public JPanel getRoot() {
        return root;
    }

    public void printConsole(String str) {
        consoleTextArea.append(str + "\n");
    }

    public void printResult(String str) {
        resultTextArea.append(str);
    }

    public void flushResult() {
        resultTextArea.setText("");
    }

    public void processSql(String str) {
        String cmd = str.replaceAll(";", "").trim();
        Analyse analyse = new Analyse(cmd, this); // 将该语句传入解析器
        String sql = analyse.getSql(); // 解析器初始化时会将传入的sql语句格式化，这里再get回来更新

        // CREATE TABLE语句
        if ((sql.charAt(0) == 'C' && sql.charAt(7) == 'T') || (sql.charAt(0) == 'c' && sql.charAt(7) == 't')) {
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

        // CREATE USER语句
        if ((sql.charAt(0) == 'C' && sql.charAt(7) == 'U') || (sql.charAt(0) == 'c' && sql.charAt(7) == 'u')) {
            analyse.createUser();
        }

        // DROP USER语句
        if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'd' && sql.charAt(5) == 'u')) {
            analyse.dropUser();
        }

        // GRANT语句
        if (sql.charAt(0) == 'G' || sql.charAt(0) == 'g') {
            analyse.grant();
        }

        // REVOKE语句
        if (sql.charAt(0) == 'R' || sql.charAt(0) == 'r') {
            analyse.revoke();
        }
    }

    public User getUser() {
        Users users = readUsers();
        int index = users.isExist(user.getName());
        return users.getUsers().get(index);
    }

    private void initMenu(){
        //菜单有关
        menuBar=new JMenuBar();//菜单栏

        //创建表菜单
        createTableMenu=new JMenu("创建表");
        String[] itemsText1={"创建s表","创建course表","创建sc表"};
        createTableMenuItems=new JMenuItem[3];  //菜单项数组
        for(int i=0;i<3;i++){
            createTableMenuItems[i]=new JMenuItem(itemsText1[i]);
            createTableMenuItems[i].addActionListener(this);
            createTableMenu.add(createTableMenuItems[i]);
        }
        menuBar.add(createTableMenu);//将菜单添加到菜单栏上

        //删除表菜单
        deleteTableMenu=new JMenu("删除表");
        String[] itemsText2={"删除s表"};
        deleteTableMenuItems=new JMenuItem[1];  //菜单项数组
        for(int i=0;i<1;i++){
            deleteTableMenuItems[i]=new JMenuItem(itemsText2[i]);
            deleteTableMenuItems[i].addActionListener(this);
            deleteTableMenu.add(deleteTableMenuItems[i]);
        }
        menuBar.add(deleteTableMenu);//将菜单添加到菜单栏上

        //修改表菜单
        alterTableMenu=new JMenu("修改表");
        String[] itemsText3={"删除s表sex列","添加s表sex列"};
        alterTableMenuItems=new JMenuItem[2];  //菜单项数组
        for(int i=0;i<2;i++){
            alterTableMenuItems[i]=new JMenuItem(itemsText3[i]);
            alterTableMenuItems[i].addActionListener(this);
            alterTableMenu.add(alterTableMenuItems[i]);
        }
        menuBar.add(alterTableMenu);//将菜单添加到菜单栏上

        //插入菜单
        insertMenu1=new JMenu("插入s");
        String[] itemsText41={"20301114 Panzhihong","20301117 Wanglin","20301116 Qizikang","20301097 Fanchengwei","20301118 Wangshuxin"};
        insertMenuItems1=new JMenuItem[5];  //菜单项数组
        for(int i=0;i<5;i++){
            insertMenuItems1[i]=new JMenuItem(itemsText41[i]);
            insertMenuItems1[i].addActionListener(this);
            insertMenu1.add(insertMenuItems1[i]);
        }
        menuBar.add(insertMenu1);//将菜单添加到菜单栏上

        //插入菜单
        insertMenu2=new JMenu("插入course");
        String[] itemsText42={"801,Database_System","802,Discrete_mathematics","803,Computer_network"};
        insertMenuItems2=new JMenuItem[3];  //菜单项数组
        for(int i=0;i<3;i++){
            insertMenuItems2[i]=new JMenuItem(itemsText42[i]);
            insertMenuItems2[i].addActionListener(this);
            insertMenu2.add(insertMenuItems2[i]);
        }
        menuBar.add(insertMenu2);//将菜单添加到菜单栏上

        //插入菜单
        insertMenu3=new JMenu("插入sc");
        String[] itemsText43={"20301114,801,99","20301114,802,98","20301117,802,97","20301117,803,95","20301116,802,92","20301097,801,88","20301118,801,96","20301118,802,94","20301118,803,90"};
        insertMenuItems3=new JMenuItem[9];  //菜单项数组
        for(int i=0;i<9;i++){
            insertMenuItems3[i]=new JMenuItem(itemsText43[i]);
            insertMenuItems3[i].addActionListener(this);
            insertMenu3.add(insertMenuItems3[i]);
        }
        menuBar.add(insertMenu3);//将菜单添加到菜单栏上

        //更新菜单
        updateMenu=new JMenu("更新");
        String[] itemsText5={"修改课程801学分为6","尝试两个802课程号","尝试在sc中801改为805"};
        updateMenuItems=new JMenuItem[3];  //菜单项数组
        for(int i=0;i<3;i++){
            updateMenuItems[i]=new JMenuItem(itemsText5[i]);
            updateMenuItems[i].addActionListener(this);
            updateMenu.add(updateMenuItems[i]);
        }
        menuBar.add(updateMenu);//将菜单添加到菜单栏上

        //删除菜单
        deleteMenu=new JMenu("删除");
        String[] itemsText6={"20301114 801"};
        deleteMenuItems=new JMenuItem[1];  //菜单项数组
        for(int i=0;i<1;i++){
            deleteMenuItems[i]=new JMenuItem(itemsText6[i]);
            deleteMenuItems[i].addActionListener(this);
            deleteMenu.add(deleteMenuItems[i]);
        }
        menuBar.add(deleteMenu);//将菜单添加到菜单栏上

        //查询菜单
        selectMenu=new JMenu("查询");
        String[] itemsText7={"s全部","course全部","sc全部","s与sc JOIN","sc801或803","sc非802","sc按成绩排序"};
        selectMenuItems=new JMenuItem[7];  //菜单项数组
        for(int i=0;i<7;i++){
            selectMenuItems[i]=new JMenuItem(itemsText7[i]);
            selectMenuItems[i].addActionListener(this);
            selectMenu.add(selectMenuItems[i]);
        }
        menuBar.add(selectMenu);//将菜单添加到菜单栏上

        //用户菜单
        userMenu=new JMenu("用户");
        String[] itemsText8={"创建razor密码123","删除razor"};
        userMenuItems=new JMenuItem[2];  //菜单项数组
        for(int i=0;i<2;i++){
            userMenuItems[i]=new JMenuItem(itemsText8[i]);
            userMenuItems[i].addActionListener(this);
            userMenu.add(userMenuItems[i]);
        }
        menuBar.add(userMenu);//将菜单添加到菜单栏上

        //权限菜单
        limitMenu=new JMenu("权限");
        String[] itemsText9={"收回PUBLIC的SELECT","授予PUBLIC的SELECT"};
        limitMenuItems=new JMenuItem[2];  //菜单项数组
        for(int i=0;i<2;i++){
            limitMenuItems[i]=new JMenuItem(itemsText9[i]);
            limitMenuItems[i].addActionListener(this);
            limitMenu.add(limitMenuItems[i]);
        }
        menuBar.add(limitMenu);//将菜单添加到菜单栏上
    }
    public void actionPerformed(ActionEvent e) {
        AbstractButton item=(AbstractButton)e.getSource();
        //将动作事件的事件源强制转换为抽象的按钮对象
        //创建
        if(item.getText().equals("创建s表")) {
            sqlTextArea.setText("CREATE TABLE s(\n" +
                    "    sno SMALLINT,\n" +
                    "    sname CHAR(20),\n" +
                    "    age SMALLINT,\n" +
                    "    sex CHAR(1),\n" +
                    "    PRIMARY KEY(sno)\n" +
                    ");");
        }
        if(item.getText().equals("创建course表")) {
            sqlTextArea.setText("CREARE TABLE course (\n" +
                    "    cno SMALLINT,\n" +
                    "    cname CHAR(15),\n" +
                    "    credit SMALLINT,\n" +
                    "    PRIMARY KEY(cno)\n" +
                    ");");
        }
        if(item.getText().equals("创建sc表")) {
            sqlTextArea.setText("CREARE TABLE sc(\n" +
                    "    sno SMALLINT,\n" +
                    "    cno SMALLINT,\n" +
                    "    grade SMALLINT,\n" +
                    "    PRIMARY KEY (sno,cno),\n" +
                    "    FOREIGN KEY (sno)REFERENCES s(sno),\n" +
                    "    FOREIGN KEY (cno)REFERENCES course(cno)\n" +
                    ");");
        }
        //删除
        if(item.getText().equals("删除s表")) {
            sqlTextArea.setText("DROP TABLE s;");
        }
        //修改
        if(item.getText().equals("删除s表sex列")) {
            sqlTextArea.setText("ALTER TABLE s\n" +
                    "DROP sex;");
        }
        if(item.getText().equals("添加s表sex列")) {
            sqlTextArea.setText("ALTER TABLE s\n" +
                    "ADD sex CHAR(1);");
        }
        //插入
        //s
        if(item.getText().equals("20301114 Panzhihong")) {
            sqlTextArea.setText("INSERT INTO s(sno,sname,age,sex)\n" +
                    "VALUES(20301114,Panzhihong,22,m);");
        }
        if(item.getText().equals("20301117 Wanglin")) {
            sqlTextArea.setText("INSERT INTO s(sno,sname,age,sex)\n" +
                    "VALUES(20301117,Wanglin,25,m);");
        }
        if(item.getText().equals("20301116 Qizikang")) {
            sqlTextArea.setText("INSERT INTO s(sno,sname,age,sex)\n" +
                    "VALUES(20301116,Qizikang,21,m);");
        }
        if(item.getText().equals("20301097 Fanchengwei")) {
            sqlTextArea.setText("INSERT INTO s(sno,sname,age,sex)\n" +
                    "VALUES(20301097,Fanchengwei,24,m);");
        }
        if(item.getText().equals("20301118 Wangshuxin")) {
            sqlTextArea.setText("INSERT INTO s(sno,sname,age,sex)\n" +
                    "VALUES(20301118,Wangshuxin,18,f);");
        }
        //course
        if(item.getText().equals("801,Database_System")) {
            sqlTextArea.setText("INSERT INTO course(cno,cname,credit)\n" +
                    "VALUES(801,Database_System,4);");
        }
        if(item.getText().equals("802,Discrete_mathematics")) {
            sqlTextArea.setText("INSERT INTO course(cno,cname,credit)\n" +
                    "VALUES(802,Discrete_mathematics,3);");
        }
        if(item.getText().equals("803,Computer_network")) {
            sqlTextArea.setText("INSERT INTO course(cno,cname,credit)\n" +
                    "VALUES(803,Computer_network,2);");
        }
        //sc
        if(item.getText().equals("20301114,801,99")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301114,801,99);");
        }
        if(item.getText().equals("20301114,802,98")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301114,802,98);");
        }
        if(item.getText().equals("20301117,802,97")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301117,802,97);");
        }
        if(item.getText().equals("20301117,803,95")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301117,803,95);");
        }
        if(item.getText().equals("20301116,802,92")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301116,802,92);");
        }
        if(item.getText().equals("20301097,801,88")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301097,801,88);");
        }
        if(item.getText().equals("20301118,801,96")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301118,801,96);");
        }
        if(item.getText().equals("20301118,802,94")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301118,802,94);");
        }
        if(item.getText().equals("20301118,803,90")) {
            sqlTextArea.setText("INSERT INTO sc(sno,cno,grade)\n" +
                    "VALUES(20301118,803,90);");
        }
        //更新
        if(item.getText().equals("修改课程801学分为6")) {
            sqlTextArea.setText("UPDATE course\n" +
                    "SET credit=6\n" +
                    "WHERE cno=801;");
        }
        if(item.getText().equals("尝试两个802课程号")) {
            sqlTextArea.setText("UPDATE course\n" +
                    "SET cno=802\n" +
                    "WHERE cname=Database_System;");
        }
        if(item.getText().equals("尝试在sc中801改为805")) {
            sqlTextArea.setText("UPDATE sc\n" +
                    "SET cno=805\n" +
                    "WHERE sno=20301114 AND cno=801;");
        }
        //删除
        if(item.getText().equals("20301114 801")) {
            sqlTextArea.setText("DELETE FROM sc\n" +
                    "WHERE sno=20301114 AND cno=801;");
        }
        //查询
        if(item.getText().equals("s全部")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM s;");
        }
        if(item.getText().equals("course全部")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM course;");
        }
        if(item.getText().equals("sc全部")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc;");
        }
        if(item.getText().equals("s与sc JOIN")) {
            sqlTextArea.setText("SELECT sname,cno,grade\n" +
                    "FROM s JOIN sc ON s.sno=sc.sno;");
        }
        if(item.getText().equals("sc801或803")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc\n" +
                    "WHERE cno=801 OR cno=803;");
        }
        if(item.getText().equals("sc非802")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc\n" +
                    "WHERE cno!=802;");
        }
        if(item.getText().equals("sc按成绩排序")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc\n" +
                    "ORDER BY grade DESC;");
        }
        //用户
        if(item.getText().equals("创建razor密码123")) {
            sqlTextArea.setText("CREATE USER razor 123;");
        }
        if(item.getText().equals("删除razor")) {
            sqlTextArea.setText("DROP USER razor;");
        }
        //权限
        if(item.getText().equals("收回PUBLIC的SELECT")) {
            sqlTextArea.setText("REVOKE SELECT ON sc FROM PUBLIC;");
        }
        if(item.getText().equals("授予PUBLIC的SELECT")) {
            sqlTextArea.setText("GRANT SELECT ON sc TO PUBLIC;");
        }

    }
}
