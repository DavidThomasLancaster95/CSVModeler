package GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ParameterSet {

    //label values
    private ArrayList<Parameter> labelValues;

    //sorting values
    private ArrayList<Parameter> sortingValues;

    //fitness values
    private ArrayList<Parameter> fitnessValues;

    //the calculated fitness results
    private int numberOfResults;

    private ArrayList<StrategyObject> strategyRatioList;



    public ParameterSet(StockSetData stockSetData) {
        // TODO eventually add in functionality for outside parameters...

        sortingValues = new ArrayList<>();
        for (int i = 0; i < stockSetData.getSortingValues().size(); i++) {
            String headerName = stockSetData.getSortingValues().get(i).getNameOfColumn();
            Parameter parameter = new Parameter(0,0,"inside", headerName);
            sortingValues.add(parameter);
        }

        //Initiate variables here
        strategyRatioList = new ArrayList<>();
    }

    public ParameterSet(ParameterSet inParameterSet) {
        this.sortingValues = new ArrayList<>();
        this.strategyRatioList = new ArrayList<>();
        this.numberOfResults = inParameterSet.getNumberOfResults();

        for (Parameter parameter: inParameterSet.getSortingValues()){
            sortingValues.add(parameter.copyConstructor());
        }

        for (StrategyObject strategyObject: inParameterSet.getStrategyRatioList()) {
            strategyRatioList.add(strategyObject.copyConstructor());
        }
    }

    public void generateRandomParameters(StockSetData stockSetData) {
        for (ColumnAndName columnAndNames: stockSetData.getSortingValues()) {
            //System.out.println(columnAndNames.getNameOfColumn());
            double highValue = getHighestDoubleFromArrayOfStrings(columnAndNames.getArrayList());
            double lowValue = getLowestDoubleFromArrayOfStrings(columnAndNames.getArrayList());
            double randomTopParam = getRandomDoubleBetweenTwoDoubles(highValue, lowValue);
            double randomBottomParam = getRandomDoubleBetweenTwoDoubles(randomTopParam, lowValue);

            setSpecificParameter(columnAndNames.getNameOfColumn(), randomTopParam, randomBottomParam, "inside");

            //System.out.println(randomTopParam + " - " + randomBottomParam);

            //System.out.println("randomParams");
        }

    }
    public void generateRandomParametersVersion2(StockSetData stockSetData) {
        for (ColumnAndName columnAndNames: stockSetData.getSortingValues()) {
            //System.out.println(columnAndNames.getNameOfColumn());
            double averageValue = columnAndNames.getArrayList().stream().mapToDouble(Double::valueOf).sum() / columnAndNames.getArrayList().size();
            double highValue = getHighestDoubleFromArrayOfStrings(columnAndNames.getArrayList());
            double lowValue = getLowestDoubleFromArrayOfStrings(columnAndNames.getArrayList());
            double randomTopParam = getRandomDoubleBetweenTwoDoubles(highValue, averageValue);
            double randomBottomParam = getRandomDoubleBetweenTwoDoubles(averageValue, lowValue);

            setSpecificParameter(columnAndNames.getNameOfColumn(), randomTopParam, randomBottomParam, "inside");

        }
    }

    public void calculateStrategyFitness(StockSetData stockSetData){
        // mark true or false;
        stockSetData.setAllSorterScoreSetValuesToTrue();
        for (int i = 0; i < stockSetData.getSortingValues().size(); i++) {
            // because both the ParameterSet and StockSetData have the sorting values in the same order we can use the same index
            ArrayList<String> dataColumnForTopHeaderAtI = stockSetData.getSortingValues().get(i).getArrayList();
            // get the high and low from the data parameters
            double paramHigh = this.sortingValues.get(i).high;
            double paramLow = this.sortingValues.get(i).low;
            String rangeType = this.sortingValues.get(i).rangeType;
            for (int j = 0; j < dataColumnForTopHeaderAtI.size(); j++) {
                double valueOfCell = Double.parseDouble(dataColumnForTopHeaderAtI.get(j)); //convert it to double from string
                if (rangeType.equals("inside")) {
                    if (!valueIsBetween(valueOfCell, paramHigh, paramLow)) {
                        stockSetData.setSorterScoreValues(j, false);
                    }
                }
                if (rangeType.equals("outside")) {
                    // TODO write in this functionality it will currently only accept inside parameters
                }
            }
        }

        // calculate all the strategies in the stockSetData.fitnessValues arrayList
        for (int i = 0; i < stockSetData.getFitnessValues().size(); i++) {
            String nameOfStrategy = stockSetData.getFitnessValues().get(i).getNameOfColumn();
            genericCalculateStrategy(nameOfStrategy, stockSetData);
        }

    }

    public static int return1() {
        return 1;
    }

    /**
     * This strategy lets us calculate any strategy
     * @param strategyHeader string of the header of the strategy to be processed
     * @param stockSetData the raw data that it runs the strategy on. At this point its already sorted.
     */
    public void genericCalculateStrategy(String strategyHeader, StockSetData stockSetData) {
        this.numberOfResults = Collections.frequency(stockSetData.getSorterScoreValues(), true);
        // calculate the various fitness functions... I think this will have to be manually added in each time.
        // which freaking integer matches the 4-2 trade
        // for this version of the code, I'm just choosing the first element.
        ArrayList<String> StrategyXToXArrayList = new ArrayList<>();
        // find the position in fitness vales array in stockSetData of the 4To2 ratio
        int positionOfXToXStrategy = 0;
        for (int i = 0; i < stockSetData.getFitnessValues().size(); i++) {
            ColumnAndName columnAndName = stockSetData.getFitnessValues().get(i);
            String nameOfColumn = columnAndName.getNameOfColumn();
            if (columnAndName.getNameOfColumn().equals(strategyHeader)) positionOfXToXStrategy = i;
        }

        for (int i = 0; i < stockSetData.getSorterScoreValues().size(); i++) {
            if (stockSetData.getSorterScoreValues().get(i)) {
                StrategyXToXArrayList.add(stockSetData.getFitnessValues().get(positionOfXToXStrategy).getArrayList().get(i));
            }
        }

        double frequencyOf1 = Collections.frequency(StrategyXToXArrayList, "1");
        double frequencyOf0 = Collections.frequency(StrategyXToXArrayList, "0");
        int frequencyOfNOMOVEMENT = Collections.frequency(StrategyXToXArrayList, "No Movement");
        double result = frequencyOf1 / (frequencyOf0 + frequencyOf1);

        StrategyObject strategyObject = new StrategyObject(strategyHeader, result, this.numberOfResults, frequencyOfNOMOVEMENT);
        this.strategyRatioList.add(strategyObject);
    }



    public void mutateRandomParameter(StockSetData stockSetData) {
        int numOfSortParameters = this.getSortingValues().size() - 1;
        int paramNumToMutate = getRandomIntBetweenTwoInts(numOfSortParameters, 0);

        double highValue = getHighestDoubleFromArrayOfStrings(stockSetData.getSortingValues().get(paramNumToMutate).getArrayList());
        double lowValue = getLowestDoubleFromArrayOfStrings(stockSetData.getSortingValues().get(paramNumToMutate).getArrayList());
        double randomTopParam = getRandomDoubleBetweenTwoDoubles(highValue, lowValue);
        double randomBottomParam = getRandomDoubleBetweenTwoDoubles(randomTopParam, lowValue);

        //System.out.println("Mutating Parameter " + paramNumToMutate);
        //System.out.println("Old High ->" + this.getSortingValues().get(paramNumToMutate).getHigh() + "| old low -> " + this.getSortingValues().get(paramNumToMutate).getLow());
        //System.out.println("new high -> " + randomTopParam + "| new low -> " + randomBottomParam );

        setSpecificParameter(stockSetData.getSortingValues().get(paramNumToMutate).getNameOfColumn(), randomTopParam, randomBottomParam, "inside");

    }

    public ParameterSet getParameterSetWithOneMutatedParameter() {

        return null;
    }



    public void setSpecificParameter(String parameterName, double high, double low, String rangeType) {
        for (int i = 0; i < this.sortingValues.size(); i++) {
            String headerName = this.sortingValues.get(i).getHeaderName();
            if (headerName.equals(parameterName)) {
                this.sortingValues.get(i).setHigh(high);
                this.sortingValues.get(i).setLow(low);
                this.sortingValues.get(i).setRangeType(rangeType);
            }
        }
    }

    public void setSpecificParameterWithParameter(Parameter parameter) {
        for (Parameter sortingValue : this.sortingValues) {
            String headerName = sortingValue.getHeaderName();
            if (headerName.equals(parameter.getHeaderName())) {
                sortingValue.setHigh(parameter.getHigh());
                sortingValue.setLow(parameter.getLow());
                sortingValue.setRangeType(parameter.getRangeType());
            }
        }
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public ArrayList<Parameter> getLabelValues() {
        return labelValues;
    }

    public ArrayList<Parameter> getSortingValues() {
        return sortingValues;
    }

    public ArrayList<Parameter> getFitnessValues() {
        return fitnessValues;
    }



    public boolean valueIsBetween(double inValue, double highParam, double lowParam) {
        if (inValue > highParam) return false;
        if (inValue < lowParam) return false;
        return true;
    }
    public boolean valueIsOutside(double inValue, double highParam, double lowParam) {
        //TODO finish this crap
        return true;
    }

    public double getHighestDoubleFromArrayOfStrings(ArrayList<String> inArray) {
        double highest = Double.parseDouble(inArray.get(0));
        for (int i = 0; i < inArray.size(); i++) {
            if (Double.parseDouble(inArray.get(i)) > highest) highest = Double.parseDouble(inArray.get(i));
        }
        return highest;
    }
    public double getLowestDoubleFromArrayOfStrings(ArrayList<String> inArray) {
        double lowest = Double.parseDouble(inArray.get(0));
        for (int i = 0; i < inArray.size(); i++) {
            if (Double.parseDouble(inArray.get(i)) < lowest) lowest = Double.parseDouble(inArray.get(i));
        }
        return lowest;
    }
    public static double getRandomDoubleBetweenTwoDoubles(double max, double min) {
        return min + Math.random() * (max - min);
    }
    public static int getRandomIntBetweenTwoInts(int max, int min) {
        return min + (int) (Math.random() * (max - min));
    }

    // debug functions
    public void printStrategyEffectiveness(String strategy){
        for (StrategyObject strategyObject: this.strategyRatioList) {
            if (strategyObject.nameOfStrategy.equals(strategy)) {
                System.out.println(strategyObject.winRatio);
            }
        }
    }

    public double getStrategyEffectiveness(String strategy) {
        for (StrategyObject strategyObject: this.strategyRatioList) {
            if (strategyObject.nameOfStrategy.equals(strategy)) {
                return strategyObject.winRatio;
            }
        }
        return -1; // this is the fail case
    }

    public StrategyObject getStrategyObject(String strategy) {
        for (StrategyObject strategyObject: this.strategyRatioList) {
            if (strategyObject.nameOfStrategy.equals(strategy)) {
                return strategyObject;
            }
        }
        return null;
    }

    public ArrayList<StrategyObject> getStrategyRatioList() {
        return strategyRatioList;
    }

//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ParameterSet object = (ParameterSet) obj;
        for (int i = 0; i < this.getSortingValues().size(); i++) {
            if (this.getSortingValues().get(i).getLow() != ((ParameterSet) obj).getSortingValues().get(i).getLow()) return false;
            if (this.getSortingValues().get(i).getHigh() != ((ParameterSet) obj).getSortingValues().get(i).getHigh()) return false;

        }

        return true;
    }

    public static class Parameter {
        private double high;
        private double low;
        private String rangeType;
        private String headerName;

        public Parameter(double high, double low, String rangeType, String headerName) {
            this.high = high;
            this.low = low;
            this.rangeType = rangeType;
            this.headerName = headerName;
        }

        public Parameter copyConstructor() {
            return new Parameter(this.high, this.low, this.rangeType, this.headerName);
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public String getRangeType() {
            return rangeType;
        }

        public void setRangeType(String rangeType) {
            this.rangeType = rangeType;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }

    public static class StrategyObject {
        private String nameOfStrategy;
        private double winRatio;
        private int resultsNum;
        private int numberOfNOMOVEMENT;
        private double noMovementRatio;

        public StrategyObject(String nameOfStrategy, double winRatio, int resultsNum, int numberOfNOMOVEMENT) {
            this.nameOfStrategy = nameOfStrategy;
            this.winRatio = winRatio;
            this.resultsNum = resultsNum;
            this.numberOfNOMOVEMENT = numberOfNOMOVEMENT;
            this.noMovementRatio = (double)numberOfNOMOVEMENT / (double)resultsNum;
        }

        public StrategyObject copyConstructor() {
            return new StrategyObject(this.getNameOfStrategy(), this.getWinRatio(), this.getResultsNum(), this.getNumberOfNOMOVEMENT());
        }

        public double getNoMovementRatio() {
            return noMovementRatio;
        }

        public String getNameOfStrategy() {
            return nameOfStrategy;
        }

        public void setNameOfStrategy(String nameOfStrategy) {
            this.nameOfStrategy = nameOfStrategy;
        }

        public double getWinRatio() {
            return winRatio;
        }

        public void setWinRatio(double winRatio) {
            this.winRatio = winRatio;
        }

        public int getResultsNum() {
            return resultsNum;
        }

        public void setResultsNum(int resultsNum) {
            this.resultsNum = resultsNum;
        }

        public int getNumberOfNOMOVEMENT() {
            return numberOfNOMOVEMENT;
        }

        public void setNumberOfNOMOVEMENT(int numberOfNOMOVEMENT) {
            this.numberOfNOMOVEMENT = numberOfNOMOVEMENT;
        }
    }

}
