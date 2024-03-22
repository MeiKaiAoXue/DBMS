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

        // 字符串格式化，传入的sql前后没有空格没有分号
        // 处理后返回的sql中间的空白均会替换为一个空格
        this.sql = format(sql);
        this.mainForm = mainForm;
    }

    public Analyse(String sql) {

        // 字符串格式化，传入的sql前后没有空格没有分号
        // 处理后返回的sql中间的空白均会替换为一个空格
        this.sql = format(sql);
    }

    // 字符串格式化，sql中间的空白均会替换为一个空格
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

    // 创建表
    public int createTable() {

        // 关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("CREATE")) {
            // TODO：CREATE错误！！！
        }
        if (!sqlWords[1].toUpperCase().equals("TABLE")) {
            // TODO：TABLE错误！！！
        }

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            database = new Database();
        }

        // 从CREATE TABLE后到左括号，获取表名
        String tableName = "";
        tableName = sql.substring(13, sql.indexOf("(")).trim(); //一定要trim
        //System.out.println("SIZE: " + database.getDatabase().size());
        mainForm.printConsole("SIZE: " + database.getDatabase().size());

        // 循环database看表名是否重复，若重复直接return，否则创建新的Table对象
        for (int i = 0; i < database.getDatabase().size(); i++) {
            if (database.getDatabase().get(i).getName().equals(tableName)) {
                //System.out.println("表已存在");
                mainForm.printConsole("表已存在");
                return 1;
                //TODO:表已存在，异常
            }
        }
        Table table = new Table(tableName);
        //System.out.println("表名: " + tableName);
        mainForm.printConsole("表名: " + tableName);

        // 先找到属性列表的结尾，然后从左括号开始到结尾，获取属性
        // index2指向属性列表的结尾，找出index2
        String attributes = "";
        int index2;
        if (sql.contains("PRIMARY KEY")) { // 先找PRIMARY子句
            index2 = sql.indexOf("PRIMARY KEY");
        } else if (sql.contains("FOREIGN KEY")) { // 没有PRIMARY子句就找FOREIGN KEY子句
            index2 = sql.indexOf("FOREIGN KEY");
        } else {
            index2 = sql.length(); // 都没有的话就直接到结尾
        }
        // 提取出属性列表
        attributes = sql.substring(sql.indexOf("(") + 1, index2 - 1); // substring包括开头不包括结尾，index2是P或F，-1后是空格或逗号
        attributes = attributes.trim(); // 去掉多余的空格
        // 属性列表按逗号分成一“对对”属性
        String[] attributesWords = attributes.split(",");
        // 解析每一“对”属性
        for (int i = 0; i < attributesWords.length; i++) {
            String attribute = format(attributesWords[i]).trim();
            String[] attributeWords = attribute.split("\\s"); // 把属性名和类型两个关键字分开
            if (attributeWords.length != 2) { // 检查是否是属性名和属性类型两个关键字
                // TODO：属性错误！！！
                return 1;
            } else {
                // TODO：插入数据！！！
                table.setColName(attributeWords[0], table.getColNum());
                table.setColType(attributeWords[1], table.getColNum());
                table.setColNum(table.getColNum() + 1);
            }
            //System.out.println("属性" + i + ": " + attribute);
            mainForm.printConsole("属性" + i + ": " + attribute);
        }

        // 处理PRIMARY KEY
        if (sql.contains("PRIMARY KEY")) {

            /*
             * PRIMARY
             * KEY关键字后面的子串，为什么不直接用sql而是重新定义一个子串，因为属性部分类似CHAR(20)这样的的括号数量不确定，直接用不好定位PRIMARY
             * KEY后面的第一个括号
             */
            String sub1 = sql.substring(sql.indexOf("PRIMARY KEY") + 11);

            String pk = sub1.substring(sub1.indexOf("(") + 1, sub1.indexOf(")")).trim();// 分出主键

            String[] primeAttribute = pk.split(","); // 主属性，由主键分词得来
            for (int i = 0; i < primeAttribute.length; i++) {
                //System.out.println("主属性" + i + ": " + primeAttribute[i]);
                mainForm.printConsole("主属性" + i + ": " + primeAttribute[i]);
                int pkIndex = findIndexByColname(primeAttribute[i], table);
                if (pkIndex == -1) {
                    //System.out.println("找不到主键列名");
                    mainForm.printConsole("找不到主键列名");
                    return 1;
                }
                table.setPk(pkIndex);   //设置外键
            }
        }

        // 处理FOREIGN KEY
        if (sql.contains("FOREIGN KEY")) {
            String sub2 = sql.substring(sql.indexOf("FOREIGN KEY") + 11);// 子串从FOREIGN KEY后的括号开始
            String[] fks = sub2.split(",");// 每有一个逗号就有一个外键

            // 每次循环处理一个外键
            for (int i = 0; i < fks.length; i++) {
                // 替换掉空格和关键字，只保留“(外键)主表(主表中被参照的主键)”
                fks[i] = fks[i].replaceAll("FOREIGN KEY", "").trim();
                fks[i] = fks[i].replaceAll("REFERENCES", "").trim();
                fks[i] = fks[i].replaceAll("[\\s]+", "").trim();

                String fk = fks[i].substring(fks[i].indexOf("(") + 1, fks[i].indexOf(")"));

                String mainTableName = fks[i].substring(fks[i].indexOf(")") + 1, fks[i].indexOf("(", fks[i].indexOf("(") + 1));

                String mainTablePk = fks[i].substring(fks[i].indexOf("(", fks[i].indexOf("(") + 1) + 1,
                        fks[i].indexOf(")", fks[i].indexOf(")", fks[i].indexOf(")") + 1)));

                //检查外键名是否在本表中
                int indexFk = findIndexByColname(fk, table);
                if (indexFk == -1) {
                    //System.out.println("外键名有误");
                    mainForm.printConsole("外键名有误");
                    return 1;
                }
                //检查外键名是否在被参照的主表中，database中查找被参照的并得到对象
                Table mainTable = findTable(mainTableName, database);
                if (mainTable == null) {
                    //System.out.println("被参照的表不存在");
                    mainForm.printConsole("被参照的表不存在");
                    return 1;
                }
                if (findIndexByColname(mainTablePk, mainTable) == -1) {
                    //System.out.println("被参照的主键不存在");
                    mainForm.printConsole("被参照的主键不存在");
                    return 1;
                }
                //设置外键
                table.setFk(indexFk, mainTableName, mainTablePk);

                //System.out.print("外键" + "[" + i + "]: " + fk);
                //.out.print("参照了表" + mainTableName);
                //System.out.println("中的主键" + mainTablePk);
                mainForm.printConsole("外键" + "[" + i + "]: " + fk + "参照了表" + mainTableName + "中的主键" + mainTablePk);

            }
            // TODO
        }

        //将这个表添加到database中并写入文件
        database.getDatabase().add(table);
        writeDatabase(database);
        return 0;
    }

    public int alterTable() {

        // 关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("ALTER")) {
            // TODO：ALTER错误！！！
        }
        if (!sqlWords[1].toUpperCase().equals("TABLE")) {
            // TODO：TABLE错误！！！
        }

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            //System.out.println("请先建表");
            mainForm.printConsole("请先建表");
            return 1;
        }


        // 提取表名
        String tableName = sqlWords[2];
        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        if (table == null) {
            //System.out.println("未找到该表");
            mainForm.printConsole("未找到该表");
            return 1;
        }

        // ADD/DROP
        if (sqlWords[3].toUpperCase().equals("ADD")) {  //添加一列
            String colName = sqlWords[4];
            String colType = sqlWords[5];
            int index=-1;//找到第一个空列作为添加的列的地方
            for(int i=0;i<10;i++){
                if(table.getColName(i).equals("#NULL") && table.getColType(i).equals("#NULL")){
                    index = i;
                    break;
                }
            }
            if(index == -1){
                mainForm.printConsole("内存不足，无法添加新列");
                return 1;
            }
            //是否重名
            if(findIndexByColname(colName,table)!=-1){
                mainForm.printConsole("该列已存在！");
                return 1;
            }
            table.setColName(colName, index);
            table.setColType(colType, index);
            table.setColNum(table.getColNum() + 1);
            //System.out.println("成功添加" + colName + "类型" + colType + "到表" + tableName);
            mainForm.printConsole("成功添加" + colName + "类型" + colType + "到表" + tableName);
        } else if (sqlWords[3].toUpperCase().equals("DROP")) {  //删除一列
            String colName = sqlWords[4];
            // 获取该列名对应的下标
            int index = findIndexByColname(colName, table);
            if (index == -1) {  //列名不存在
                //System.out.println("未找到该列");
                mainForm.printConsole("未找到该列");
                return 1;
            } else if (table.isPk(index)) {    //该列为主属性
                //System.out.println("列"+colName+"为主键，拒绝删除");
                mainForm.printConsole("列" + colName + "为主键，拒绝删除");
                return 1;
            } else {
                //删除这一列的所有东西
                table.setColName("#NULL", index);
                table.setColType("#NULL", index);
                for (int i = 0; i < 20; i++) {
                    table.setCell(i, index, "#NULL");
                }
                table.setColNum(table.getColNum() -1);
            }

            //System.out.println("成功删除" + colName + "从表" + tableName);
            mainForm.printConsole("成功删除" + colName + "从表" + tableName);
        } else {
            // TODO：ADD/DROP错误！！！
        }

        //修改后的database同步到文件
        writeDatabase(database);
        return 0;
    }

    public int dropTable() {

        //关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("DROP")) {
            // TODO：DROP错误！！！
        }
        if (!sqlWords[1].toUpperCase().equals("TABLE")) {
            // TODO：TABLE错误！！！
        }

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            //System.out.println("请先建表");
            mainForm.printConsole("请先建表");
            return 1;
        }

        // 提取表名
        String tableName = sqlWords[2];

        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        if (table == null) {
            //System.out.println("未找到该表");
            mainForm.printConsole("未找到该表");
            return 1;
        }
        database.getDatabase().remove(table);

        //System.out.println("成功删除表" + tableName);
        mainForm.printConsole("成功删除表" + tableName);

        //修改后的database同步到文件
        writeDatabase(database);
        return 0;
    }

    public int insertInto() {

        //关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("INSERT")) {
            // TODO：INSERT错误！！！
        }
        if (!sqlWords[1].toUpperCase().equals("INTO")) {
            // TODO：INTO错误！！！
        }

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            //System.out.println("请先建表");
            mainForm.printConsole("请先建表");
            return 1;
        }

        // 从INSERT INTO后到左括号，获取表名
        String tableName = "";
        tableName = sql.substring(12, sql.indexOf("(")).trim();
        //System.out.println("TableName: " + tableName);
        mainForm.printConsole("TableName: " + tableName);

        //看看有没有权限
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//有权限记录
            if (mainForm.getUser().insertLimit(tableIndexInUser)) {//权限记录为受限
                mainForm.printConsole("该用户没有对表" + tableName + "的插入权限");
                return 1;
            }
        }

        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        if (table == null) {
            //System.out.println("未找到该表");
            mainForm.printConsole("未找到该表");
            return 1;
        }

        // 获取列名集合
        String sub1 = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")")).trim();
        String colNames[] = sub1.split(",");
        //System.out.println("sub1: " + sub1);
        mainForm.printConsole("sub1: " + sub1);

        // 获取值集合，先找到VALUES后的第一个左括号(能确保VALUES不在时可以抛出异常)
        String sub2 = sql
                .substring(sql.indexOf("(", sql.indexOf("VALUES")) + 1, sql.indexOf(")", sql.indexOf("VALUES"))).trim();
        String values[] = sub2.split(",");
        //System.out.println("sub2: " + sub2);
        mainForm.printConsole("sub2: " + sub2);


        String[][] fk = table.getFk();
        // 插入，每次循环处理一个属性
        for (int i = 0; i < colNames.length; i++) {
            int index = findIndexByColname(colNames[i], table); //找到要插入的列的下标（这个表里的列的下标）
            //System.out.println(index+":"+colNames[i]);
            mainForm.printConsole(index + ":" + colNames[i]);
            if (index == -1) {
                //System.out.println("属性名错误");
                mainForm.printConsole("属性名错误");
                return 1;
            } else {
                if (fk[index][0].equals("#NULL") && fk[index][1].equals("#NULL")) {   //如果不是外键就插入
                    table.setCell(table.getTupleNum(), index, values[i]);
                } else {  //如果是外键，检查一下主表主键是否有这个值
                    //检查一下主表主键是否有这个值
                    Table mainTable = findTable(fk[index][0], database);
                    if (mainTable == null) {
                        //System.out.println("外键错误：被参照的主表不存在");
                        mainForm.printConsole("外键错误：被参照的主表不存在");
                        return 1;
                    }
                    if (findValue(values[i], mainTable, findIndexByColname(fk[index][1], mainTable)) == -1) {
                        //System.out.println("主表没有"+values[i]+"这个名字的主键");
                        mainForm.printConsole("主表没有" + values[i] + "这个名字的主键");
                        return 1;
                    }
                    //赋值吧
                    table.setCell(table.getTupleNum(), index, values[i]);
                    mainForm.printConsole("插入成功");
                }
            }
        }


        //上面先假装插入，这里再检查，若违反实体完整性，就删掉刚刚插入的元组
        boolean entityIntegrity = true;
        boolean[] pk = table.getPk();
        for (int i = 0; i < table.getColNum(); i++) {
            //检查主键是否为空
            if (pk[i] && table.getCell(table.getTupleNum(), i).equals("#NULL")) { //就是刚刚插入的那行元组，看看主键是不是为空
                entityIntegrity = false;
                mainForm.printConsole("插入的元组主键" + table.getColName(i) + "为空，违反实体完整性");
                //撤销插入的元组，即置空
                for (int j = 0; j < table.getColNum(); j++) {
                    table.setCell(table.getTupleNum(), i, "#NULL");
                }
                return 1;
            }
            //检查主键是否重复
            if (pk[i] && findValue(table.getCell(table.getTupleNum(), i), table, i) != -1 && table.getPkNum() == 1) { //是主属性且主属性有重复值且单值主键
                entityIntegrity = false;
                mainForm.printConsole("插入的元组主键" + table.getColName(i) + "的值" + table.getCell(table.getTupleNum(), i) + "重复，违反实体完整性");
                //撤销插入的元组，即置空
                for (int j = 0; j < table.getColNum(); j++) {
                    table.setCell(table.getTupleNum(), i, "#NULL");
                }
                return 1;
            }
        }


        //元组数量+1
        table.setTupleNum(table.getTupleNum() + 1);
        mainForm.printConsole("成功插入" + sub2 + "到表" + tableName + "  在列" + sub1);
        mainForm.printConsole("tuplenum" + table.getTupleNum() + "ColNum" + table.getColNum());

        // 最后查一下对于多个属性的主键，是否有重复值(只能查两个)
        //mainForm.printConsole("PkNum:"+table.getPkNum());
        if (table.getPkNum() > 1) {
            int count = 0;
            int[] pks = new int[10];
            //主键的下标记录一下
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
                        mainForm.printConsole("主键的两个属性完全相同，违反了实体完整性，更新失败");
                        return 1;
                    }
                }
            }
        }

        //保存database
        writeDatabase(database);
        return 0;
    }

    public int update() {

        //关键词检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("UPDATE")) {
            // TODO：UPDATE错误！！！
        }

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("请先建表");
            return 1;
        }

        // 获取表名
        String tableName = sqlWords[1];

        //看看有没有权限
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//有权限记录
            if (mainForm.getUser().updateLimit(tableIndexInUser)) {//权限记录为受限
                mainForm.printConsole("该用户没有对表" + tableName + "的更新权限");
                return 1;
            }
        }

        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("未找到该表");
            return 1;
        }

        // 获取SET后的子串
        int index1 = sql.indexOf("WHERE");
        String sub1 = "";
        String sub2 = "";

        if (index1 != -1) { // 有WHERE子句
            sub1 = sql.substring(sql.indexOf("SET") + 3, index1).trim(); // SET后到WHERE子句前
            sub2 = sql.substring(index1 + 5).trim(); // WHERE后

            // WHERE子句判断
            boolean[] flag = judge(sub2, table);

            // 处理赋值
            String[] assignment = sub1.split(",");
            //每次循环对一列进行操作
            for (String s : assignment) {
                String colName = s.split("=")[0];
                String value = s.split("=")[1];
                // 找到列名对应的下标
                int colIndex = findIndexByColname(colName, table);
                if (colIndex == -1) {
                    mainForm.printConsole("列名错误");
                    return 0;
                }
                // 每个循环对一个元组进行操作
                for (int j = 0; j < table.getTupleNum(); j++) {
                    if (flag[j]) {    //如果这个元组需要更新的话
                        //先来看看是不是外键
                        String[][] fk = table.getFk();
                        if (fk[colIndex][0].equals("#NULL") && fk[colIndex][1].equals("#NULL")) {   //如果不是外键
                            //检查实体完整性
                            if (table.isPk(colIndex) && value.equals("#NULL")) {  //主键是否赋空值
                                mainForm.printConsole("更新的元组主键" + table.getColName(colIndex) + "为空，违反实体完整性");
                                return 1;
                            }
                            if (table.isPk(colIndex) && findValue(value, table, colIndex) != -1 && table.getPkNum() == 1) { //是主属性且主属性有重复值且单值主键
                                mainForm.printConsole("更新的元组主键" + table.getColName(colIndex) + "的值" + value + "重复，违反实体完整性");
                                return 1;
                            }
                            table.setCell(j, colIndex, value);
                        } else {  //如果是外键，检查一下主表主键是否有这个值
                            //检查一下主表主键是否有这个值
                            Table mainTable = findTable(fk[colIndex][0], database);
                            if (mainTable == null) {
                                mainForm.printConsole("外键错误：被参照的主表不存在");
                                return 1;
                            }
                            if (findValue(value, mainTable, findIndexByColname(fk[colIndex][1], mainTable)) == -1) {
                                mainForm.printConsole("主表没有" + value + "这个名字的主键");
                                return 1;
                            }
                            //检查实体完整性
                            if (table.isPk(colIndex) && value.equals("#NULL")) {  //主键是否赋空值
                                mainForm.printConsole("更新的元组主键" + table.getColName(colIndex) + "为空，违反实体完整性");
                                return 1;
                            }
                            if (table.isPk(colIndex) && findValue(value, table, colIndex) != -1 && table.getPkNum() == 1) { //是主属性且主属性有重复值且单值主键
                                mainForm.printConsole("更新的元组主键" + table.getColName(colIndex) + "的值" + value + "重复，违反实体完整性");
                                return 1;
                            }
                            //赋值吧
                            table.setCell(j, colIndex, value);
                        }
                    }
                }
                mainForm.printConsole("元组已更新" + colName + " = " + value);
            }
        } else { // 没有WHERE子句
            sub1 = sql.substring(sql.indexOf("SET") + 3).trim(); // SET后直接到末尾
            // 每一个循环对一列进行操作
            String[] assignment = sub1.split(",");
            for (String s : assignment) {
                String colName = s.split("=")[0];
                String value = s.split("=")[1];
                // 找到列名对应的下标
                int colIndex = findIndexByColname(colName, table);
                if (colIndex == -1) {
                    mainForm.printConsole("列名错误");
                    return 0;
                }
                // 每个循环对一个元组进行操作
                for (int j = 0; j < table.getTupleNum(); j++) {
                    //先来看看是不是外键
                    String[][] fk = table.getFk();
                    if (fk[colIndex][0].equals("#NULL") && fk[colIndex][1].equals("#NULL")) {   //如果不是外键
                        //检查实体完整性
                        if (table.isPk(colIndex)) {  //不含WHERE子句的UPDATE不能对主键操作
                            mainForm.printConsole("不建议不含WHERE子句的不安全的UPDATE语句对主键操作，故该操作已拒绝");
                            return 1;
                        }
                        table.setCell(j, colIndex, value);
                    } else {  //如果是外键，检查一下主表主键是否有这个值
                        //检查一下主表主键是否有这个值
                        Table mainTable = findTable(fk[colIndex][0], database);
                        if (mainTable == null) {
                            mainForm.printConsole("外键错误：被参照的主表不存在");
                            return 1;
                        }
                        if (findValue(value, mainTable, findIndexByColname(fk[colIndex][1], mainTable)) == -1) {
                            mainForm.printConsole("主表没有" + value + "这个名字的主键");
                            return 1;
                        }
                        //检查实体完整性
                        if (table.isPk(colIndex)) {  //不含WHERE子句的UPDATE不能对主键操作
                            mainForm.printConsole("不建议不含WHERE子句的不安全的UPDATE语句对主键操作，故该操作已拒绝");
                            return 1;
                        }
                        //赋值吧
                        table.setCell(j, colIndex, value);
                    }
                }
                mainForm.printConsole("所有元组已更新" + colName + " = " + value);
            }
        }

        // 最后查一下对于多个属性的主键，是否有重复值(只能查两个)
        //System.out.println("PkNum:"+table.getPkNum());
        if (table.getPkNum() > 1) {
            int count = 0;
            int[] pks = new int[10];
            //主键的下标记录一下
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
                        mainForm.printConsole("主键的两个属性完全相同，违反了实体完整性，更新失败");
                        return 1;
                    }
                }
            }
        }

        // 保存数据库
        writeDatabase(database);
        return 0;
    }

    public int delete() {

        // 关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("DELETE")) {
            // TODO：INSERT错误！！！
        }
        if (!sqlWords[1].toUpperCase().equals("FROM")) {
            // TODO：INTO错误！！！
        }

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("请先建表");
            return 1;
        }

        // 获取表名
        String tableName = sqlWords[2];

        //看看有没有权限
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//有权限记录
            if (mainForm.getUser().deleteLimit(tableIndexInUser)) {//权限记录为受限
                mainForm.printConsole("该用户没有对表" + tableName + "的删除权限");
                return 1;
            }
        }

        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("未找到该表");
            return 1;
        }

        // 获取WHERE子句
        String sub1 = "";
        int index1 = sql.indexOf("WHERE");
        if (index1 != -1) { // 有WHERE子句
            sub1 = sql.substring(index1 + 5).trim(); // WHERE后
            boolean[] flag;
            // 有一个问题，删除一个元组要把后面的放到前面来补上这个空位
            // 为什么要两层循环，唉，很难用文字解释
            int tupleNum = table.getTupleNum();
            for (int i = 0; i < tupleNum; i++) { //这里的不用get方法，以防循环次数会随着循环变化而变化
                for (int s = 0; s < table.getTupleNum(); s++) { //这里的table.getTupleNum会随着循环变化而变化
                    mainForm.printConsole("i=" + i + "s" + s);
                    flag = judge(sub1, table);   //每次循环后元组顺序都会变，所以要重新更新flag数组
                    if (flag[s]) {
                        //首先先把该元组删除
                        for (int j = 0; j < 10; j++) {
                            table.setCell(s, j, "#NULL");
                        }
                        //然后将后面的元组复制到刚删掉的元组上
                        for (int j = s; j < table.getTupleNum() - 1; j++) {
                            for (int k = 0; k < 10; k++) {
                                table.setCell(j, k, table.getCell(j + 1, k));
                            }
                        }
                        //把最后一个元组删掉
                        for (int t = 0; t < 10; t++) {
                            table.setCell(table.getTupleNum(), t, "#NULL");
                        }
                        //元组数-1
                        table.setTupleNum(table.getTupleNum() - 1);
                    }
                }
            }


        } else { // 没有WHERE子句，删除所有元组
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

    // 暂时不考虑JOIN
    public int select() {

        // 关键词检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("SELECT")) {
            // TODO：SELECT错误！！！
        }
        if (!sqlWords[2].toUpperCase().equals("FROM")) {
            // TODO：FROM错误！！！
        }
        String colName = sqlWords[1];
        String tableName = sqlWords[3];

        //清除输出框
        mainForm.flushResult();

        //看看有没有JOIN ON和ORDER BY
        if (sql.contains("JOIN") && sql.contains("ON")) {
            join();
            return 0;
        }
        if(sql.contains("ORDER BY")){
            order();
            return 0;
        }

        // 读取数据库
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("请先建表");
            return 1;
        }

        //看看有没有权限
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//有权限记录
            if (mainForm.getUser().selectLimit(tableIndexInUser)) {//权限记录为受限
                mainForm.printConsole("该用户没有对表" + tableName + "的查询权限");
                return 1;
            }
        }

        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("未找到该表");
            return 1;
        }

        // 获取WHERE子句
        String sub1 = "";
        int index1 = sql.indexOf("WHERE");
        boolean[] flag = new boolean[20];
        if (index1 != -1) { // 有WHERE子句
            sub1 = sql.substring(index1 + 5).trim(); // WHERE后
            flag = judge(sub1, table);

        } else { // 没有WHERE子句
            for (int i = 0; i < 20; i++) {
                flag[i] = true;
            }
        }

        //列名处理
        int[] colIndex;     //对要查询的列名进行处理，把他们的下标存入一个数组中
        String[] str = colName.split(",");//列名分词后的字符串数组
        int colNum;
        //看看查询列名是不是*
        if (colName.trim().equals("*")) { //查询所有列名
            colIndex = new int[table.getColNum()];
            colNum = table.getColNum();
            for (int i = 0; i < table.getColNum(); i++) {
                colIndex[i] = i;
            }
        } else {  //查询指定列名
            colNum = str.length;
            colIndex = new int[colNum];
            for (int i = 0; i < colNum; i++) {
                colIndex[i] = findIndexByColname(str[i], table);
                if (colIndex[i] == -1) {
                    mainForm.printConsole("列名错误");
                    return 1;
                }
            }
        }

        // 打印表头
        int count = 0;
        for (int i = 0; i < table.getTupleNum(); i++) {
            if (flag[i]) {
                count++;
            }
        }
        mainForm.printResult("表" + table.getName() + "共计" + table.getTupleNum() + "行，其中" + count + "行满足条件" + "\n");
        for (int i = 0; i < colNum; i++) {
            mainForm.printResult(table.getColName(colIndex[i]) + "\t");
        }

        mainForm.printResult("\n");

        // 打印数据
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

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("请先建表");
            return 1;
        }
        //看看有没有权限
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//有权限记录
            if (mainForm.getUser().selectLimit(tableIndexInUser)) {//权限记录为受限
                mainForm.printConsole("该用户没有对表" + tableName + "的查询权限");
                return 1;
            }
        }
        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        Table joinTable = findTable(joinTablename, database);
        if (table == null || joinTable == null) {
            mainForm.printConsole("未找到表");
            return 1;
        }

        int index1 = findIndexByColname(joinColname, table);
        int index2 = findIndexByColname(joinColname, joinTable);
        if (index1 == -1 || index2 == -1) {
            mainForm.printConsole("ON后列名不存在");
            return 1;
        }

        //合成新表头
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
        if(newTableColNum!=newTable.getColNum()){   //调试用
            System.out.println("两种方式得到的列数量不一样！");
        }
        //两表数据连接
        for (int a = 0; a < table.getTupleNum(); a++) {
            for (int b = 0; b < joinTable.getTupleNum(); b++) {
                //如果连接的属性值相等，就连上
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
        // 获取WHERE子句
        String sub1 = "";
        int indexW = sql.indexOf("WHERE");
        boolean[] flag = new boolean[20];
        if (indexW != -1) { // 有WHERE子句
            sub1 = sql.substring(indexW + 5).trim(); // WHERE后
            flag = judge(sub1, newTable);

        } else { // 没有WHERE子句
            for (int t = 0; t < 20; t++) {
                flag[t] = true;
            }
        }
        //列名处理
        int[] colIndex;     //对要查询的列名进行处理，把他们的下标存入一个数组中
        String[] cols = colName.split(",");//列名分词后的字符串数组
        int colNum;
        //看看查询列名是不是*
        if (colName.trim().equals("*")) { //查询所有列名
            colIndex = new int[newTable.getColNum()];
            colNum = newTable.getColNum();
            for (int t = 0; t < newTable.getColNum(); t++) {
                colIndex[t] = t;
            }
        } else {  //查询指定列名
            colNum = cols.length;
            colIndex = new int[colNum];
            for (int t = 0; t < colNum; t++) {
                colIndex[t] = findIndexByColname(cols[t], newTable);
                if (colIndex[t] == -1) {
                    mainForm.printConsole("列名错误");
                    return 1;
                }
            }
        }

        // 打印表头
        int count = 0;
        for (int t = 0; t < newTable.getTupleNum(); t++) {
            if (flag[t]) {
                count++;
            }
        }
        mainForm.printResult("表" + newTable.getName() + "共计" + newTable.getTupleNum() + "行，其中" + count + "行满足条件" + "\n");
        System.out.println(colNum+""+newTable.getColNum());
        for (int t = 0;t < colNum; t++) {
            mainForm.printResult(newTable.getColName(colIndex[t]) + "\t");
        }

        mainForm.printResult("\n");

        // 打印数据
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

        //读取数据库
        Database database = readDatabase();
        if (database == null) {
            mainForm.printConsole("请先建表");
            return 1;
        }
        //看看有没有权限
        int tableIndexInUser = mainForm.getUser().findLimitByTablename(tableName);
        if (tableIndexInUser != -1) {//有权限记录
            if (mainForm.getUser().selectLimit(tableIndexInUser)) {//权限记录为受限
                mainForm.printConsole("该用户没有对表" + tableName + "的查询权限");
                return 1;
            }
        }
        // database中查找该表并得到对象
        Table table = findTable(tableName, database);
        if (table == null) {
            mainForm.printConsole("未找到表");
            return 1;
        }

        System.out.println(orderColname);

        int indexOrderCol = findIndexByColname(orderColname, table);
        if (indexOrderCol == -1) {
            mainForm.printConsole("ORDER BY后列名不存在");
            return 1;
        }


        // 获取WHERE子句
        String sub1 = "";
        int indexW = sql.indexOf("WHERE");
        boolean[] flag = new boolean[20];
        if (indexW != -1) { // 有WHERE子句
            sub1 = sql.substring(indexW + 5,sql.indexOf("ORDER BY")).trim(); // WHERE后
            flag = judge(sub1, table);

        } else { // 没有WHERE子句
            for (int i = 0; i < 20; i++) {
                flag[i] = true;
            }
        }

        //列名处理
        int[] colIndex;     //对要查询的列名进行处理，把他们的下标存入一个数组中
        String[] cols = colName.split(",");//列名分词后的字符串数组
        int colNum;
        //看看查询列名是不是*
        if (colName.trim().equals("*")) { //查询所有列名
            colIndex = new int[table.getColNum()];
            colNum = table.getColNum();
            for (int i = 0; i < table.getColNum(); i++) {
                colIndex[i] = i;
            }
        } else {  //查询指定列名
            colNum = cols.length;
            colIndex = new int[colNum];
            for (int i = 0; i < colNum; i++) {
                colIndex[i] = findIndexByColname(cols[i], table);
                if (colIndex[i] == -1) {
                    mainForm.printConsole("列名错误");
                    return 1;
                }
            }
        }

        // 打印表头
        int[][] tupleIndex = new int[20][2];
        for(int i=0;i<20;i++){
            for(int j=0;j<2;j++){
                tupleIndex[i][j]=-1;
            }
        }

        //存入满足WHERE的元组中，要排序的列值
        int count = 0;
        for (int i = 0; i < table.getTupleNum(); i++) {
            if (flag[i]) {
                tupleIndex[count][0] = i;
                tupleIndex[count][1] = Integer.parseInt(table.getCell(i,indexOrderCol));
                count++;
            }
        }

        //冒泡排序
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
        }else{//默认升序
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

        mainForm.printResult("表" + table.getName() + "共计" + table.getTupleNum() + "行，其中" + count + "行满足条件" + "\n");
        System.out.println(colNum+""+table.getColNum());
        for (int i = 0;i < colNum; i++) {
            mainForm.printResult(table.getColName(colIndex[i]) + "\t");
        }

        mainForm.printResult("\n");

        // 打印数据
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < colNum; j++) {
                    mainForm.printResult(table.getCell(tupleIndex[i][0], colIndex[j]) + "\t");
            }
            mainForm.printResult("\n");
        }
        return 0;
    }

    //创建用户
    public int createUser() {
        // 关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("CREATE")) {
            // TODO：CREATE错误！！！
        }
        if (!sqlWords[1].toUpperCase().equals("USER")) {
            // TODO：USER错误！！！
        }

        //读取Users对象
        Users users = readUsers();
//        if(users == null){
//            writeUsers(new Users());
//            users = readUsers();
//        }

        //解析获取用户名和密码
        String username = sqlWords[2];
        String password = sqlWords[3];

        //检查用户名是否重复
        if (users.isExist(username) != -1) {
            mainForm.printConsole("该用户已存在！");
            return 1;
        }
        ;

        //不重复的话就添加用户
        users.getUsers().add(new User(username, password));
        mainForm.printConsole("成功添加用户 " + username + " " + password);
        writeUsers(users);
        return 0;
    }

    //删除用户
    public int dropUser() {
        // 关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("DROP")) {
            // TODO：DROP错误！！！
        }
        if (!sqlWords[1].toUpperCase().equals("USER")) {
            // TODO：USER错误！！！
        }

        //读取Users对象
        Users users = readUsers();

        //解析获取用户名和密码
        String username = sqlWords[2];

        //不能自己删除自己
        if (username.equals(mainForm.getUser().getName())) {
            mainForm.printConsole("不能自己删除自己啊！");
            return 1;
        }

        //检查用户名是否存在
        int index = users.isExist(username);
        if (index == -1) {
            mainForm.printConsole("要删除的用户不存在！");
            return 1;
        }
        ;

        //存在的话就删除用户
        users.getUsers().remove(users.getUsers().get(index));
        writeUsers(users);
        return 0;
    }

    //授予权限
    public int grant() {
        // 关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("GRANT")) {
            // TODO：GRANT错误！！！
        }
        String operation;
        String tablename;
        String username;
        //检查权限关键字是否为ALL PRIVILEGES
        if (sqlWords[1].toUpperCase().equals("ALL")) {
            operation = "ALL";
            tablename = sqlWords[4];
            username = sqlWords[6];
        } else {
            operation = sqlWords[1];
            tablename = sqlWords[3];
            username = sqlWords[5];
        }

        //读取Users
        Users users = readUsers();

        //检查用户名是否存在
        int index = users.isExist(username);
        if (!username.equals("PUBLIC") && index == -1) {  //用户名不存在且不是PUBLIC
            mainForm.printConsole("用户名错误");
            return 1;
        }

        //拿到用户对象
        User user = null;
        if (!username.equals("PUBLIC")) {
            user = users.getUsers().get(index);
        }

        //非PUBLIC的处理
        if (user != null) {
            user.dellimit(tablename, operation);
            mainForm.printConsole("用户" + user.getName() + "对表" + tablename + "的" + operation + "操作权限已授予");

        }
        //PUBLIC的处理
        else {
            //遍历所有用户添加这个约束
            for (int i = 0; i < users.getUsers().size(); i++) {
                users.getUsers().get(i).dellimit(tablename, operation);
            }
            mainForm.printConsole("所有用户对表" + tablename + "的" + operation + "操作权限已授予");

        }
        writeUsers(users);
        return 0;
    }


    //收回权限
    public int revoke() {
        // 关键字检查
        String sqlWords[] = sql.split("[\\s]");
        if (!sqlWords[0].toUpperCase().equals("REVOKE")) {
            // TODO：REVOKE错误！！！
        }
        String operation;
        String tablename;
        String username;
        //检查权限关键字是否为ALL PRIVILEGES
        if (sqlWords[1].toUpperCase().equals("ALL")) {
            operation = "ALL";
            tablename = sqlWords[4];
            username = sqlWords[6];
        } else {
            operation = sqlWords[1];
            tablename = sqlWords[3];
            username = sqlWords[5];
        }

        //读取Users
        Users users = readUsers();

        //检查用户名是否存在
        int index = users.isExist(username);
        if (!username.equals("PUBLIC") && index == -1) {  //用户名不存在且不是PUBLIC
            mainForm.printConsole("用户名错误");
            return 1;
        }

        //拿到用户对象
        User user = null;
        if (!username.equals("PUBLIC")) {
            user = users.getUsers().get(index);
        }

        //非PUBLIC的处理
        if (user != null) {
            user.addlimit(tablename, operation);
            mainForm.printConsole("用户" + user.getName() + "对表" + tablename + "的" + operation + "操作权限已收回");
        }
        //PUBLIC的处理
        else {
            //遍历所有用户添加这个约束
            for (int i = 0; i < users.getUsers().size(); i++) {
                users.getUsers().get(i).addlimit(tablename, operation);
            }
            mainForm.printConsole("所有用户对表" + tablename + "的" + operation + "操作权限已收回");

        }
        writeUsers(users);
        return 0;
    }

    // 找出database中对应名字的Table对象
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

    //找出某表中某列名所对应的下标
    private static int findIndexByColname(String colName, Table table) {
        int index = -1;
        //遍历table的所有列
        for (int i = 0; i < 10; i++) {
            if (colName.equals(table.getColName(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    //找出某表中对应下标列是否有value值
    private int findValue(String value, Table table, int colIndex) {
        int find = -1;
        for (int i = 0; i < table.getTupleNum(); i++) {
            //System.err.println(table.getCell(i,colIndex));
            if (table.getCell(i, colIndex).equals(value)) {
                find = i;
                //mainForm.printConsole("找到了" + value);
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
        mainForm.printConsole(value + "值有" + count + "个");
        return count;
    }

    //Where子句判断
    private boolean[] judge(String condition, Table table) {

        boolean[] flag = new boolean[20];
        for (int i = 0; i < 20; i++) {
            flag[i] = false;
        }

        //两个条件的逻辑运算
        if (condition.contains("AND") || condition.contains("and")) {
            // TODO:与
            String[] strr = condition.trim().split("AND");

            //两个条件各自的flag
            boolean[][] flags = new boolean[2][20];
            for (int i = 0; i < 20; i++) {
                flags[0][i] = false;
                flags[1][i] = false;
            }
            //每次循环处理一个条件
            for (int i = 0; i < 2; i++) {
                String[] str = strr[i].trim().split("=");
                String colName = str[0];
                String value = str[1];
                mainForm.printConsole(colName + "=" + value);
                int index = findIndexByColname(colName, table);
                for (int j = 0; j < table.getTupleNum(); j++) {
                    if (index == -1) {
                        mainForm.printConsole("WHERE子句中列名错误");
                        return flag;
                    }
                    if (table.getCell(j, index).equals(value)) {
                        flags[i][j] = true;
                    }
                }
            }
            //两个flags做与运算得到flag
            for (int i = 0; i < 20; i++) {
                flag[i] = (flags[0][i] && flags[1][i]);
            }
        } else if (condition.contains("OR") || condition.contains("or")) {
            // TODO:或
            String[] strr = condition.trim().split("OR");

            //两个条件各自的flag
            boolean[][] flags = new boolean[2][20];
            for (int i = 0; i < 20; i++) {
                flags[0][i] = false;
                flags[1][i] = false;
            }
            //每次循环处理一个条件
            for (int i = 0; i < 2; i++) {
                String[] str = strr[i].trim().split("=");
                String colName = str[0];
                String value = str[1];
                mainForm.printConsole(colName + "=" + value);
                int index = findIndexByColname(colName, table);
                for (int j = 0; j < table.getTupleNum(); j++) {
                    if (index == -1) {
                        mainForm.printConsole("WHERE子句中列名错误");
                        return flag;
                    }
                    if (table.getCell(j, index).equals(value)) {
                        flags[i][j] = true;
                    }
                }
            }
            //两个flags做或运算得到flag
            for (int i = 0; i < 20; i++) {
                flag[i] = (flags[0][i] || flags[1][i]);
            }
        } else if (condition.contains("NOT") || condition.contains("not") || condition.contains("!")) {
            // TODO:非
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
                    mainForm.printConsole("WHERE子句中列名错误");
                    return flag;
                }
                if (table.getCell(i, index).equals(value)) {
                    flag[i] = true;
                    mainForm.printConsole("ROW:" + i);
                }
            }
            //flag取反
            for (int i = 0; i < table.getTupleNum(); i++) {
                flag[i] = !flag[i];
            }
        } else {//一个条件的逻辑运算
            String[] str = condition.trim().split("=");
            String colName = str[0];
            String value = str[1];
            mainForm.printConsole(colName + "=" + value);
            int index = findIndexByColname(colName, table);
            for (int i = 0; i < table.getTupleNum(); i++) {
                if (index == -1) {
                    mainForm.printConsole("WHERE子句中列名错误");
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
