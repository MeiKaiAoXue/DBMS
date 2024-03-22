package db;

import java.io.Serializable;

public class Table implements Serializable {
    private String name;
    private int colNum;
    private int tupleNum;
    private String[] colName = new String[10];
    private String[] colType = new String[10];
    private String[][] tuples = new String[20][10];

    private boolean[] pk = new boolean[10];

    private String[][] fk = new String[10][2];

    public Table(String name) {
        this.name = name;
        colNum = 0;
        tupleNum = 0;
        for(int i=0;i<10;i++) {
            colName[i] = "#NULL";
            colType[i] = "#NULL";
            pk[i] = false;  //������¼��ʼ��
        }
        //Ԫ��ֵ��ʼ��
        for(int j=0;j<20;j++) {
            for(int k=0;k<10;k++) {
                tuples[j][k] = "#NULL";
            }
        }
        //�����¼��ʼ��
        for(int i=0;i<10;i++){
            for(int j=0;j<2;j++){
                fk[i][j] = "#NULL";
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getColNum() {
        return colNum;
    }

    public int getTupleNum() {
        return tupleNum;
    }

    public String getColName(int column) {
        return colName[column];
    }

    public String getColType(int column) {
        return colType[column];
    }

    public String getCell(int row,int column) {
        return tuples[row][column];
    }

    public boolean[] getPk() {
        return pk;
    }

    //��������������
    public int getPkNum(){
        int count=0;
        for(int i=0;i<colNum;i++){
            if(pk[i]){
                count++;
            }
        }
        return count;
    }

    //���ض�Ӧ�±�����ǲ���������
    public boolean isPk(int index){
        return pk[index];
    }

    public String[][] getFk() {
        return fk;
    }

    public void setColName(String colName, int index) {
        this.colName[index] = colName;
    }

    public void setColType(String colType,int index) {
        this.colType[index] = colType;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public void setCell(int row,int column,String value){
        this.tuples[row][column] = value;
    }

    public void setTupleNum(int tupleNum) {
        this.tupleNum = tupleNum;
    }

    public void setPk(int index){
        this.pk[index] = true;
    }

    public void setFk(int index,String tableName,String pkName){
        this.fk[index][0] = tableName;
        this.fk[index][1] = pkName;
    }
}
