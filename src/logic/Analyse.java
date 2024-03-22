package logic;

import db.Database;
import db.Table;
import db.User;
import db.Users;
import view.MainForm;

import static db.FileIO.*;

public class Analyse {
    private String sql = null;
    private MainForm mainForm = null;

    public Analyse(String sql, MainForm mainForm) {

        // �ַ�����ʽ���������sqlǰ��û�пո�û�зֺ�
        // ����󷵻ص�sql�м�Ŀհ׾����滻Ϊһ���ո�
        this.sql = format(sql);
        this.mainForm = mainForm;
    }

    public Analyse(String sql) {

        // �ַ�����ʽ���������sqlǰ��û�пո�û�зֺ�
        // ����󷵻ص�sql�м�Ŀհ׾����滻Ϊһ���ո�
        this.sql = format(sql);
    }

    // �ַ�����ʽ����sql�м�Ŀհ׾����滻Ϊһ���ո�
    public String format(String str) {
        if (str == null)
            return null;
        str = str.replaceAll("[\\s]+", " ");
        str = str.replaceAll("\n", " ");
        return str.trim();
    }

    // get
    public String getSql() {
        return sql;
    }

    // ������
    public int createTable() {

        // �ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("CREATE")) {
            // TODO��CREATE���󣡣���
        }
        if (!sqlWords[1].toUpperCase().equals("TABLE")) {
            // TODO��TABLE���󣡣���
        }

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            database = new Database();
        }

        // ��CREATE TABLE�������ţ���ȡ����
        String tableName = "";
        tableName = sql.substring(13, sql.indexOf("(")).trim(); //һ��Ҫtrim
        //System.out.println("SIZE: " + database.getDatabase().size());
        mainForm.printConsole("SIZE: " + database.getDatabase().size());

        // ѭ��database�������Ƿ��ظ������ظ�ֱ��return�����򴴽��µ�Table����
        for (int i = 0; i < database.getDatabase().size(); i++) {
            if (database.getDatabase().get(i).getName().equals(tableName)) {
                //System.out.println("���Ѵ���");
                mainForm.printConsole("���Ѵ���");
                return 1;
                //TODO:���Ѵ��ڣ��쳣
            }
        }
        Table table = new Table(tableName);
        //System.out.println("����: " + tableName);
        mainForm.printConsole("����: " + tableName);

        // ���ҵ������б�Ľ�β��Ȼ��������ſ�ʼ����β����ȡ����
        // index2ָ�������б�Ľ�β���ҳ�index2
        String attributes = "";
        int index2;
        if (sql.contains("PRIMARY KEY")) { // ����PRIMARY�Ӿ�
            index2 = sql.indexOf("PRIMARY KEY");
        } else if (sql.contains("FOREIGN KEY")) { // û��PRIMARY�Ӿ����FOREIGN KEY�Ӿ�
            index2 = sql.indexOf("FOREIGN KEY");
        } else {
            index2 = sql.length(); // ��û�еĻ���ֱ�ӵ���β
        }
        // ��ȡ�������б�
        attributes = sql.substring(sql.indexOf("(") + 1, index2 - 1); // substring������ͷ��������β��index2��P��F��-1���ǿո�򶺺�
        attributes = attributes.trim(); // ȥ������Ŀո�
        // �����б����ŷֳ�һ���Զԡ�����
        String[] attributesWords = attributes.split(",");
        // ����ÿһ���ԡ�����
        for (int i = 0; i < attributesWords.length; i++) {
            String attribute = format(attributesWords[i]).trim();
            String[] attributeWords = attribute.split("\\s"); // �������������������ؼ��ַֿ�
            if (attributeWords.length != 2) { // ����Ƿ������������������������ؼ���
                // TODO�����Դ��󣡣���
                return 1;
            } else {
                // TODO���������ݣ�����
                table.setColName(attributeWords[0], table.getColNum());
                table.setColType(attributeWords[1], table.getColNum());
                table.setColNum(table.getColNum() + 1);
            }
            //System.out.println("����" + i + ": " + attribute);
            mainForm.printConsole("����" + i + ": " + attribute);
        }

        // ����PRIMARY KEY
        if (sql.contains("PRIMARY KEY")) {

            /*
             * PRIMARY
             * KEY�ؼ��ֺ�����Ӵ���Ϊʲô��ֱ����sql�������¶���һ���Ӵ�����Ϊ���Բ�������CHAR(20)�����ĵ�����������ȷ����ֱ���ò��ö�λPRIMARY
             * KEY����ĵ�һ������
             */
            String sub1 = sql.substring(sql.indexOf("PRIMARY KEY") + 11);

            String pk = sub1.substring(sub1.indexOf("(") + 1, sub1.indexOf(")")).trim();// �ֳ�����

            String[] primeAttribute = pk.split(","); // �����ԣ��������ִʵ���
            for (int i = 0; i < primeAttribute.length; i++) {
                //System.out.println("������" + i + ": " + primeAttribute[i]);
                mainForm.printConsole("������" + i + ": " + primeAttribute[i]);
                int pkIndex = findIndexByColname(primeAttribute[i], table);
                if (pkIndex == -1) {
                    //System.out.println("�Ҳ�����������");
                    mainForm.printConsole("�Ҳ�����������");
                    return 1;
                }
                table.setPk(pkIndex);   //�������
            }
        }

        // ����FOREIGN KEY
        if (sql.contains("FOREIGN KEY")) {
            String sub2 = sql.substring(sql.indexOf("FOREIGN KEY") + 11);// �Ӵ���FOREIGN KEY������ſ�ʼ
            String[] fks = sub2.split(",");// ÿ��һ�����ž���һ�����

            // ÿ��ѭ������һ�����
            for (int i = 0; i < fks.length; i++) {
                // �滻���ո�͹ؼ��֣�ֻ������(���)����(�����б����յ�����)��
                fks[i] = fks[i].replaceAll("FOREIGN KEY", "").trim();
                fks[i] = fks[i].replaceAll("REFERENCES", "").trim();
                fks[i] = fks[i].replaceAll("[\\s]+", "").trim();

                String fk = fks[i].substring(fks[i].indexOf("(") + 1, fks[i].indexOf(")"));

                String mainTableName = fks[i].substring(fks[i].indexOf(")") + 1, fks[i].indexOf("(", fks[i].indexOf("(") + 1));

                String mainTablePk = fks[i].substring(fks[i].indexOf("(", fks[i].indexOf("(") + 1) + 1,
                        fks[i].indexOf(")", fks[i].indexOf(")", fks[i].indexOf(")") + 1)));

                //���������Ƿ��ڱ�����
                int indexFk = findIndexByColname(fk, table);
                if (indexFk == -1) {
                    //System.out.println("���������");
                    mainForm.printConsole("���������");
                    return 1;
                }
                //���������Ƿ��ڱ����յ������У�database�в��ұ����յĲ��õ�����
                Table mainTable = findTable(mainTableName, database);
                if (mainTable == null) {
                    //System.out.println("�����յı�����");
                    mainForm.printConsole("�����յı�����");
                    return 1;
                }
                if (findIndexByColname(mainTablePk, mainTable) == -1) {
                    //System.out.println("�����յ�����������");
                    mainForm.printConsole("�����յ�����������");
                    return 1;
                }
                //�������
                table.setFk(indexFk, mainTableName, mainTablePk);

                //System.out.print("���" + "[" + i + "]: " + fk);
                //.out.print("�����˱�" + mainTableName);
                //System.out.println("�е�����" + mainTablePk);
                mainForm.printConsole("���" + "[" + i + "]: " + fk + "�����˱�" + mainTableName + "�е�����" + mainTablePk);

            }
            // TODO
        }

        //���������ӵ�database�в�д���ļ�
        database.getDatabase().add(table);
        writeDatabase(database);
        return 0;
    }

    public int alterTable() {

        // �ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("ALTER")) {
            // TODO��ALTER���󣡣���
        }
        if (!sqlWords[1].toUpperCase().equals("TABLE")) {
            // TODO��TABLE���󣡣���
        }

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            //System.out.println("���Ƚ���");
            mainForm.printConsole("���Ƚ���");
            return 1;
        }


        // ��ȡ����
        String tableName = sqlWords[2];
        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        if (table == null) {
            //System.out.println("δ�ҵ��ñ�");
            mainForm.printConsole("δ�ҵ��ñ�");
            return 1;
        }

        // ADD/DROP
        if (sqlWords[3].toUpperCase().equals("ADD")) {  //���һ��
            String colName = sqlWords[4];
            String colType = sqlWords[5];
            int index=-1;//�ҵ���һ��������Ϊ��ӵ��еĵط�
            for(int i=0;i<10;i++){
                if(table.getColName(i).equals("#NULL") && table.getColType(i).equals("#NULL")){
                    index = i;
                    break;
                }
            }
            if(index == -1){
                mainForm.printConsole("�ڴ治�㣬�޷��������");
                return 1;
            }
            //�Ƿ�����
            if(findIndexByColname(colName,table)!=-1){
                mainForm.printConsole("�����Ѵ��ڣ�");
                return 1;
            }
            table.setColName(colName, index);
            table.setColType(colType, index);
            table.setColNum(table.getColNum() + 1);
            //System.out.println("�ɹ����" + colName + "����" + colType + "����" + tableName);
            mainForm.printConsole("�ɹ����" + colName + "����" + colType + "����" + tableName);
        } else if (sqlWords[3].toUpperCase().equals("DROP")) {  //ɾ��һ��
            String colName = sqlWords[4];
            // ��ȡ��������Ӧ���±�
            int index = findIndexByColname(colName, table);
            if (index == -1) {  //����������
                //System.out.println("δ�ҵ�����");
                mainForm.printConsole("δ�ҵ�����");
                return 1;
            } else if (table.isPk(index)) {    //����Ϊ������
                //System.out.println("��"+colName+"Ϊ�������ܾ�ɾ��");
                mainForm.printConsole("��" + colName + "Ϊ�������ܾ�ɾ��");
                return 1;
            } else {
                //ɾ����һ�е����ж���
                table.setColName("#NULL", index);
                table.setColType("#NULL", index);
                for (int i = 0; i < 20; i++) {
                    table.setCell(i, index, "#NULL");
                }
                table.setColNum(table.getColNum() -1);
            }

            //System.out.println("�ɹ�ɾ��" + colName + "�ӱ�" + tableName);
            mainForm.printConsole("�ɹ�ɾ��" + colName + "�ӱ�" + tableName);
        } else {
            // TODO��ADD/DROP���󣡣���
        }

        //�޸ĺ��databaseͬ�����ļ�
        writeDatabase(database);
        return 0;
    }

    public int dropTable() {

        //�ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("DROP")) {
            // TODO��DROP���󣡣���
        }
        if (!sqlWords[1].toUpperCase().equals("TABLE")) {
            // TODO��TABLE���󣡣���
        }

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            //System.out.println("���Ƚ���");
            mainForm.printConsole("���Ƚ���");
            return 1;
        }

        // ��ȡ����
        String tableName = sqlWords[2];

        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        if (table == null) {
            //System.out.println("δ�ҵ��ñ�");
            mainForm.printConsole("δ�ҵ��ñ�");
            return 1;
        }
        database.getDatabase().remove(table);

        //System.out.println("�ɹ�ɾ����" + tableName);
        mainForm.printConsole("�ɹ�ɾ����" + tableName);

        //�޸ĺ��databaseͬ�����ļ�
        writeDatabase(database);
        return 0;
    }

    public int insertInto() {

        //�ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("INSERT")) {
            // TODO��INSERT���󣡣���
        }
        if (!sqlWords[1].toUpperCase().equals("INTO")) {
            // TODO��INTO���󣡣���
        }

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            //System.out.println("���Ƚ���");
            mainForm.printConsole("���Ƚ���");
            return 1;
        }

        // ��INSERT INTO�������ţ���ȡ����
        String tableName = "";
        tableName = sql.substring(12, sql.indexOf("(")).trim();
        //System.out.println("TableName: " + tableName);
        mainForm.printConsole("TableName: " + tableName);

        //������û��Ȩ��
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//��Ȩ�޼�¼
            if (mainForm.getUser().insertLimit(tableIndexInUser)) {//Ȩ�޼�¼Ϊ����
                mainForm.printConsole("���û�û�жԱ�" + tableName + "�Ĳ���Ȩ��");
                return 1;
            }
        }

        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        if (table == null) {
            //System.out.println("δ�ҵ��ñ�");
            mainForm.printConsole("δ�ҵ��ñ�");
            return 1;
        }

        // ��ȡ��������
        String sub1 = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")")).trim();
        String colNames[] = sub1.split(",");
        //System.out.println("sub1: " + sub1);
        mainForm.printConsole("sub1: " + sub1);

        // ��ȡֵ���ϣ����ҵ�VALUES��ĵ�һ��������(��ȷ��VALUES����ʱ�����׳��쳣)
        String sub2 = sql
                .substring(sql.indexOf("(", sql.indexOf("VALUES")) + 1, sql.indexOf(")", sql.indexOf("VALUES"))).trim();
        String values[] = sub2.split(",");
        //System.out.println("sub2: " + sub2);
        mainForm.printConsole("sub2: " + sub2);


        String[][] fk = table.getFk();
        // ���룬ÿ��ѭ������һ������
        for (int i = 0; i < colNames.length; i++) {
            int index = findIndexByColname(colNames[i], table); //�ҵ�Ҫ������е��±꣨���������е��±꣩
            //System.out.println(index+":"+colNames[i]);
            mainForm.printConsole(index + ":" + colNames[i]);
            if (index == -1) {
                //System.out.println("����������");
                mainForm.printConsole("����������");
                return 1;
            } else {
                if (fk[index][0].equals("#NULL") && fk[index][1].equals("#NULL")) {   //�����������Ͳ���
                    table.setCell(table.getTupleNum(), index, values[i]);
                } else {  //�������������һ�����������Ƿ������ֵ
                    //���һ�����������Ƿ������ֵ
                    Table mainTable = findTable(fk[index][0], database);
                    if (mainTable == null) {
                        //System.out.println("������󣺱����յ���������");
                        mainForm.printConsole("������󣺱����յ���������");
                        return 1;
                    }
                    if (findValue(values[i], mainTable, findIndexByColname(fk[index][1], mainTable)) == -1) {
                        //System.out.println("����û��"+values[i]+"������ֵ�����");
                        mainForm.printConsole("����û��" + values[i] + "������ֵ�����");
                        return 1;
                    }
                    //��ֵ��
                    table.setCell(table.getTupleNum(), index, values[i]);
                    mainForm.printConsole("����ɹ�");
                }
            }
        }


        //�����ȼ�װ���룬�����ټ�飬��Υ��ʵ�������ԣ���ɾ���ող����Ԫ��
        boolean entityIntegrity = true;
        boolean[] pk = table.getPk();
        for (int i = 0; i < table.getColNum(); i++) {
            //��������Ƿ�Ϊ��
            if (pk[i] && table.getCell(table.getTupleNum(), i).equals("#NULL")) { //���Ǹող��������Ԫ�飬���������ǲ���Ϊ��
                entityIntegrity = false;
                mainForm.printConsole("�����Ԫ������" + table.getColName(i) + "Ϊ�գ�Υ��ʵ��������");
                //���������Ԫ�飬���ÿ�
                for (int j = 0; j < table.getColNum(); j++) {
                    table.setCell(table.getTupleNum(), i, "#NULL");
                }
                return 1;
            }
            //��������Ƿ��ظ�
            if (pk[i] && findValue(table.getCell(table.getTupleNum(), i), table, i) != -1 && table.getPkNum() == 1) { //�������������������ظ�ֵ�ҵ�ֵ����
                entityIntegrity = false;
                mainForm.printConsole("�����Ԫ������" + table.getColName(i) + "��ֵ" + table.getCell(table.getTupleNum(), i) + "�ظ���Υ��ʵ��������");
                //���������Ԫ�飬���ÿ�
                for (int j = 0; j < table.getColNum(); j++) {
                    table.setCell(table.getTupleNum(), i, "#NULL");
                }
                return 1;
            }
        }


        //Ԫ������+1
        table.setTupleNum(table.getTupleNum() + 1);
        mainForm.printConsole("�ɹ�����" + sub2 + "����" + tableName + "  ����" + sub1);
        mainForm.printConsole("tuplenum" + table.getTupleNum() + "ColNum" + table.getColNum());

        // ����һ�¶��ڶ�����Ե��������Ƿ����ظ�ֵ(ֻ�ܲ�����)
        //mainForm.printConsole("PkNum:"+table.getPkNum());
        if (table.getPkNum() > 1) {
            int count = 0;
            int[] pks = new int[10];
            //�������±��¼һ��
            for (int i = 0; i < table.getColNum(); i++) {
                if (table.isPk(i)) {
                    pks[count++] = i;
                }
            }
            //mainForm.printConsole(table.getPkNum()+"##"+count);
            for (int i = 0; i < table.getTupleNum(); i++) {
                int t1 = i;
                int t2 = findValue(table.getCell(i, pks[0]), table, pks[0]);
                //mainForm.printConsole("t1:"+t1+"t2:"+t2);
                if (t2 != -1) {
                    if (t1 != t2 && table.getCell(t1, pks[1]).equals(table.getCell(t2, pks[1]))) {
                        mainForm.printConsole("����������������ȫ��ͬ��Υ����ʵ�������ԣ�����ʧ��");
                        return 1;
                    }
                }
            }
        }

        //����database
        writeDatabase(database);
        return 0;
    }

    public int update() {

        //�ؼ��ʼ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("UPDATE")) {
            // TODO��UPDATE���󣡣���
        }

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("���Ƚ���");
            return 1;
        }

        // ��ȡ����
        String tableName = sqlWords[1];

        //������û��Ȩ��
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//��Ȩ�޼�¼
            if (mainForm.getUser().updateLimit(tableIndexInUser)) {//Ȩ�޼�¼Ϊ����
                mainForm.printConsole("���û�û�жԱ�" + tableName + "�ĸ���Ȩ��");
                return 1;
            }
        }

        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("δ�ҵ��ñ�");
            return 1;
        }

        // ��ȡSET����Ӵ�
        int index1 = sql.indexOf("WHERE");
        String sub1 = "";
        String sub2 = "";

        if (index1 != -1) { // ��WHERE�Ӿ�
            sub1 = sql.substring(sql.indexOf("SET") + 3, index1).trim(); // SET��WHERE�Ӿ�ǰ
            sub2 = sql.substring(index1 + 5).trim(); // WHERE��

            // WHERE�Ӿ��ж�
            boolean[] flag = judge(sub2, table);

            // ����ֵ
            String[] assignment = sub1.split(",");
            //ÿ��ѭ����һ�н��в���
            for (String s : assignment) {
                String colName = s.split("=")[0];
                String value = s.split("=")[1];
                // �ҵ�������Ӧ���±�
                int colIndex = findIndexByColname(colName, table);
                if (colIndex == -1) {
                    mainForm.printConsole("��������");
                    return 0;
                }
                // ÿ��ѭ����һ��Ԫ����в���
                for (int j = 0; j < table.getTupleNum(); j++) {
                    if (flag[j]) {    //������Ԫ����Ҫ���µĻ�
                        //���������ǲ������
                        String[][] fk = table.getFk();
                        if (fk[colIndex][0].equals("#NULL") && fk[colIndex][1].equals("#NULL")) {   //����������
                            //���ʵ��������
                            if (table.isPk(colIndex) && value.equals("#NULL")) {  //�����Ƿ񸳿�ֵ
                                mainForm.printConsole("���µ�Ԫ������" + table.getColName(colIndex) + "Ϊ�գ�Υ��ʵ��������");
                                return 1;
                            }
                            if (table.isPk(colIndex) && findValue(value, table, colIndex) != -1 && table.getPkNum() == 1) { //�������������������ظ�ֵ�ҵ�ֵ����
                                mainForm.printConsole("���µ�Ԫ������" + table.getColName(colIndex) + "��ֵ" + value + "�ظ���Υ��ʵ��������");
                                return 1;
                            }
                            table.setCell(j, colIndex, value);
                        } else {  //�������������һ�����������Ƿ������ֵ
                            //���һ�����������Ƿ������ֵ
                            Table mainTable = findTable(fk[colIndex][0], database);
                            if (mainTable == null) {
                                mainForm.printConsole("������󣺱����յ���������");
                                return 1;
                            }
                            if (findValue(value, mainTable, findIndexByColname(fk[colIndex][1], mainTable)) == -1) {
                                mainForm.printConsole("����û��" + value + "������ֵ�����");
                                return 1;
                            }
                            //���ʵ��������
                            if (table.isPk(colIndex) && value.equals("#NULL")) {  //�����Ƿ񸳿�ֵ
                                mainForm.printConsole("���µ�Ԫ������" + table.getColName(colIndex) + "Ϊ�գ�Υ��ʵ��������");
                                return 1;
                            }
                            if (table.isPk(colIndex) && findValue(value, table, colIndex) != -1 && table.getPkNum() == 1) { //�������������������ظ�ֵ�ҵ�ֵ����
                                mainForm.printConsole("���µ�Ԫ������" + table.getColName(colIndex) + "��ֵ" + value + "�ظ���Υ��ʵ��������");
                                return 1;
                            }
                            //��ֵ��
                            table.setCell(j, colIndex, value);
                        }
                    }
                }
                mainForm.printConsole("Ԫ���Ѹ���" + colName + " = " + value);
            }
        } else { // û��WHERE�Ӿ�
            sub1 = sql.substring(sql.indexOf("SET") + 3).trim(); // SET��ֱ�ӵ�ĩβ
            // ÿһ��ѭ����һ�н��в���
            String[] assignment = sub1.split(",");
            for (String s : assignment) {
                String colName = s.split("=")[0];
                String value = s.split("=")[1];
                // �ҵ�������Ӧ���±�
                int colIndex = findIndexByColname(colName, table);
                if (colIndex == -1) {
                    mainForm.printConsole("��������");
                    return 0;
                }
                // ÿ��ѭ����һ��Ԫ����в���
                for (int j = 0; j < table.getTupleNum(); j++) {
                    //���������ǲ������
                    String[][] fk = table.getFk();
                    if (fk[colIndex][0].equals("#NULL") && fk[colIndex][1].equals("#NULL")) {   //����������
                        //���ʵ��������
                        if (table.isPk(colIndex)) {  //����WHERE�Ӿ��UPDATE���ܶ���������
                            mainForm.printConsole("�����鲻��WHERE�Ӿ�Ĳ���ȫ��UPDATE���������������ʸò����Ѿܾ�");
                            return 1;
                        }
                        table.setCell(j, colIndex, value);
                    } else {  //�������������һ�����������Ƿ������ֵ
                        //���һ�����������Ƿ������ֵ
                        Table mainTable = findTable(fk[colIndex][0], database);
                        if (mainTable == null) {
                            mainForm.printConsole("������󣺱����յ���������");
                            return 1;
                        }
                        if (findValue(value, mainTable, findIndexByColname(fk[colIndex][1], mainTable)) == -1) {
                            mainForm.printConsole("����û��" + value + "������ֵ�����");
                            return 1;
                        }
                        //���ʵ��������
                        if (table.isPk(colIndex)) {  //����WHERE�Ӿ��UPDATE���ܶ���������
                            mainForm.printConsole("�����鲻��WHERE�Ӿ�Ĳ���ȫ��UPDATE���������������ʸò����Ѿܾ�");
                            return 1;
                        }
                        //��ֵ��
                        table.setCell(j, colIndex, value);
                    }
                }
                mainForm.printConsole("����Ԫ���Ѹ���" + colName + " = " + value);
            }
        }

        // ����һ�¶��ڶ�����Ե��������Ƿ����ظ�ֵ(ֻ�ܲ�����)
        //System.out.println("PkNum:"+table.getPkNum());
        if (table.getPkNum() > 1) {
            int count = 0;
            int[] pks = new int[10];
            //�������±��¼һ��
            for (int i = 0; i < table.getColNum(); i++) {
                if (table.isPk(i)) {
                    pks[count++] = i;
                }
            }
            //System.out.println(table.getPkNum()+"##"+count);
            for (int i = 0; i < table.getTupleNum(); i++) {
                int t1 = i;
                int t2 = findValue(table.getCell(i, pks[0]), table, pks[0]);
                //System.out.println("t1:"+t1+"t2:"+t2);
                if (t2 != -1) {
                    if (t1 != t2 && table.getCell(t1, pks[1]).equals(table.getCell(t2, pks[1]))) {
                        mainForm.printConsole("����������������ȫ��ͬ��Υ����ʵ�������ԣ�����ʧ��");
                        return 1;
                    }
                }
            }
        }

        // �������ݿ�
        writeDatabase(database);
        return 0;
    }

    public int delete() {

        // �ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("DELETE")) {
            // TODO��INSERT���󣡣���
        }
        if (!sqlWords[1].toUpperCase().equals("FROM")) {
            // TODO��INTO���󣡣���
        }

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("���Ƚ���");
            return 1;
        }

        // ��ȡ����
        String tableName = sqlWords[2];

        //������û��Ȩ��
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//��Ȩ�޼�¼
            if (mainForm.getUser().deleteLimit(tableIndexInUser)) {//Ȩ�޼�¼Ϊ����
                mainForm.printConsole("���û�û�жԱ�" + tableName + "��ɾ��Ȩ��");
                return 1;
            }
        }

        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("δ�ҵ��ñ�");
            return 1;
        }

        // ��ȡWHERE�Ӿ�
        String sub1 = "";
        int index1 = sql.indexOf("WHERE");
        if (index1 != -1) { // ��WHERE�Ӿ�
            sub1 = sql.substring(index1 + 5).trim(); // WHERE��
            boolean[] flag;
            // ��һ�����⣬ɾ��һ��Ԫ��Ҫ�Ѻ���ķŵ�ǰ�������������λ
            // ΪʲôҪ����ѭ�����������������ֽ���
            int tupleNum = table.getTupleNum();
            for (int i = 0; i < tupleNum; i++) { //����Ĳ���get�������Է�ѭ������������ѭ���仯���仯
                for (int s = 0; s < table.getTupleNum(); s++) { //�����table.getTupleNum������ѭ���仯���仯
                    mainForm.printConsole("i=" + i + "s" + s);
                    flag = judge(sub1, table);   //ÿ��ѭ����Ԫ��˳�򶼻�䣬����Ҫ���¸���flag����
                    if (flag[s]) {
                        //�����ȰѸ�Ԫ��ɾ��
                        for (int j = 0; j < 10; j++) {
                            table.setCell(s, j, "#NULL");
                        }
                        //Ȼ�󽫺����Ԫ�鸴�Ƶ���ɾ����Ԫ����
                        for (int j = s; j < table.getTupleNum() - 1; j++) {
                            for (int k = 0; k < 10; k++) {
                                table.setCell(j, k, table.getCell(j + 1, k));
                            }
                        }
                        //�����һ��Ԫ��ɾ��
                        for (int t = 0; t < 10; t++) {
                            table.setCell(table.getTupleNum(), t, "#NULL");
                        }
                        //Ԫ����-1
                        table.setTupleNum(table.getTupleNum() - 1);
                    }
                }
            }


        } else { // û��WHERE�Ӿ䣬ɾ������Ԫ��
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 10; j++) {
                    table.setCell(i, j, "#NULL");
                }
            }
            table.setTupleNum(0);
        }

        mainForm.printConsole("DELETE " + tableName);
        mainForm.printConsole("WHERE:" + sub1);

        writeDatabase(database);
        return 0;
    }

    // ��ʱ������JOIN
    public int select() {

        // �ؼ��ʼ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("SELECT")) {
            // TODO��SELECT���󣡣���
        }
        if (!sqlWords[2].toUpperCase().equals("FROM")) {
            // TODO��FROM���󣡣���
        }
        String colName = sqlWords[1];
        String tableName = sqlWords[3];

        //��������
        mainForm.flushResult();

        //������û��JOIN ON��ORDER BY
        if (sql.contains("JOIN") && sql.contains("ON")) {
            join();
            return 0;
        }
        if(sql.contains("ORDER BY")){
            order();
            return 0;
        }

        // ��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("���Ƚ���");
            return 1;
        }

        //������û��Ȩ��
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//��Ȩ�޼�¼
            if (mainForm.getUser().selectLimit(tableIndexInUser)) {//Ȩ�޼�¼Ϊ����
                mainForm.printConsole("���û�û�жԱ�" + tableName + "�Ĳ�ѯȨ��");
                return 1;
            }
        }

        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("δ�ҵ��ñ�");
            return 1;
        }

        // ��ȡWHERE�Ӿ�
        String sub1 = "";
        int index1 = sql.indexOf("WHERE");
        boolean[] flag = new boolean[20];
        if (index1 != -1) { // ��WHERE�Ӿ�
            sub1 = sql.substring(index1 + 5).trim(); // WHERE��
            flag = judge(sub1, table);

        } else { // û��WHERE�Ӿ�
            for (int i = 0; i < 20; i++) {
                flag[i] = true;
            }
        }

        //��������
        int[] colIndex;     //��Ҫ��ѯ���������д��������ǵ��±����һ��������
        String[] str = colName.split(",");//�����ִʺ���ַ�������
        int colNum;
        //������ѯ�����ǲ���*
        if (colName.trim().equals("*")) { //��ѯ��������
            colIndex = new int[table.getColNum()];
            colNum = table.getColNum();
            for (int i = 0; i < table.getColNum(); i++) {
                colIndex[i] = i;
            }
        } else {  //��ѯָ������
            colNum = str.length;
            colIndex = new int[colNum];
            for (int i = 0; i < colNum; i++) {
                colIndex[i] = findIndexByColname(str[i], table);
                if (colIndex[i] == -1) {
                    mainForm.printConsole("��������");
                    return 1;
                }
            }
        }

        // ��ӡ��ͷ
        int count = 0;
        for (int i = 0; i < table.getTupleNum(); i++) {
            if (flag[i]) {
                count++;
            }
        }
        mainForm.printResult("��" + table.getName() + "����" + table.getTupleNum() + "�У�����" + count + "����������" + "\n");
        for (int i = 0; i < colNum; i++) {
            mainForm.printResult(table.getColName(colIndex[i]) + "\t");
        }

        mainForm.printResult("\n");

        // ��ӡ����
        for (int i = 0; i < table.getTupleNum(); i++) {
            for (int j = 0; j < colNum; j++) {
                if (flag[i]) {
                    mainForm.printResult(table.getCell(i, colIndex[j]) + "\t");
                }
            }
            if (flag[i]) {
                mainForm.printResult("\n");
            }
        }


//        System.out.println("SELECT " + colName);
//        System.out.println("FROM " + tableName);
//        System.out.println("WHERE " + sub1);

        return 0;
    }

    public int join() {
        String sqlWords[] = sql.split("[\\s]");
        String colName = sqlWords[1];
        String tableName = sqlWords[3];
        String joinTablename = sqlWords[5];
        String joinColname;
        String[] str = sqlWords[7].split("=");
        joinColname = str[0].substring(str[0].indexOf(".") + 1).trim();

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("���Ƚ���");
            return 1;
        }
        //������û��Ȩ��
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//��Ȩ�޼�¼
            if (mainForm.getUser().selectLimit(tableIndexInUser)) {//Ȩ�޼�¼Ϊ����
                mainForm.printConsole("���û�û�жԱ�" + tableName + "�Ĳ�ѯȨ��");
                return 1;
            }
        }
        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        Table joinTable = findTable(joinTablename, database);
        if (table == null || joinTable == null) {
            mainForm.printConsole("δ�ҵ���");
            return 1;
        }

        int index1 = findIndexByColname(joinColname, table);
        int index2 = findIndexByColname(joinColname, joinTable);
        if (index1 == -1 || index2 == -1) {
            mainForm.printConsole("ON������������");
            return 1;
        }

        //�ϳ��±�ͷ
        Table newTable = new Table("newTable");
        int newTableColNum = table.getColNum() + joinTable.getColNum() - 1;
        int i;
        for (i = 0; i < table.getColNum(); i++) {
            newTable.setColName(table.getColName(i), i);
            newTable.setColNum(newTable.getColNum()+1);
        }
        int k = i;
        for (int j = i; j < i + joinTable.getColNum(); j++) {
            if (!joinTable.getColName(j - i).equals(joinColname)) {
                newTable.setColName(joinTable.getColName(j - i), k);
                newTable.setColNum(newTable.getColNum()+1);
                k++;
            }
        }
        if(newTableColNum!=newTable.getColNum()){   //������
            System.out.println("���ַ�ʽ�õ�����������һ����");
        }
        //������������
        for (int a = 0; a < table.getTupleNum(); a++) {
            for (int b = 0; b < joinTable.getTupleNum(); b++) {
                //������ӵ�����ֵ��ȣ�������
                if (table.getCell(a, index1).equals(joinTable.getCell(b, index2))) {
                    int o;
                    for (o = 0; o < table.getColNum(); o++) {
                        newTable.setCell(newTable.getTupleNum(), o, table.getCell(a, o));
                    }
                    int q = o;
                    for (int p = o; p < o + joinTable.getColNum(); p++) {
                        if (!joinTable.getColName(p - o).equals(joinColname)) {
                            newTable.setCell(newTable.getTupleNum(), q, joinTable.getCell(b, p - o));
                            q++;
                        }
                    }
                    newTable.setTupleNum(newTable.getTupleNum() + 1);
                }
            }
        }
        // ��ȡWHERE�Ӿ�
        String sub1 = "";
        int indexW = sql.indexOf("WHERE");
        boolean[] flag = new boolean[20];
        if (indexW != -1) { // ��WHERE�Ӿ�
            sub1 = sql.substring(indexW + 5).trim(); // WHERE��
            flag = judge(sub1, newTable);

        } else { // û��WHERE�Ӿ�
            for (int t = 0; t < 20; t++) {
                flag[t] = true;
            }
        }
        //��������
        int[] colIndex;     //��Ҫ��ѯ���������д��������ǵ��±����һ��������
        String[] cols = colName.split(",");//�����ִʺ���ַ�������
        int colNum;
        //������ѯ�����ǲ���*
        if (colName.trim().equals("*")) { //��ѯ��������
            colIndex = new int[newTable.getColNum()];
            colNum = newTable.getColNum();
            for (int t = 0; t < newTable.getColNum(); t++) {
                colIndex[t] = t;
            }
        } else {  //��ѯָ������
            colNum = cols.length;
            colIndex = new int[colNum];
            for (int t = 0; t < colNum; t++) {
                colIndex[t] = findIndexByColname(cols[t], newTable);
                if (colIndex[t] == -1) {
                    mainForm.printConsole("��������");
                    return 1;
                }
            }
        }

        // ��ӡ��ͷ
        int count = 0;
        for (int t = 0; t < newTable.getTupleNum(); t++) {
            if (flag[t]) {
                count++;
            }
        }
        mainForm.printResult("��" + newTable.getName() + "����" + newTable.getTupleNum() + "�У�����" + count + "����������" + "\n");
        System.out.println(colNum+""+newTable.getColNum());
        for (int t = 0;t < colNum; t++) {
            mainForm.printResult(newTable.getColName(colIndex[t]) + "\t");
        }

        mainForm.printResult("\n");

        // ��ӡ����
        for (int t = 0; t < newTable.getTupleNum(); t++) {
            for (int s = 0; s < colNum; s++) {
                if (flag[t]) {
                    mainForm.printResult(newTable.getCell(t, colIndex[s]) + "\t");
                }
            }
            if (flag[t]) {
                mainForm.printResult("\n");
            }
        }
        return 0;
    }

    public int order() {
        String sqlWords[] = sql.split("[\\s]");
        String colName = sqlWords[1];
        String tableName = sqlWords[3];
        String orderColname = sql.substring(sql.indexOf("ORDER BY") + 9).split(" ")[0].trim();

        //��ȡ���ݿ�
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("���Ƚ���");
            return 1;
        }
        //������û��Ȩ��
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//��Ȩ�޼�¼
            if (mainForm.getUser().selectLimit(tableIndexInUser)) {//Ȩ�޼�¼Ϊ����
                mainForm.printConsole("���û�û�жԱ�" + tableName + "�Ĳ�ѯȨ��");
                return 1;
            }
        }
        // database�в��Ҹñ��õ�����
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("δ�ҵ���");
            return 1;
        }

        System.out.println(orderColname);

        int indexOrderCol = findIndexByColname(orderColname, table);
        if (indexOrderCol == -1) {
            mainForm.printConsole("ORDER BY������������");
            return 1;
        }


        // ��ȡWHERE�Ӿ�
        String sub1 = "";
        int indexW = sql.indexOf("WHERE");
        boolean[] flag = new boolean[20];
        if (indexW != -1) { // ��WHERE�Ӿ�
            sub1 = sql.substring(indexW + 5,sql.indexOf("ORDER BY")).trim(); // WHERE��
            flag = judge(sub1, table);

        } else { // û��WHERE�Ӿ�
            for (int i = 0; i < 20; i++) {
                flag[i] = true;
            }
        }

        //��������
        int[] colIndex;     //��Ҫ��ѯ���������д��������ǵ��±����һ��������
        String[] cols = colName.split(",");//�����ִʺ���ַ�������
        int colNum;
        //������ѯ�����ǲ���*
        if (colName.trim().equals("*")) { //��ѯ��������
            colIndex = new int[table.getColNum()];
            colNum = table.getColNum();
            for (int i = 0; i < table.getColNum(); i++) {
                colIndex[i] = i;
            }
        } else {  //��ѯָ������
            colNum = cols.length;
            colIndex = new int[colNum];
            for (int i = 0; i < colNum; i++) {
                colIndex[i] = findIndexByColname(cols[i], table);
                if (colIndex[i] == -1) {
                    mainForm.printConsole("��������");
                    return 1;
                }
            }
        }

        // ��ӡ��ͷ
        int[][] tupleIndex = new int[20][2];
        for(int i=0;i<20;i++){
            for(int j=0;j<2;j++){
                tupleIndex[i][j]=-1;
            }
        }

        //��������WHERE��Ԫ���У�Ҫ�������ֵ
        int count = 0;
        for (int i = 0; i < table.getTupleNum(); i++) {
            if (flag[i]) {
                tupleIndex[count][0] = i;
                tupleIndex[count][1] = Integer.parseInt(table.getCell(i,indexOrderCol));
                count++;
            }
        }

        //ð������
        if(sql.contains("DESC")){
            for (int i = 0; i < count - 1; i++) {
                for (int j = 0; j < count - 1 - i; j++) {
                    if (tupleIndex[j][1] < tupleIndex[j+1][1]) {
                        int temp1 = tupleIndex[j][0];
                        tupleIndex[j][0] = tupleIndex[j + 1][0];
                        tupleIndex[j + 1][0] = temp1;
                        int temp2 = tupleIndex[j][1];
                        tupleIndex[j][1] = tupleIndex[j + 1][1];
                        tupleIndex[j + 1][1] = temp2;
                    }
                }
            }
        }else{//Ĭ������
            for (int i = 0; i < count - 1; i++) {
                for (int j = 0; j < count - 1 - i; j++) {
                    if (tupleIndex[j][1] > tupleIndex[j+1][1]) {
                        int temp1 = tupleIndex[j][0];
                        tupleIndex[j][0] = tupleIndex[j + 1][0];
                        tupleIndex[j + 1][0] = temp1;
                        int temp2 = tupleIndex[j][1];
                        tupleIndex[j][1] = tupleIndex[j + 1][1];
                        tupleIndex[j + 1][1] = temp2;
                    }
                }
            }
        }

        mainForm.printResult("��" + table.getName() + "����" + table.getTupleNum() + "�У�����" + count + "����������" + "\n");
        System.out.println(colNum+""+table.getColNum());
        for (int i = 0;i < colNum; i++) {
            mainForm.printResult(table.getColName(colIndex[i]) + "\t");
        }

        mainForm.printResult("\n");

        // ��ӡ����
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < colNum; j++) {
                    mainForm.printResult(table.getCell(tupleIndex[i][0], colIndex[j]) + "\t");
            }
            mainForm.printResult("\n");
        }
        return 0;
    }

    //�����û�
    public int createUser() {
        // �ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("CREATE")) {
            // TODO��CREATE���󣡣���
        }
        if (!sqlWords[1].toUpperCase().equals("USER")) {
            // TODO��USER���󣡣���
        }

        //��ȡUsers����
        Users users = readUsers();
//        if(users == null){
//            writeUsers(new Users());
//            users = readUsers();
//        }

        //������ȡ�û���������
        String username = sqlWords[2];
        String password = sqlWords[3];

        //����û����Ƿ��ظ�
        if (users.isExist(username) != -1) {
            mainForm.printConsole("���û��Ѵ��ڣ�");
            return 1;
        }
        ;

        //���ظ��Ļ�������û�
        users.getUsers().add(new User(username, password));
        mainForm.printConsole("�ɹ�����û� " + username + " " + password);
        writeUsers(users);
        return 0;
    }

    //ɾ���û�
    public int dropUser() {
        // �ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("DROP")) {
            // TODO��DROP���󣡣���
        }
        if (!sqlWords[1].toUpperCase().equals("USER")) {
            // TODO��USER���󣡣���
        }

        //��ȡUsers����
        Users users = readUsers();

        //������ȡ�û���������
        String username = sqlWords[2];

        //�����Լ�ɾ���Լ�
        if (username.equals(mainForm.getUser().getName())) {
            mainForm.printConsole("�����Լ�ɾ���Լ�����");
            return 1;
        }

        //����û����Ƿ����
        int index = users.isExist(username);
        if (index == -1) {
            mainForm.printConsole("Ҫɾ�����û������ڣ�");
            return 1;
        }
        ;

        //���ڵĻ���ɾ���û�
        users.getUsers().remove(users.getUsers().get(index));
        writeUsers(users);
        return 0;
    }

    //����Ȩ��
    public int grant() {
        // �ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("GRANT")) {
            // TODO��GRANT���󣡣���
        }
        String operation;
        String tablename;
        String username;
        //���Ȩ�޹ؼ����Ƿ�ΪALL PRIVILEGES
        if (sqlWords[1].toUpperCase().equals("ALL")) {
            operation = "ALL";
            tablename = sqlWords[4];
            username = sqlWords[6];
        } else {
            operation = sqlWords[1];
            tablename = sqlWords[3];
            username = sqlWords[5];
        }

        //��ȡUsers
        Users users = readUsers();

        //����û����Ƿ����
        int index = users.isExist(username);
        if (!username.equals("PUBLIC") && index == -1) {  //�û����������Ҳ���PUBLIC
            mainForm.printConsole("�û�������");
            return 1;
        }

        //�õ��û�����
        User user = null;
        if (!username.equals("PUBLIC")) {
            user = users.getUsers().get(index);
        }

        //��PUBLIC�Ĵ���
        if (user != null) {
            user.dellimit(tablename, operation);
            mainForm.printConsole("�û�" + user.getName() + "�Ա�" + tablename + "��" + operation + "����Ȩ��������");

        }
        //PUBLIC�Ĵ���
        else {
            //���������û�������Լ��
            for (int i = 0; i < users.getUsers().size(); i++) {
                users.getUsers().get(i).dellimit(tablename, operation);
            }
            mainForm.printConsole("�����û��Ա�" + tablename + "��" + operation + "����Ȩ��������");

        }
        writeUsers(users);
        return 0;
    }


    //�ջ�Ȩ��
    public int revoke() {
        // �ؼ��ּ��
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("REVOKE")) {
            // TODO��REVOKE���󣡣���
        }
        String operation;
        String tablename;
        String username;
        //���Ȩ�޹ؼ����Ƿ�ΪALL PRIVILEGES
        if (sqlWords[1].toUpperCase().equals("ALL")) {
            operation = "ALL";
            tablename = sqlWords[4];
            username = sqlWords[6];
        } else {
            operation = sqlWords[1];
            tablename = sqlWords[3];
            username = sqlWords[5];
        }

        //��ȡUsers
        Users users = readUsers();

        //����û����Ƿ����
        int index = users.isExist(username);
        if (!username.equals("PUBLIC") && index == -1) {  //�û����������Ҳ���PUBLIC
            mainForm.printConsole("�û�������");
            return 1;
        }

        //�õ��û�����
        User user = null;
        if (!username.equals("PUBLIC")) {
            user = users.getUsers().get(index);
        }

        //��PUBLIC�Ĵ���
        if (user != null) {
            user.addlimit(tablename, operation);
            mainForm.printConsole("�û�" + user.getName() + "�Ա�" + tablename + "��" + operation + "����Ȩ�����ջ�");
        }
        //PUBLIC�Ĵ���
        else {
            //���������û�������Լ��
            for (int i = 0; i < users.getUsers().size(); i++) {
                users.getUsers().get(i).addlimit(tablename, operation);
            }
            mainForm.printConsole("�����û��Ա�" + tablename + "��" + operation + "����Ȩ�����ջ�");

        }
        writeUsers(users);
        return 0;
    }

    // �ҳ�database�ж�Ӧ���ֵ�Table����
    private static Table findTable(String tableName, Database database) {
        Table table = null;
        int index = 0;
        for (int i = 0; i < database.getDatabase().size(); i++) {
            if (tableName.equals(database.getDatabase().get(i).getName())) {
                index = i;
                table = database.getDatabase().get(index);
                break;
            }
        }
        return table;
    }

    //�ҳ�ĳ����ĳ��������Ӧ���±�
    private static int findIndexByColname(String colName, Table table) {
        int index = -1;
        //����table��������
        for (int i = 0; i < 10; i++) {
            if (colName.equals(table.getColName(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    //�ҳ�ĳ���ж�Ӧ�±����Ƿ���valueֵ
    private int findValue(String value, Table table, int colIndex) {
        int find = -1;
        for (int i = 0; i < table.getTupleNum(); i++) {
            //System.err.println(table.getCell(i,colIndex));
            if (table.getCell(i, colIndex).equals(value)) {
                find = i;
                //mainForm.printConsole("�ҵ���" + value);
            }
        }

        return find;
    }

    private int countValue(String value, Table table, int colIndex) {
        int count = 0;
        for (int i = 0; i < table.getTupleNum(); i++) {
            //System.err.println(table.getCell(i,colIndex));
            if (table.getCell(i, colIndex).equals(value)) {
                count += 1;
            }
        }
        mainForm.printConsole(value + "ֵ��" + count + "��");
        return count;
    }

    //Where�Ӿ��ж�
    private boolean[] judge(String condition, Table table) {

        boolean[] flag = new boolean[20];
        for (int i = 0; i < 20; i++) {
            flag[i] = false;
        }

        //�����������߼�����
        if (condition.contains("AND") || condition.contains("and")) {
            // TODO:��
            String[] strr = condition.trim().split("AND");

            //�����������Ե�flag
            boolean[][] flags = new boolean[2][20];
            for (int i = 0; i < 20; i++) {
                flags[0][i] = false;
                flags[1][i] = false;
            }
            //ÿ��ѭ������һ������
            for (int i = 0; i < 2; i++) {
                String[] str = strr[i].trim().split("=");
                String colName = str[0];
                String value = str[1];
                mainForm.printConsole(colName + "=" + value);
                int index = findIndexByColname(colName, table);
                for (int j = 0; j < table.getTupleNum(); j++) {
                    if (index == -1) {
                        mainForm.printConsole("WHERE�Ӿ�����������");
                        return flag;
                    }
                    if (table.getCell(j, index).equals(value)) {
                        flags[i][j] = true;
                    }
                }
            }
            //����flags��������õ�flag
            for (int i = 0; i < 20; i++) {
                flag[i] = (flags[0][i] && flags[1][i]);
            }
        } else if (condition.contains("OR") || condition.contains("or")) {
            // TODO:��
            String[] strr = condition.trim().split("OR");

            //�����������Ե�flag
            boolean[][] flags = new boolean[2][20];
            for (int i = 0; i < 20; i++) {
                flags[0][i] = false;
                flags[1][i] = false;
            }
            //ÿ��ѭ������һ������
            for (int i = 0; i < 2; i++) {
                String[] str = strr[i].trim().split("=");
                String colName = str[0];
                String value = str[1];
                mainForm.printConsole(colName + "=" + value);
                int index = findIndexByColname(colName, table);
                for (int j = 0; j < table.getTupleNum(); j++) {
                    if (index == -1) {
                        mainForm.printConsole("WHERE�Ӿ�����������");
                        return flag;
                    }
                    if (table.getCell(j, index).equals(value)) {
                        flags[i][j] = true;
                    }
                }
            }
            //����flags��������õ�flag
            for (int i = 0; i < 20; i++) {
                flag[i] = (flags[0][i] || flags[1][i]);
            }
        } else if (condition.contains("NOT") || condition.contains("not") || condition.contains("!")) {
            // TODO:��
            String strr;
            strr = condition.trim().replaceAll("NOT", "");
            strr = condition.trim().replaceAll("!", "");
            System.out.println(strr);
            String[] str = strr.split("=");
            String colName = str[0].trim();
            String value = str[1].trim();
            mainForm.printConsole(colName + "=" + value);
            int index = findIndexByColname(colName, table);
            for (int i = 0; i < table.getTupleNum(); i++) {
                if (index == -1) {
                    mainForm.printConsole("WHERE�Ӿ�����������");
                    return flag;
                }
                if (table.getCell(i, index).equals(value)) {
                    flag[i] = true;
                    mainForm.printConsole("ROW:" + i);
                }
            }
            //flagȡ��
            for (int i = 0; i < table.getTupleNum(); i++) {
                flag[i] = !flag[i];
            }
        } else {//һ���������߼�����
            String[] str = condition.trim().split("=");
            String colName = str[0];
            String value = str[1];
            mainForm.printConsole(colName + "=" + value);
            int index = findIndexByColname(colName, table);
            for (int i = 0; i < table.getTupleNum(); i++) {
                if (index == -1) {
                    mainForm.printConsole("WHERE�Ӿ�����������");
                    return flag;
                }
                if (table.getCell(i, index).equals(value)) {
                    flag[i] = true;
                    mainForm.printConsole("ROW:" + i);
                }
            }
        }
        return flag;
    }
}
