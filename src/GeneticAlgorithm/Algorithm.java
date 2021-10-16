package GeneticAlgorithm;

import java.lang.reflect.Array;
import java.util.*;

public class Algorithm {

    public Algorithm() {
    }

    public static ArrayList<ParameterSet> generateArrayListParameterSet(int initialPopSize, StockSetData stockSetData, String strategy, int minResults) {
        System.out.println("Generating random initial set...");
        ArrayList<ParameterSet> returnList = new ArrayList<>();
        for (int i = 0; i < initialPopSize; i++) {
            ParameterSet paramSet = new ParameterSet(stockSetData);
            paramSet.generateRandomParametersVersion2(stockSetData);
            paramSet.calculateStrategyFitness(stockSetData);
            if (paramSet.getStrategyEffectiveness(strategy) > 0.00001 && paramSet.getNumberOfResults() > minResults && paramSet.getStrategyObject(strategy).getNoMovementRatio() < 0.1){
                returnList.add(paramSet);
                System.out.println(paramSet.getStrategyEffectiveness(strategy) + " @ " + i);
            } else {
                //System.out.println("FAIL " + paramSet.getStrategyEffectiveness(strategy) + " @ " + i + " " +paramSet.getNumberOfResults());
                i--;
            }
        }

        // cleaning the data
        Collections.sort(returnList, new Comparator<ParameterSet>() {
            @Override
            public int compare(ParameterSet o2, ParameterSet o1) {
                return Double.compare(o1.getStrategyEffectiveness(strategy), o2.getStrategyEffectiveness(strategy));
            }
        });
        System.out.println("Finished generating random initial set\n");
        returnList.removeIf(x -> Double.isNaN(x.getStrategyEffectiveness(strategy)));
        return returnList;
    }

    public static double getHighestWinRateFromArrayListParameterSet(ArrayList<ParameterSet> inPSetList, String strategy) {
        double highestValue = 0;
        for (ParameterSet parameterSet: inPSetList) {
            if (parameterSet.getStrategyEffectiveness(strategy) > highestValue) {
                highestValue = parameterSet.getStrategyEffectiveness(strategy);
            }
        }
        return highestValue;
    }

    public static ArrayList<ParameterSet> getTopXParameterSetsByStrategy(ArrayList<ParameterSet> inPSetList, String strategy, int numToReturn) {
        // cleaning the data
        // sorts it by highest to lowest
        Collections.sort(inPSetList, new Comparator<ParameterSet>() {
            @Override
            public int compare(ParameterSet o2, ParameterSet o1) {
                return Double.compare(o1.getStrategyEffectiveness(strategy), o2.getStrategyEffectiveness(strategy));
            }
        });

        return new ArrayList<ParameterSet>(inPSetList.subList(0, numToReturn));
    }



    public static void simpleMutatingAlgorithm(StockSetData stockSetData, String strategy, int initialPopSize, int skimPopSize, int allTimeHighPopSize, int numberOfGenerations, int minResults) {
        // ---------------------------- SETUP -------------------------
        // initiate objects and variables
            // The best parameters
        ArrayList<ParameterSet> allTimeHighs = new ArrayList<>();
        // Create an initial population to work with
        ArrayList<ParameterSet> population = Algorithm.generateArrayListParameterSet(initialPopSize, stockSetData, strategy, minResults);
        // Skim off the top values
        ArrayList<ParameterSet> subPopulation = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize);
        // because this is the first loop, we put an arbitrary amount (5) in our allTimeHighs from the subParamList
        for (int i = 0; i < allTimeHighPopSize; i++) {
            // we have to create a new object because java is homosexual
            ParameterSet parameterSet = new ParameterSet(subPopulation.get(i));
            allTimeHighs.add(parameterSet);
        }

        for (int i = 0; i < numberOfGenerations; i++) {
            // ---------------------------- MAIN LOOP -----------------------------
            // Use allTimeHighs and SubPopulation to fill the population again
            // reevaluate the allTimeHighs
            allTimeHighs = reevaluateAllTimeHighsFromSubPopulation(allTimeHighs, subPopulation, strategy);

            population.clear();
            population.addAll(allTimeHighs);

            // add all the mutated allTimeHighs to the population
            for (ParameterSet parameterSet: allTimeHighs) {
                ParameterSet newParamSet = new ParameterSet(parameterSet);
                newParamSet.mutateRandomParameter(stockSetData);
                population.add(newParamSet);
            }

            // keep mutating the rest of the results while you can
            while(population.size() <= initialPopSize) {
                for(ParameterSet parameterSet: subPopulation) {
                    ParameterSet newParameterSet = new ParameterSet(parameterSet);
                    newParameterSet.mutateRandomParameter(stockSetData);
                    population.add(newParameterSet);
                    if (population.size() > initialPopSize) break;
                }
            }

            subPopulation.clear();
            subPopulation = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize);
            // -----------------------------END MAIN LOOP-----------------------------
            System.out.println("Finished Running Generation " + i + " | Win rate -> " + allTimeHighs.get(0).getStrategyEffectiveness(strategy));
        }

        // Print most effective strategies.
        for (ParameterSet parameterSet: allTimeHighs) {
            System.out.println(parameterSet.getStrategyEffectiveness(strategy));
        }

        System.out.println("ASDF");
    }

    public static void simpleCrossoverAlgorithm(StockSetData stockSetData, String strategy, int initialPopSize, int skimPopSize, int numberOfGenerations, int minResults) {
        // Top Strategy
        ParameterSet topParameterSet = new ParameterSet(stockSetData);
        // Create an initial population to work with
        ArrayList<ParameterSet> population = Algorithm.generateArrayListParameterSet(initialPopSize, stockSetData, strategy, minResults);
        // Set the top parameterSet
        topParameterSet = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize).get(0);

        //----------------- MAIN LOOP ----------------
        for (int i = 0; i < numberOfGenerations; i++) {
            System.out.println("---->I RAN!<----");
            // get top two ParameterSets from the population
            ParameterSet pSet1 = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize).get(0);
            ParameterSet pSet2 = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize).get(1);
            ParameterSet pSet3 = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize).get(2);
            ParameterSet pSet4 = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize).get(3);
            ArrayList<ParameterSet> pSetArrayList = new ArrayList<>();
            pSetArrayList.add(pSet1);
            pSetArrayList.add(pSet2);
            pSetArrayList.add(pSet3);
            pSetArrayList.add(pSet4);
            population.clear();
            // fill the population with random crossovers from our best pairs
            for (int j = 0; j < initialPopSize; j++) {
                //System.out.println("---->I RAN2!<----");
                ParameterSet crossSet1 = randomParameterCrossoverFromRandomPoints(stockSetData, pSetArrayList, minResults);
                crossSet1.calculateStrategyFitness(stockSetData);
                //System.out.println(crossSet1.getNumberOfResults());
                if (crossSet1.getNumberOfResults() < minResults) {
                    System.out.println("Results were not good enough");
                    j--;
                } else {
                    population.add(crossSet1);
                }


            }
            // contest the topParameterSet
            ParameterSet topOfPopulation = Algorithm.getTopXParameterSetsByStrategy(population, strategy, skimPopSize).get(0);
            if (topOfPopulation.getStrategyEffectiveness(strategy) > topParameterSet.getStrategyEffectiveness(strategy)) {
                topParameterSet = topOfPopulation;
            }
            System.out.println("Finished Running Generation " + i + " | Win rate -> " + topParameterSet.getStrategyEffectiveness(strategy));
        }

        System.out.println("asdf");
    }

    public static ParameterSet randomParameterCrossoverFromRandomPoints(StockSetData stockSetData, ArrayList<ParameterSet> pSetArray, int minResults){
        int randomNum1 = ParameterSet.getRandomIntBetweenTwoInts(pSetArray.size() - 1, 0);
        int randomNum2 = ParameterSet.getRandomIntBetweenTwoInts(pSetArray.size() - 1, 0);

        return randomParameterCrossover(stockSetData, pSetArray.get(randomNum1), pSetArray.get(randomNum2), minResults);
    }

    public static ParameterSet randomParameterCrossover(StockSetData stockSetData,ParameterSet pSet1, ParameterSet pSet2, int minResults) {

        ParameterSet returnParamSet = new ParameterSet(stockSetData);

        int genomeSize = pSet1.getSortingValues().size();
        int randomCutType = ParameterSet.getRandomIntBetweenTwoInts(1, 1);
        int randomCutPlace = ParameterSet.getRandomIntBetweenTwoInts(genomeSize, 1);
        int outOf10 = ParameterSet.getRandomIntBetweenTwoInts(10, 1);
        // extract array of parameters for both parameterSets
        ArrayList<ParameterSet.Parameter> pSet1ParamArray = new ParameterSet(pSet1).getSortingValues();
        ArrayList<ParameterSet.Parameter> pSet2ParamArray = new ParameterSet(pSet2).getSortingValues();

        //System.out.println("cutting genome at point " + randomCutPlace);

        // this is a normal kind of cut
        if (randomCutType == 1) {
            for (int i = 0; i < randomCutPlace; i++) {
                returnParamSet.setSpecificParameterWithParameter(pSet1ParamArray.get(i));
            }
            for (int i = randomCutPlace; i < genomeSize; i++) {
                returnParamSet.setSpecificParameterWithParameter(pSet2ParamArray.get(i));
            }
        }
        if (randomCutType == 2) {
            // TODO, calculate that other version for now the cut type max is 1. set to 2 once finished
            System.out.println("You shouldn't see this");
        }
        if (outOf10 == 5) {
            System.out.println("Value has been mutated");
            returnParamSet.mutateRandomParameter(stockSetData);
        }
        //System.out.println("---->I RAN!<----");
        return returnParamSet;
    }

    public static ArrayList<ParameterSet> reevaluateAllTimeHighsFromSubPopulation(ArrayList<ParameterSet> allTimeHighs, ArrayList<ParameterSet> subPopulation, String strategy) {

        int allTimesHighSize = allTimeHighs.size();
        int allTimesHighSizeOriginal = allTimesHighSize;
        // copyVersion
        ArrayList<ParameterSet> comboArrayList = new ArrayList<>();
        comboArrayList.addAll(allTimeHighs);
        comboArrayList.addAll(subPopulation);

        Collections.sort(comboArrayList, new Comparator<ParameterSet>() {
            @Override
            public int compare(ParameterSet o2, ParameterSet o1) {
                return Double.compare(o1.getStrategyEffectiveness(strategy), o2.getStrategyEffectiveness(strategy));
            }
        });

        allTimeHighs.clear();

        for (int i = 0; i < allTimesHighSize; i++) {
            ParameterSet pSet1 = comboArrayList.get(i);
            ParameterSet pSet2 = comboArrayList.get(i + 1);
            if (!comboArrayList.get(i).equals(comboArrayList.get(i + 1))) {
                allTimeHighs.add(comboArrayList.get(i));
            } else {
                allTimesHighSize++;
            }
        }

        for (int i = 0; i < allTimesHighSizeOriginal - 1; i++) {
            if (allTimeHighs.get(i).equals(allTimeHighs.get(i + 1))) {
                System.out.println("asdf");
            }
        }



        return allTimeHighs;

    }

}
