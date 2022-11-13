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
//        String fileName = readPath();
        String fileName = "file2.txt";

        Logger logger = new Logger("/logging/", fileName, 1.1f, "Genetic Algorithm using simple distance calculation but with different configuration");

        String pathToFile = System.getProperty("user.dir") + "/resources/" + fileName;

        Timer timer = new Timer();

        timer.start();

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        geneticAlgorithm.spawnIndividuals(pathToFile);

        geneticAlgorithm.populateSpeedLimits();

        do {
            if (!geneticAlgorithm.hasConverged) {
                geneticAlgorithm.doGeneration();

                System.out.println("Best fitness: " + geneticAlgorithm.getBestIndividual().getDistanceFitness());
            }
        } while (geneticAlgorithm.generationCount != Configuration.MAX_GENERATIONS && geneticAlgorithm.noImprovementCount != Configuration.MAX_NO_IMPROVEMENT_COUNT);

        timer.stop();

        logger.log("Generation count", geneticAlgorithm.generationCount);
        logger.log("Best fitness", geneticAlgorithm.getBestIndividual().getDistanceFitness());
        logger.logWithoutMessage("Fitness over time", geneticAlgorithm.fitnessOverTime);

        timer.renderTime();

        logger.logWithoutMessage("Time elapsed", timer.getDurationInSeconds());
    }

}
