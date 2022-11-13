package geneticAlgorithm;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private final Random random;
    private final List<Double> cumulativeProportions;
    private double previousFitness;
    public List<Individual> population;
    public List<Double> fitnessOverTime;
    public int generationCount = 0;
    public int noImprovementCount = 0;
    private int citiesCount = 0;
    public final List<City> cities;

    public Boolean hasConverged = generationCount > Configuration.MAX_GENERATIONS ||
            noImprovementCount > Configuration.MAX_NO_IMPROVEMENT_COUNT;

    public GeneticAlgorithm() {
        this.previousFitness = Double.MAX_VALUE;
        this.cumulativeProportions = new ArrayList<>();
        this.fitnessOverTime = new ArrayList<>();
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
            String[] tokens = line.split(" ");
            int xCoordinate = Integer.parseInt(tokens[1]);
            int yCoordinate = Integer.parseInt(tokens[2]);
            City city = new City(xCoordinate, yCoordinate);
            this.cities.add(city);
            this.citiesCount++;
            line = reader.readLine();
        }

        reader.close();

        for (int populationIndex = 0; populationIndex < Configuration.POPULATION_COUNT; populationIndex++) {
            this.population.add(generateIndividual());
        }
    }

    /**
     * Generate an individual with a random sequence of cities
     */
    private Individual generateIndividual() {
        List<Integer> sequence = new ArrayList<>();

        /* Add the cities to the sequence */
        for (int cityNumber = 0; cityNumber < this.citiesCount; cityNumber++) {
            sequence.add(cityNumber);
        }

        /* Shuffle the sequence */
        Collections.shuffle(sequence);

        return new Individual(sequence);
    }

    public void doGeneration() {
        this.generationCount++;

        updateCumulativeProportions();

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

            // Mutate
            Pair<Individual, Individual> mutatedOffsprings = doMutate(offsprings.first(), offsprings.second());

            offspring.add(mutatedOffsprings.first());
            offspring.add(mutatedOffsprings.second());
        }

        // Add all the offspring to our existing population
        this.population.addAll(offspring);

        // Order the population individual by their fitness values
        population.sort((a, b) -> {
            try {
                return Double.compare(a.calculateFitness(this.cities), b.calculateFitness(this.cities));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });

        Individual bestIndividual = population.get(0);

        bestIndividual.getSequence().add(bestIndividual.getSequence().get(0));

        double bestFitness = bestIndividual.calculateFitness(this.cities);

        if (previousFitness == bestFitness) {
            noImprovementCount++;
        } else {
            previousFitness = bestFitness;
            noImprovementCount = 0;
        }

        fitnessOverTime.add(bestFitness);
    }

    private Pair<Individual, Individual> doMutate(Individual offspringA, Individual offspringB) {
        Individual newOffspringA = new Individual(offspringA.getSequence());
        Individual newOffspringB = new Individual(offspringB.getSequence());

        if (random.nextDouble() < Configuration.MUTATION_CHANCE) {
            newOffspringA = mutate(offspringA);
        }

        if (random.nextDouble()< Configuration.MUTATION_CHANCE) {
            newOffspringB = mutate(offspringB);
        }

        return new Pair<>(newOffspringA, newOffspringB);
    }

    private Individual mutate(Individual offspring) {
        if (random.nextDouble() > 0.5) {
            // Do swap mutate
            return swapMutate(offspring);
        } else {
            // Do rotate mutate
            return rotateMutate(offspring);
        }
    }

    private Individual swapMutate(Individual individual) {
        List<Integer> sequence = individual.getSequence();

        Pair<Integer, Integer> uniqueTowns = getUniqueTowns();

        // Swap the towns in the sequence
        int temp = sequence.get(uniqueTowns.first());
        sequence.set(uniqueTowns.first(), sequence.get(uniqueTowns.second()));
        sequence.set(uniqueTowns.second(), temp);

        return new Individual(sequence);
    }

    private Individual rotateMutate(Individual individual) {
        Pair<Integer, Integer> uniqueTowns = getUniqueTowns();

        Integer townA = uniqueTowns.first();
        Integer townB = uniqueTowns.second();

        Integer firstIndex = townA < townB ? townA : townB;
        Integer secondIndex = townA < townB ? townB : townA;

        List<Integer> newSequence = individual.getSequence().subList(0, firstIndex);
        List<Integer> middle = individual.getSequence().stream().skip(firstIndex).limit(secondIndex - firstIndex).toList();
        List<Integer> tail = individual.getSequence().stream().skip(secondIndex).toList();

        newSequence.addAll(middle);
        newSequence.addAll(tail);

        return new Individual(newSequence);
    }

    private Pair<Integer, Integer> getUniqueTowns() {
        // Generate two unique towns
        int townA = random.nextInt(this.citiesCount);
        int townB = random.nextInt(this.citiesCount);

        while (townB == townA) {
            townB = random.nextInt(this.citiesCount);
        }

        return new Pair<>(townA, townB);
    }

    private Pair<Individual, Individual> getOffSpring(Individual individualA, Individual individualB) {
        Individual offspringA = doCrossover(individualA, individualB);
        Individual offspringB = doCrossover(individualB, individualA);

        return new Pair<>(offspringA, offspringB);
    }

    private Individual doCrossover(Individual individualA, Individual individualB) {
        // Generate an integer between 1 and town size - 1
        int crossoverPosition = random.nextInt(this.citiesCount - 1) + 1;

        List<Integer> offspringSequence = new ArrayList<>(individualA.getSequence().subList(0, crossoverPosition));

        // Add all the elements from individualB that are not in offspringSequence
        for (int i = 0; i < individualB.getSequence().size(); i++) {
            if (!offspringSequence.contains(individualB.getSequence().get(i))) {
                offspringSequence.add(individualB.getSequence().get(i));
            }
        }

        return new Individual(offspringSequence);
    }

    private Individual getParent() {
        Individual selectedIndividual;

        if (random.nextDouble() > 0.5) {
            // Tournament
            selectedIndividual = tournamentSelection();
        } else {
            // Biased random
            selectedIndividual = biasedRandomSelection();
        }

        selectedIndividual.getSequence().add(selectedIndividual.getSequence().get(0));

        return selectedIndividual;
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

    private Individual biasedRandomSelection() {
        double selectedValue = random.nextDouble();

        for (int i = 0; i < cumulativeProportions.size(); i++) {
            double value = cumulativeProportions.get(i);

            if (value >= selectedValue) {
                return population.get(i);
            }
        }

        return population.get(Configuration.POPULATION_COUNT - 1);
    }

    public Individual getBestIndividual() {
        // Go back to starting town
        return getParent();
    }

    public void updateCumulativeProportions() {
        // Sum of all current individual fitness
        double sum = 0.0;

        for (Individual i : population) {
            sum = i.calculateFitness(this.cities);
        }

        List<Double> proportions = new ArrayList<>();

        for (Individual i : population) {
            proportions.add(sum / i.calculateFitness(this.cities));
        }

        double proportionSum = 0.0;

        for (Double i : proportions) {
            proportionSum += i;
        }

        List<Double> normalizedProportions = new ArrayList<>();

        for (Double i : proportions) {
            normalizedProportions.add(i / proportionSum);
        }

        double cumulativeTotal = 0.0;

        for (Double i : normalizedProportions) {
            cumulativeTotal += i;
            cumulativeProportions.add(cumulativeTotal);
        }
    }
}
