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

    //����˵��йص�һЩ����
    JMenuBar menuBar;//�˵���
    JMenu createTableMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] createTableMenuItems;//���ô��屳����ɫ�Ĳ˵�������
    JMenu deleteTableMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] deleteTableMenuItems;//���ô��屳����ɫ�Ĳ˵�������
    JMenu alterTableMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] alterTableMenuItems;//���ô��屳����ɫ�Ĳ˵�������
    JMenu insertMenu1;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] insertMenuItems1;//���ô��屳����ɫ�Ĳ˵�������
    JMenu insertMenu2;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] insertMenuItems2;//���ô��屳����ɫ�Ĳ˵�������
    JMenu insertMenu3;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] insertMenuItems3;//���ô��屳����ɫ�Ĳ˵�������
    JMenu updateMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] updateMenuItems;//���ô��屳����ɫ�Ĳ˵�������
    JMenu deleteMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] deleteMenuItems;//���ô��屳����ɫ�Ĳ˵�������
    JMenu selectMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] selectMenuItems;//���ô��屳����ɫ�Ĳ˵�������
    JMenu userMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] userMenuItems;//���ô��屳����ɫ�Ĳ˵�������
    JMenu limitMenu;//���ô��屳����ɫ�Ĳ˵�
    JMenuItem[] limitMenuItems;//���ô��屳����ɫ�Ĳ˵�������

    public MainForm(User user,JFrame frame) {
        this.user = user;

        userLabel.setText("��ǰ�û���" + user.getName());

        //JTextArea�Զ�����
        consoleTextArea.setLineWrap(true);
        consoleTextArea.setWrapStyleWord(true);

        initMenu();
        //�������ò˵���
        frame.setJMenuBar(menuBar);

        //ִ��
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
        Analyse analyse = new Analyse(cmd, this); // ������䴫�������
        String sql = analyse.getSql(); // ��������ʼ��ʱ�Ὣ�����sql����ʽ����������get��������

        // CREATE TABLE���
        if ((sql.charAt(0) == 'C' && sql.charAt(7) == 'T') || (sql.charAt(0) == 'c' && sql.charAt(7) == 't')) {
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

        // CREATE USER���
        if ((sql.charAt(0) == 'C' && sql.charAt(7) == 'U') || (sql.charAt(0) == 'c' && sql.charAt(7) == 'u')) {
            analyse.createUser();
        }

        // DROP USER���
        if ((sql.charAt(0) == 'D' && sql.charAt(5) == 'U') || (sql.charAt(0) == 'd' && sql.charAt(5) == 'u')) {
            analyse.dropUser();
        }

        // GRANT���
        if (sql.charAt(0) == 'G' || sql.charAt(0) == 'g') {
            analyse.grant();
        }

        // REVOKE���
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
        //�˵��й�
        menuBar=new JMenuBar();//�˵���

        //������˵�
        createTableMenu=new JMenu("������");
        String[] itemsText1={"����s��","����course��","����sc��"};
        createTableMenuItems=new JMenuItem[3];  //�˵�������
        for(int i=0;i<3;i++){
            createTableMenuItems[i]=new JMenuItem(itemsText1[i]);
            createTableMenuItems[i].addActionListener(this);
            createTableMenu.add(createTableMenuItems[i]);
        }
        menuBar.add(createTableMenu);//���˵���ӵ��˵�����

        //ɾ����˵�
        deleteTableMenu=new JMenu("ɾ����");
        String[] itemsText2={"ɾ��s��"};
        deleteTableMenuItems=new JMenuItem[1];  //�˵�������
        for(int i=0;i<1;i++){
            deleteTableMenuItems[i]=new JMenuItem(itemsText2[i]);
            deleteTableMenuItems[i].addActionListener(this);
            deleteTableMenu.add(deleteTableMenuItems[i]);
        }
        menuBar.add(deleteTableMenu);//���˵���ӵ��˵�����

        //�޸ı�˵�
        alterTableMenu=new JMenu("�޸ı�");
        String[] itemsText3={"ɾ��s��sex��","���s��sex��"};
        alterTableMenuItems=new JMenuItem[2];  //�˵�������
        for(int i=0;i<2;i++){
            alterTableMenuItems[i]=new JMenuItem(itemsText3[i]);
            alterTableMenuItems[i].addActionListener(this);
            alterTableMenu.add(alterTableMenuItems[i]);
        }
        menuBar.add(alterTableMenu);//���˵���ӵ��˵�����

        //����˵�
        insertMenu1=new JMenu("����s");
        String[] itemsText41={"20301114 Panzhihong","20301117 Wanglin","20301116 Qizikang","20301097 Fanchengwei","20301118 Wangshuxin"};
        insertMenuItems1=new JMenuItem[5];  //�˵�������
        for(int i=0;i<5;i++){
            insertMenuItems1[i]=new JMenuItem(itemsText41[i]);
            insertMenuItems1[i].addActionListener(this);
            insertMenu1.add(insertMenuItems1[i]);
        }
        menuBar.add(insertMenu1);//���˵���ӵ��˵�����

        //����˵�
        insertMenu2=new JMenu("����course");
        String[] itemsText42={"801,Database_System","802,Discrete_mathematics","803,Computer_network"};
        insertMenuItems2=new JMenuItem[3];  //�˵�������
        for(int i=0;i<3;i++){
            insertMenuItems2[i]=new JMenuItem(itemsText42[i]);
            insertMenuItems2[i].addActionListener(this);
            insertMenu2.add(insertMenuItems2[i]);
        }
        menuBar.add(insertMenu2);//���˵���ӵ��˵�����

        //����˵�
        insertMenu3=new JMenu("����sc");
        String[] itemsText43={"20301114,801,99","20301114,802,98","20301117,802,97","20301117,803,95","20301116,802,92","20301097,801,88","20301118,801,96","20301118,802,94","20301118,803,90"};
        insertMenuItems3=new JMenuItem[9];  //�˵�������
        for(int i=0;i<9;i++){
            insertMenuItems3[i]=new JMenuItem(itemsText43[i]);
            insertMenuItems3[i].addActionListener(this);
            insertMenu3.add(insertMenuItems3[i]);
        }
        menuBar.add(insertMenu3);//���˵���ӵ��˵�����

        //���²˵�
        updateMenu=new JMenu("����");
        String[] itemsText5={"�޸Ŀγ�801ѧ��Ϊ6","��������802�γ̺�","������sc��801��Ϊ805"};
        updateMenuItems=new JMenuItem[3];  //�˵�������
        for(int i=0;i<3;i++){
            updateMenuItems[i]=new JMenuItem(itemsText5[i]);
            updateMenuItems[i].addActionListener(this);
            updateMenu.add(updateMenuItems[i]);
        }
        menuBar.add(updateMenu);//���˵���ӵ��˵�����

        //ɾ���˵�
        deleteMenu=new JMenu("ɾ��");
        String[] itemsText6={"20301114 801"};
        deleteMenuItems=new JMenuItem[1];  //�˵�������
        for(int i=0;i<1;i++){
            deleteMenuItems[i]=new JMenuItem(itemsText6[i]);
            deleteMenuItems[i].addActionListener(this);
            deleteMenu.add(deleteMenuItems[i]);
        }
        menuBar.add(deleteMenu);//���˵���ӵ��˵�����

        //��ѯ�˵�
        selectMenu=new JMenu("��ѯ");
        String[] itemsText7={"sȫ��","courseȫ��","scȫ��","s��sc JOIN","sc801��803","sc��802","sc���ɼ�����"};
        selectMenuItems=new JMenuItem[7];  //�˵�������
        for(int i=0;i<7;i++){
            selectMenuItems[i]=new JMenuItem(itemsText7[i]);
            selectMenuItems[i].addActionListener(this);
            selectMenu.add(selectMenuItems[i]);
        }
        menuBar.add(selectMenu);//���˵���ӵ��˵�����

        //�û��˵�
        userMenu=new JMenu("�û�");
        String[] itemsText8={"����razor����123","ɾ��razor"};
        userMenuItems=new JMenuItem[2];  //�˵�������
        for(int i=0;i<2;i++){
            userMenuItems[i]=new JMenuItem(itemsText8[i]);
            userMenuItems[i].addActionListener(this);
            userMenu.add(userMenuItems[i]);
        }
        menuBar.add(userMenu);//���˵���ӵ��˵�����

        //Ȩ�޲˵�
        limitMenu=new JMenu("Ȩ��");
        String[] itemsText9={"�ջ�PUBLIC��SELECT","����PUBLIC��SELECT"};
        limitMenuItems=new JMenuItem[2];  //�˵�������
        for(int i=0;i<2;i++){
            limitMenuItems[i]=new JMenuItem(itemsText9[i]);
            limitMenuItems[i].addActionListener(this);
            limitMenu.add(limitMenuItems[i]);
        }
        menuBar.add(limitMenu);//���˵���ӵ��˵�����
    }
    public void actionPerformed(ActionEvent e) {
        AbstractButton item=(AbstractButton)e.getSource();
        //�������¼����¼�Դǿ��ת��Ϊ����İ�ť����
        //����
        if(item.getText().equals("����s��")) {
            sqlTextArea.setText("CREATE TABLE s(\n" +
                    "    sno SMALLINT,\n" +
                    "    sname CHAR(20),\n" +
                    "    age SMALLINT,\n" +
                    "    sex CHAR(1),\n" +
                    "    PRIMARY KEY(sno)\n" +
                    ");");
        }
        if(item.getText().equals("����course��")) {
            sqlTextArea.setText("CREARE TABLE course (\n" +
                    "    cno SMALLINT,\n" +
                    "    cname CHAR(15),\n" +
                    "    credit SMALLINT,\n" +
                    "    PRIMARY KEY(cno)\n" +
                    ");");
        }
        if(item.getText().equals("����sc��")) {
            sqlTextArea.setText("CREARE TABLE sc(\n" +
                    "    sno SMALLINT,\n" +
                    "    cno SMALLINT,\n" +
                    "    grade SMALLINT,\n" +
                    "    PRIMARY KEY (sno,cno),\n" +
                    "    FOREIGN KEY (sno)REFERENCES s(sno),\n" +
                    "    FOREIGN KEY (cno)REFERENCES course(cno)\n" +
                    ");");
        }
        //ɾ��
        if(item.getText().equals("ɾ��s��")) {
            sqlTextArea.setText("DROP TABLE s;");
        }
        //�޸�
        if(item.getText().equals("ɾ��s��sex��")) {
            sqlTextArea.setText("ALTER TABLE s\n" +
                    "DROP sex;");
        }
        if(item.getText().equals("���s��sex��")) {
            sqlTextArea.setText("ALTER TABLE s\n" +
                    "ADD sex CHAR(1);");
        }
        //����
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
        //����
        if(item.getText().equals("�޸Ŀγ�801ѧ��Ϊ6")) {
            sqlTextArea.setText("UPDATE course\n" +
                    "SET credit=6\n" +
                    "WHERE cno=801;");
        }
        if(item.getText().equals("��������802�γ̺�")) {
            sqlTextArea.setText("UPDATE course\n" +
                    "SET cno=802\n" +
                    "WHERE cname=Database_System;");
        }
        if(item.getText().equals("������sc��801��Ϊ805")) {
            sqlTextArea.setText("UPDATE sc\n" +
                    "SET cno=805\n" +
                    "WHERE sno=20301114 AND cno=801;");
        }
        //ɾ��
        if(item.getText().equals("20301114 801")) {
            sqlTextArea.setText("DELETE FROM sc\n" +
                    "WHERE sno=20301114 AND cno=801;");
        }
        //��ѯ
        if(item.getText().equals("sȫ��")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM s;");
        }
        if(item.getText().equals("courseȫ��")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM course;");
        }
        if(item.getText().equals("scȫ��")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc;");
        }
        if(item.getText().equals("s��sc JOIN")) {
            sqlTextArea.setText("SELECT sname,cno,grade\n" +
                    "FROM s JOIN sc ON s.sno=sc.sno;");
        }
        if(item.getText().equals("sc801��803")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc\n" +
                    "WHERE cno=801 OR cno=803;");
        }
        if(item.getText().equals("sc��802")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc\n" +
                    "WHERE cno!=802;");
        }
        if(item.getText().equals("sc���ɼ�����")) {
            sqlTextArea.setText("SELECT *\n" +
                    "FROM sc\n" +
                    "ORDER BY grade DESC;");
        }
        //�û�
        if(item.getText().equals("����razor����123")) {
            sqlTextArea.setText("CREATE USER razor 123;");
        }
        if(item.getText().equals("ɾ��razor")) {
            sqlTextArea.setText("DROP USER razor;");
        }
        //Ȩ��
        if(item.getText().equals("�ջ�PUBLIC��SELECT")) {
            sqlTextArea.setText("REVOKE SELECT ON sc FROM PUBLIC;");
        }
        if(item.getText().equals("����PUBLIC��SELECT")) {
            sqlTextArea.setText("GRANT SELECT ON sc TO PUBLIC;");
        }

    }
}
