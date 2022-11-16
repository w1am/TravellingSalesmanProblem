package geneticAlgorithm;

public class Configuration {
    // A limit to stop the program if it doesn't converge
    public static int MAX_GENERATIONS = 10000;

    // Chance of mutation
    public static double MUTATION_CHANCE = 0.05;

    // Maximum number of individuals in the population
    public static int POPULATION_COUNT = 200;

    // Maximum number of generations without improvement
    public static int MAX_NO_IMPROVEMENT_COUNT = 20;
}
