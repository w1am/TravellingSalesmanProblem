package geneticAlgorithm;

import logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static String readPath() throws IOException {
        System.out.print("Enter the file name located in resources folder (eg. file1.txt): ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        return reader.readLine();
    }

    public static void main(String[] args) throws IOException {
        Logger logger = new Logger("/logging/", 1.0f, "Genetic Algorithm using simple distance calculation");

        String pathToFile = System.getProperty("user.dir") + "/resources/" + readPath();

        Timer timer = new Timer();

        timer.start();

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        geneticAlgorithm.spawnIndividuals(pathToFile);

        geneticAlgorithm.doGeneration();

        do {
            if (geneticAlgorithm.generationCount < Configuration.MAX_GENERATIONS && !geneticAlgorithm.hasConverged) {
                geneticAlgorithm.doGeneration();
            }
        } while (
            geneticAlgorithm.generationCount < Configuration.MAX_GENERATIONS &&
            geneticAlgorithm.noImprovementCount != Configuration.MAX_NO_IMPROVEMENT_COUNT
        );

        timer.stop();

        logger.log("Generation count", geneticAlgorithm.generationCount);
        logger.log("Best fitness", geneticAlgorithm.getBestIndividual().calculateFitness(geneticAlgorithm.cities));
        logger.log("Generations", geneticAlgorithm.fitnessOverTime);

        timer.renderTime();

        logger.logWithoutMessage("Time elapsed", timer.getDurationInSeconds());
    }

}