import Backtesting.TriggerModler;
import Calculation.TimeCalculation;
import ExcelCommunication.ExcelCommunicator;
import GeneticAlgorithm.PracticeGenetic;
import Objects.DayData;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;


import static ExcelCommunication.ExcelCommunicator.*;

public class Main {
    public static void main(String[] args) throws IOException {

        //PracticeGenetic practiceGenetic = new PracticeGenetic();
        //practiceGenetic.mainMethod();

//        ExcelCommunicator excelCommunicator = new ExcelCommunicator();
//
//        //List<List<String>> records = getCSV2("D:\\Stocks\\MMM\\DataAsCSV\\FullVersion.csv");
//        List<List<String>> records = getCSV2("D:\\Stocks\\MMM\\DataAsCSV\\D4070221full.csv");
//
//        System.out.println("Downloaded from csv ");
//
//        ArrayList<DayData> fullList = getAllData(records);
//
//        fullList.forEach(DayData::initiateSecondaryIndicators);
//
//        for (DayData data: fullList) {
//            if(data.pointHighArray.stream().mapToDouble(a -> a).sum() >0) {
//                System.out.println(data.stockName);
//            }
//        }


//        ExcelCommunicator excelCommunicator = new ExcelCommunicator();
//        List<List<String>> records = getCSV2("D:\\Stocks\\MMM\\DataAsCSV\\FullVersion.csv");
//        DayData dayData = getStockByNumber(records, 20);
//        dayData.initiateSecondaryIndicators();

        //pushToCSV();







        System.out.println(TimeCalculation.ConvertTimeIntToString(27901));


        TriggerModler triggerModler = new TriggerModler("C:\\Users\\white\\Documents\\School\\CS240\\ExcelTests\\src\\", "D:\\Stocks\\MMM\\");

        triggerModler.runBackTest();
        System.out.println("asdf");

    }







}
