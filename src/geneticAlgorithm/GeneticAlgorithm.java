package geneticAlgorithm;

import java.io.*;
import java.util.*;

public class GeneticAlgorithm {
    private final Random random;
    private double previousFitness;
    public List<Individual> population;
    public int generationCount = 0;
    public int noImprovementCount = 0;
    private int cityCount = 0;
    public final List<City> cities;

    public Boolean hasConverged = generationCount > Configuration.MAX_GENERATIONS ||
            noImprovementCount > Configuration.MAX_NO_IMPROVEMENT_COUNT;

    public GeneticAlgorithm() {
        this.previousFitness = Double.MAX_VALUE;
        this.population = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Read the file and store the cities in a list
     * and create a population of individuals
     */
    public void spawnIndividuals(String pathToFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
        String line = reader.readLine();

        while (line != null) {
            String[] tokens = line.replaceAll("\\s+"," ").trim().split(" ");
            int xCoordinate = Integer.parseInt(tokens[1]);
            int yCoordinate = Integer.parseInt(tokens[2]);
            City city = new City(xCoordinate, yCoordinate);
            this.cities.add(city);
            this.cityCount++;
            line = reader.readLine();
        }

        reader.close();

        for (int populationIndex = 0; populationIndex < Configuration.POPULATION_COUNT; populationIndex++) {
            this.population.add(randomIndividual());
        }
    }

    /**
     * Generate an individual with a random sequence of cities
     */
    private Individual randomIndividual() {
        List<Integer> sequence = new ArrayList<>();

        /* Add the cities to the sequence */
        for (int cityNumber = 0; cityNumber < this.cityCount; cityNumber++) {
            sequence.add(cityNumber);
        }

        /* Shuffle the sequence */
        Collections.shuffle(sequence);

        return new Individual(sequence);
    }

    /**
     * Create a generation
     */
    public void createGeneration() {
        this.generationCount++;

        List<Individual> offspring = new ArrayList<>();

        for (int i = 0; i < Configuration.POPULATION_COUNT; i++) {
            // Get parents
            Individual father = getParent();
            Individual mother = getParent();

            while (mother == father) {
                father = getParent();
            }

            // Perform crossover
            Pair<Individual, Individual> offsprings = getOffSpring(mother, father);

            // Mutation
            Mutation mutation = new Mutation(this.cityCount);
            Pair<Individual, Individual> mutatedOffsprings = mutation.mutate(offsprings.first(), offsprings.second());

            offspring.add(mutatedOffsprings.first());
            offspring.add(mutatedOffsprings.second());
        }

        // Add all the offspring to our existing population
        this.population.addAll(offspring);

        // Order population by fitness value (ascending)
        this.population.sort(Comparator.comparingDouble(individual -> individual.calculateFitness(this.cities)));

        // Grab the best individuals
        Individual bestIndividual = population.get(0);

        bestIndividual.getSequence().add(bestIndividual.getSequence().get(0));

        double bestFitness = bestIndividual.calculateFitness(this.cities);

        if (previousFitness == bestFitness) {
            noImprovementCount++;
        } else {
            previousFitness = bestFitness;
            noImprovementCount = 0;
        }
    }

    /**
     * Get offsprings from the parents
     */
    private Pair<Individual, Individual> getOffSpring(Individual individualA, Individual individualB) {
        Individual offspringA = doCrossover(individualA, individualB);
        Individual offspringB = doCrossover(individualB, individualA);

        return new Pair<>(offspringA, offspringB);
    }

    /**
     * Perform crossover
     */
    private Individual doCrossover(Individual individualA, Individual individualB) {
        // Generate an integer between 1 and town size - 1
        int crossoverPosition = random.nextInt(this.cityCount - 1) + 1;

        List<Integer> offspringSequence = new ArrayList<>(individualA.getSequence().subList(0,
                crossoverPosition));

        // Add all the elements from individualB that are not in offspringSequence
        for (int i = 0; i < individualB.getSequence().size(); i++) {
            if (!offspringSequence.contains(individualB.getSequence().get(i))) {
                offspringSequence.add(individualB.getSequence().get(i));
            }
        }

        return new Individual(offspringSequence);
    }

    private Individual getParent() {
        return tournamentSelection();
    }

    private Individual tournamentSelection() {
        Individual candidate1 = population.get(random.nextInt(Configuration.POPULATION_COUNT));
        Individual candidate2 = population.get(random.nextInt(Configuration.POPULATION_COUNT));

        while (candidate1 == candidate2) {
            candidate2 = population.get(random.nextInt(Configuration.POPULATION_COUNT));
        }

        if (candidate1.calculateFitness(this.cities) > candidate2.calculateFitness(this.cities)) {
            return candidate1;
        } else {
            return candidate2;
        }
    }

    public Individual getBestIndividual() {
        // Go back to starting town
        return getParent();
    }
}
