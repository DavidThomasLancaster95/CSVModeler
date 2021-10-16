package ExcelCommunication;

import GeneticAlgorithm.ColumnAndName;
import GeneticAlgorithm.StockSetData;
import Objects.DayData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelCommunicator {

    public ExcelCommunicator() {
    }

    // this is used with the genetic algorithm.
    public static StockSetData getStockSetDataFromCSV(String filePath) {
        int TOP_HEADER_ROW_INDEX = 4;
        // Create our BuyOptionObject
        StockSetData stockSetData = new StockSetData();
        // Get all the data as records
        List<List<String>> records = getCSV2(filePath);
        // Label the columns
        List<String> topHeader = records.get(TOP_HEADER_ROW_INDEX);
        List<String> nameHeader = records.get(TOP_HEADER_ROW_INDEX + 1);

        // loop horizontally through the elements in the top header
        for (int i = 0; i < topHeader.size(); i++) {
            // decide the type of column

            if (topHeader.get(i).equals("l")) {
                ArrayList<String> loaderArray = new ArrayList<>();
                for (int j = TOP_HEADER_ROW_INDEX + 2; j < records.size(); j++) {
                    loaderArray.add(records.get(j).get(i));
                }
                ColumnAndName columnAndName = new ColumnAndName(loaderArray ,nameHeader.get(i));
                stockSetData.getLabelValues().add(columnAndName);
            }
            if (topHeader.get(i).equals("s")) {
                ArrayList<String> loaderArray = new ArrayList<>();
                for (int j = TOP_HEADER_ROW_INDEX + 2; j < records.size(); j++) {
                    loaderArray.add(records.get(j).get(i));
                }
                ColumnAndName columnAndName = new ColumnAndName(loaderArray ,nameHeader.get(i));
                stockSetData.getSortingValues().add(columnAndName);
            }
            if (topHeader.get(i).equals("f")) {
                ArrayList<String> loaderArray = new ArrayList<>();
                for (int j = TOP_HEADER_ROW_INDEX + 2; j < records.size(); j++) {
                    loaderArray.add(records.get(j).get(i));
                }
                ColumnAndName columnAndName = new ColumnAndName(loaderArray ,nameHeader.get(i));
                stockSetData.getFitnessValues().add(columnAndName);
            }


        }

        return stockSetData;
    }

    // puts all the days into a Arraylist of DayData. For now though it can only hold about 50.
    public static ArrayList<DayData> getAllData(List<List<String>> records) {
        ArrayList<DayData> dayDataL = new ArrayList<>(50);
        for (int i = 1; i < 51; i++) {

            dayDataL.add(getStockByNumber(records, i));
            //System.out.println("Added " + i);
            //System.out.println(getUsedMemory());
        }

        return  dayDataL;
    }

    // this gets the raw data from the csv.
    // it's used to generate the variable called 'records'
    public static List<List<String>> getCSV2(String filepath) { //this method words the best
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            int lineCounter = 1; // this is to stop it from reading lines after the data is done
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 2 && lineCounter > 14) { // <----- the 14th line is chosen arbitrarily.
                    System.out.println("end of file");
                    break;
                } else {
                    records.add(Arrays.asList(values));
                    lineCounter++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    // for the records file at the number of stock in it it returns a dayData object.
    public static DayData getStockByNumber(List<List<String>> records, int stockNumber) {
        int colNum = (stockNumber * 4) + 2;
        int startColNum = ((stockNumber - 1) * 4) + 2;
        List<String> headRow = records.get(6);
        DayData dayData = new DayData(Float.parseFloat(headRow.get(startColNum + 1)), headRow.get(startColNum));

        for (int i = 8; i < records.size()/* -4*/; i++) {
            List<String> subRow = records.get(i);

            //System.out.println(i);


            if (noContainLetter(subRow.get(startColNum)) && noContainLetter(subRow.get(startColNum +1)) && noContainLetter(subRow.get(startColNum + 2)) && noContainLetter(subRow.get(startColNum + 3))) {
                dayData.markDL.add(Double.parseDouble(subRow.get(startColNum).replace('%', '0')));
                dayData.priceL.add(Double.parseDouble(subRow.get(startColNum + 1)));
                dayData.volumeL.add(Double.parseDouble(subRow.get(startColNum + 2)));
                dayData.tickL.add(Integer.parseInt(subRow.get(startColNum + 3)));
            } else {
                dayData.markDL.add(0.0);
                dayData.priceL.add(0.0);
                dayData.volumeL.add(0.0);
                dayData.tickL.add(0);
            }
        }

        return dayData;
    }

    public static boolean noContainLetter(String inString){
        if (inString == "") return true;
        if (inString == null) return true;
        for (char c: inString.toCharArray()) {
            if (Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMemory() {
        return (getMaxMemory() - getFreeMemory())/1000000;
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}
