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
        String fileName = readPath();

        Logger logger = new Logger("/logging/", fileName, 1.0f, "Genetic Algorithm");

        String pathToFile = System.getProperty("user.dir") + "/resources/" + fileName;

        Timer timer = new Timer();

        timer.start();

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        geneticAlgorithm.spawnIndividuals(pathToFile);

        geneticAlgorithm.createGeneration();

        do {
            if (geneticAlgorithm.generationCount < Configuration.MAX_GENERATIONS && !geneticAlgorithm.hasConverged) {
                geneticAlgorithm.createGeneration();
            }
        } while (
                geneticAlgorithm.generationCount < Configuration.MAX_GENERATIONS &&
                        geneticAlgorithm.noImprovementCount != Configuration.MAX_NO_IMPROVEMENT_COUNT
        );

        timer.stop();

        Individual bestIndividual = geneticAlgorithm.getBestIndividual();

        logger.log("Generation count", geneticAlgorithm.generationCount);
        logger.log("Length of path", bestIndividual.calculateFitness(geneticAlgorithm.cities));
        logger.log("Optimal path", bestIndividual.printSequence());
        logger.log("Time elapsed (ns)", timer.getDurationInNanos());

        logger.logTabular(
                fileName,
                geneticAlgorithm.generationCount,
                geneticAlgorithm.getBestIndividual().calculateFitness(geneticAlgorithm.cities),
                geneticAlgorithm.getBestIndividual().printSequence(),
                timer.getDurationInNanos()
        );
    }

}
