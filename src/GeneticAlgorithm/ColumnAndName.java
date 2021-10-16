package GeneticAlgorithm;

import java.util.ArrayList;

public class ColumnAndName {
    private ArrayList<String> arrayList;
    private String nameOfColumn;

    public ColumnAndName(ArrayList<String> arrayList, String nameOfColumn) {
        this.arrayList = arrayList;
        this.nameOfColumn = nameOfColumn;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public String getNameOfColumn() {
        return nameOfColumn;
    }

    public void setNameOfColumn(String nameOfColumn) {
        this.nameOfColumn = nameOfColumn;
    }
}
