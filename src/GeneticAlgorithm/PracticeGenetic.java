package GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static ExcelCommunication.ExcelCommunicator.getStockSetDataFromCSV;


public class PracticeGenetic {

    public PracticeGenetic() {
    }

    public void mainMethod() {

        //StockSetData stockSetData = getStockSetDataFromCSV("D:\\Stocks\\PriceActionAnalysis\\DemoExcelSheets\\DemoData.csv");
        //StockSetData stockSetData = getStockSetDataFromCSV("D:\\Stocks\\CSV files\\Book3.csv");
        StockSetData stockSetData = getStockSetDataFromCSV("D:\\Stocks\\CSV files\\olddata.csv");
        stockSetData.initiateSorterColumn();



//        ArrayList<ParameterSet> paramList = Algorithm.generateArrayListParameterSet(20, stockSetData, "4% - 2%");
//        ArrayList<ParameterSet> subParamList = Algorithm.getTopXParameterSetsByStrategy(paramList, "4% - 2%", 5);
//
//        paramList.forEach(x -> System.out.println(x.getStrategyEffectiveness("4% - 2%")));
//
//        double value = Algorithm.getHighestWinRateFromArrayListParameterSet(paramList, "4% - 2%");
//
//        ParameterSet topParameterSet = new ParameterSet(stockSetData);
//        ParameterSet topParameterSetMutated = new ParameterSet(stockSetData);
//
//        topParameterSet = new ParameterSet(subParamList.get(0));
//        topParameterSetMutated = new ParameterSet(subParamList.get(0));
//        topParameterSetMutated.mutateRandomParameter(stockSetData);
//
//
//
//        System.out.println(value);

        //Algorithm.simpleMutatingAlgorithm(stockSetData, "4% - 2%", 40, 10, 2, 1000,200);
        Algorithm.simpleCrossoverAlgorithm(stockSetData, "3% - 2%", 100, 5, 1000, 100);
        System.out.println("asdf");
    }



}

//    double bestWinRate = 0;
//    ParameterSet winnerParameterSet = new ParameterSet(stockSetData);
//
//        for (int i = 0; i < 100; i++) {
//        ParameterSet newParameterSet1 = new ParameterSet(stockSetData);
//        newParameterSet1.generateRandomParameters(stockSetData);
//        newParameterSet1.calculateStrategyFitness(stockSetData);
//        //newParameterSet1.printStrategyEffectiveness("4% - 2%");
//        if (newParameterSet1.getNumberOfResults() > 50 && newParameterSet1.getStrategyEffectiveness("4% - 2%") > bestWinRate && newParameterSet1.getStrategyObject("4% - 2%").getNoMovementRatio() < 0.1) {
//        bestWinRate = newParameterSet1.getStrategyEffectiveness("4% - 2%");
//        winnerParameterSet = newParameterSet1;
//        }
//        }