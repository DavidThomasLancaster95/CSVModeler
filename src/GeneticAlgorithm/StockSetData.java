package GeneticAlgorithm;

import java.util.ArrayList;

public class StockSetData {
    //public int size;

    //label values
    private ArrayList<ColumnAndName> labelValues;

    //sorting values
    private ArrayList<ColumnAndName> sortingValues;

    //fitness values
    private ArrayList<ColumnAndName> fitnessValues;

    //other
    private ArrayList<Double> fitnessScore;
    private ArrayList<Boolean> sorterScoreValues;

    public StockSetData() {
        labelValues = new ArrayList<>();
        sortingValues = new ArrayList<>();
        fitnessValues = new ArrayList<>();
        //initiateSorterColumn();
    }

    public void initiateSorterColumn() {
        this.sorterScoreValues = new ArrayList<>();
        for (int i = 0; i < labelValues.get(0).getArrayList().size(); i++) {
            this.sorterScoreValues.add(true);
        }
    }

    public void setAllSorterScoreSetValuesToTrue() {
        for (int i = 0; i < sorterScoreValues.size(); i++) {
            sorterScoreValues.set(i, true);
        }
    }


    public void setSorterScoreValues(int index, boolean value) {
        sorterScoreValues.set(index, value);
    }

    public ArrayList<Boolean> getSorterScoreValues() {
        return sorterScoreValues;
    }

    public ArrayList<ColumnAndName> getLabelValues() {
        return labelValues;
    }

    public ArrayList<ColumnAndName> getSortingValues() {
        return sortingValues;
    }

    public ArrayList<ColumnAndName> getFitnessValues() {
        return fitnessValues;
    }

    public ArrayList<Double> getFitnessScore() {
        return fitnessScore;
    }
}
