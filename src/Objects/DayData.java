package Objects;

import Calculation.TimeCalculation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayData {

    // primary indicators
    public float stockFloat;
    public String stockName;
    public ArrayList<Double> markDL;
    public ArrayList<Double> priceL;
    public ArrayList<Double> volumeL;
    public ArrayList<Integer> tickL;
    // secondaryIndicators
    public ArrayList<Boolean> sortIndicator;

    public ArrayList<Integer> volumeSeconds;
    public ArrayList<Integer> tickSeconds;
    public ArrayList<Double> PCI;
    public ArrayList<Integer> volumeMinute;
    public double firstHourLow;


    public ArrayList<Integer> pointHighArray;
    public ArrayList<String> tradeStrategy4To2;
    public ArrayList<String> tradeStrategy2To1;


    // trigger value variables;
    public double triggerPCI;
    public int triggerVolumeSecond;
    public int triggerTickSecond;
    public int triggerVolumeMinute;
    public int triggerTime;
    public int triggerStrategy4To2;
    public int triggerStrategy2To1;
    public String triggerTimeAsString;

    public DayData(float stockFloat, String stockName) {
        this.stockFloat = stockFloat;
        this.stockName = stockName;
        markDL = new ArrayList<>();
        priceL = new ArrayList<>();
        volumeL = new ArrayList<>();
        tickL = new ArrayList<>();
        tradeStrategy4To2 = new ArrayList<>();
        tradeStrategy2To1 = new ArrayList<>();
        sortIndicator = new ArrayList<>();
        this.pointHighArray = new ArrayList<>();
        firstHourLow = 9999;

        // trigger value variables initiate
        triggerPCI = -99999.0;
        triggerVolumeSecond = -1;
        triggerTickSecond = -1;
        triggerVolumeMinute = -1;
        triggerTime = -1;
        triggerStrategy4To2 = -1;
        triggerStrategy2To1 = -1;
        triggerTimeAsString = "";


    }

    public void calculateTriggerValues(int trigger, List<String> headers) {

        triggerTime = trigger;
        triggerTimeAsString = TimeCalculation.ConvertTimeIntToString(trigger);

        for (String head: headers) {
            if (head.equals("volumeSecond")) {
                if (this.volumeSeconds.size() == 0) {
                    //System.out.println("Calculating VolumeSeconds");
                    calculateVolumeSeconds();
                }
                triggerVolumeSecond = this.volumeSeconds.get(trigger);
            }
            if (head.equals("PCI")) {
                if (this.PCI.size() == 0) {
                    //System.out.println("Calculating PCI");
                    calculatePCI();
                }
                triggerPCI = this.PCI.get(trigger);
            }
            if (head.equals("tickSecond")) {
                if (this.tickSeconds.size() == 0) {
                    calculateTickSeconds();
                }
                triggerTickSecond = this.tickSeconds.get(trigger);
            }
            if (head.equals("volumeMinute")) {
                if (this.volumeMinute.size() == 0) {
                    calculateVolumeMinute();
                }
                triggerVolumeMinute = this.volumeMinute.get(trigger);
            }
            if (head.equals("4% - 2%")) {
                this.triggerStrategy4To2 = calculateWinStrategyByTypeAndStart(0.04, -0.02, trigger);
            }
            if (head.equals("2% - 1%")) {
                this.triggerStrategy2To1 = calculateWinStrategyByTypeAndStart(0.02, -0.01, trigger);
            }
            if (head.equals("firstHourLow") && firstHourLow == 9999) {
                calculateFirstHourLow();
            }

        }


    }

    public double calculateTriggerPCI(int trigger) {
        // get price before trigger
        double beforeTrigger = this.priceL.get(trigger);
        // get price at trigger
        double atTrigger = this.priceL.get(trigger - 1);

        return (atTrigger - beforeTrigger)/ beforeTrigger;
    }

    public void initiateSecondaryIndicators(List<SimpleParameter> simpleParameterList) {
        this.volumeSeconds = new ArrayList<>();
        this.tickSeconds = new ArrayList<>();
        this.PCI = new ArrayList<>();
        this.volumeMinute = new ArrayList<>();


        for (SimpleParameter parameter: simpleParameterList) {
            if (parameter.headerName.equals("volumeSecond")) {
                calculateVolumeSeconds();
            }
            if (parameter.headerName.equals("tickSeconds")) {
                calculateTickSeconds();
            }
            if (parameter.headerName.equals("PCI")) {
                calculatePCI();
            }
            if (parameter.headerName.equals("firstHourLow")) {
                calculateFirstHourLow();
            }
            if (parameter.headerName.equals("volumeMinute")) {
                calculateVolumeMinute();
            }
        }




        calculateSortIndicator(); // this has to run after PCI because it uses it.

        //this.tradeStrategy4To2 = calculateWinStrategy(0.04, -0.02);
        //this.tradeStrategy2To1 = calculateWinStrategy(0.02, -0.01);
        //calculatePointHighArray();
    }

    public void calculateSortIndicator(){ //initiate everything to zero
        for (int i = 0; i < this.priceL.size(); i++) {
            this.sortIndicator.add(false);
        }
    }

    public void emptySortIndicator(){
        for (int i = 0; i < this.sortIndicator.size(); i++) {
            sortIndicator.set(i, false);
        }
    }

    public int calculateWinStrategyByTypeAndStart(double winPercentage, double lossPercentage, int startingLine) {
        double currentPrice = priceL.get(startingLine);
        int success = 0;
        for (int i = startingLine; i > 0; i--) {
            double pciAtThisLine = calculatePCIBetween2Points(priceL.get(i), currentPrice);
            if (pciAtThisLine >= winPercentage) {
                success = 1;
                break;
            }
            if (pciAtThisLine <= lossPercentage){
                break;
            }
        }
        return success;
    }

    public ArrayList<String> calculateWinStrategy(double winPercentage, double lossPercentage) {
        ArrayList<String> strategyL = new ArrayList<>();
        for (int i = 0; i < this.priceL.size(); i++) {
            double iPrice = priceL.get(i);
            for (int j = i; j >=0; j--) {
                double jPrice = priceL.get(j);
                if (calculatePCIBetween2Points(jPrice, iPrice) > winPercentage) {
                    strategyL.add("1");
                    break;
                }
                if (calculatePCIBetween2Points(jPrice, iPrice) < lossPercentage) {
                    strategyL.add("0");
                    break;
                }
                if (j == 0) {
                    strategyL.add("No Movement");
                    break;
                }
            }
        }
        System.out.println("Finished Calculating Win Strategy for stock");
        System.out.println(System.currentTimeMillis());
        return strategyL;
    }

    public void calculatePointHighArray() {
        //System.out.println("calculating point high array...");
        for (int i = 0; i < 150; i++) {
            this.pointHighArray.add(0/*null*/);
        }
        for (int i = 150; i < this.priceL.size() - 150; i++) {
            // get max value in array before
            ArrayList<Double> beforeArray = new ArrayList<>(priceL.subList((i-150), (i - 1)));
            double beforeMax = Collections.max(beforeArray);
            // get max value in array after wards
            ArrayList<Double> afterArray = new ArrayList<>(priceL.subList((i+1), (i+150)));
            double afterMax = Collections.max(afterArray);
            // if it fits, add it.
            double currentPrice = priceL.get(i);
            if ((currentPrice > beforeMax) && (currentPrice > afterMax)) {
                pointHighArray.add(1);
                System.out.println("found a point");
            } else {
                pointHighArray.add(0);
            }
        }
        for (int i = 0; i < 150; i++) {
            this.pointHighArray.add(0/*null*/);
        }
        //System.out.println("finished calculating.");
    }

    public void calculateVolumeMinute() {
        for (int i = 0; i < this.volumeL.size() - 59; i++) {
            double currentVolume = volumeL.get(i);
            double volume59SecondsAgo = volumeL.get(i + 59);
            volumeMinute.add((int)(currentVolume - volume59SecondsAgo));
        }
        for (int i = 0; i < 59; i++) { // add the dead values to the end.
            this.volumeMinute.add(0);
        }
    }

    public void calculateVolumeSeconds() {
        for (int i = 0; i < this.volumeL.size() - 1; i++) {
            double currentSecondVolume = volumeL.get(i);
            double previousSecondVolume = volumeL.get(i + 1);
            volumeSeconds.add((int) (currentSecondVolume - previousSecondVolume));
        }
        volumeSeconds.add(0);
    }

    public void calculateTickSeconds() {
        for (int i = 0; i < this.tickL.size() - 1; i++) {
            double currentSecondVolume = tickL.get(i);
            double previousSecondVolume = tickL.get(i + 1);
            tickSeconds.add((int) (currentSecondVolume - previousSecondVolume));
        }
        tickSeconds.add(0);
    }

    public void calculatePCI() {
        for (int i = 0; i < this.priceL.size() - 1; i++) {
            double currentSecondVolume = priceL.get(i);
            double previousSecondVolume = priceL.get(i + 1);
            PCI.add(((currentSecondVolume - previousSecondVolume)/ previousSecondVolume));
        }
        PCI.add(0.0);
    }

    public void calculateFirstHourLow() {
        //System.out.println(this.priceL.size());
        for (int i = TimeCalculation.lineOfTime930(); i > TimeCalculation.lineOfTime1030(); i--) {
            if (this.priceL.get(i) < this.firstHourLow) firstHourLow = this.priceL.get(i);
        }
    }

    public ArrayList<Integer> getVolumeSeconds() {
        return volumeSeconds;
    }

    public ArrayList<Integer> getTickSeconds() {
        return tickSeconds;
    }

    public ArrayList<Double> getPCI() {
        return PCI;
    }

    public void addMarkDL(double inDouble) {
        this.markDL.add(inDouble);
    }

    public void addPriceL(double inDouble) {
        this.priceL.add(inDouble);
    }

    public void addVolumeL(double inDouble) {
        this.volumeL.add(inDouble);
    }

    public void addTickL(int inInt) {
        tickL.add(inInt);
    }

    public float getStockFloat() {
        return stockFloat;
    }

    public String getStockName() {
        return stockName;
    }

    public ArrayList<Double> getMarkDL() {
        return markDL;
    }

    public ArrayList<Double> getPriceL() {
        return priceL;
    }

    public ArrayList<Double> getVolumeL() {
        return volumeL;
    }

    public ArrayList<Integer> getTickL() {
        return tickL;
    }

    public double calculatePCIBetween2Points(double newPrice, double oldPrice) {
        return ((newPrice - oldPrice)/ oldPrice);
    }


}
