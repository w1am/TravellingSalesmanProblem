package geneticAlgorithm;

import java.io.*;
import java.util.*;

public class GeneticAlgorithm {
    private final Random random;
    public List<Individual> population;
    public List<Double> fitnessOverTime;
    public int generationCount = 0;
    public int noImprovementCount = 0;
    private int cityCount = 0;
    public final List<City> cities;
    public HashMap<Pair<Integer, Integer>, Float> pathSpeedLimits;

    public Boolean hasConverged = generationCount > Configuration.MAX_GENERATIONS ||
            noImprovementCount > Configuration.MAX_NO_IMPROVEMENT_COUNT;

    private float previousConvergenceArea = Float.MAX_VALUE;

    public GeneticAlgorithm() {
        this.fitnessOverTime = new ArrayList<>();
        this.population = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.random = new Random();
        this.pathSpeedLimits = new HashMap<>();
    }

    /**
     * Populate speed limits for each routes
     */
    public void populateSpeedLimits() {
        int localRandom = random.nextInt(17) + 1;

        for (int fromTown = 0; fromTown < this.cityCount; fromTown++) {
            for (int toTown = 0; toTown < this.cityCount; toTown++) {
                // If our from town is our to town, no need to calculate a path
                if (fromTown == toTown) continue;

                // Calculate the path distance as speed is distance dependent
                double pathDistance = this.cities
                    .get(fromTown)
                    .distanceTo(this.cities.get(toTown));

                // Add the speed for this directional path
                this.pathSpeedLimits.put(new Pair<>(fromTown, toTown), (float) (pathDistance / localRandom));
            }
        }
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
            this.population.add(generateIndividual());
        }
    }

    /**
     * Generate an individual with a random sequence of cities
     */
    private Individual generateIndividual() {
        List<Integer> sequence = new ArrayList<>();

        /* Add the cities to the sequence */
        for (int cityNumber = 0; cityNumber < this.cityCount; cityNumber++) {
            sequence.add(cityNumber);
        }

        /* Shuffle the sequence */
        Collections.shuffle(sequence);

        return new Individual(sequence, this.cities, this.pathSpeedLimits);
    }

    public void doGeneration() {
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

            // Mutate
            Pair<Individual, Individual> mutatedOffsprings = doMutate(offsprings.first(), offsprings.second());

            offspring.add(mutatedOffsprings.first());
            offspring.add(mutatedOffsprings.second());
        }

        // Add all the offspring to our existing population
        this.population.addAll(offspring);

        MultiObjective.updatePopulationFitness(this.population);

        // Take the best 'PopulationCount' worth of individuals
        List<Individual> newPopulation = new ArrayList<>();

        // Order by rank then order by crowding distance in descending order
        this.population.sort((Individual a, Individual b) -> {
            if (a.getRank() == b.getRank()) {
                return (int) (b.getCrowdingDistance() - a.getCrowdingDistance());
            } else {
                return a.getRank() - b.getRank();
            }
        });

        List<Individual> finalNewPopulation = newPopulation;
        this.population.forEach((Individual individual) -> {
            if (!finalNewPopulation.contains(individual)) {
                finalNewPopulation.add(individual);
            }
        });

        newPopulation = newPopulation.subList(0, Configuration.POPULATION_COUNT);

        this.population.clear();

        this.population.addAll(newPopulation);

        // Find the individual in the population with rank = 1 and order by the time fitness
        List<Individual> firstRank = this.population.stream()
                .filter(individual -> individual.getRank() == 1)
                .sorted(Comparator.comparingDouble(Individual::getTimeFitness)).toList();

        float currentArea = MultiObjective.calculateArea(firstRank);

        if (Math.abs(this.previousConvergenceArea - currentArea) < 0.1) {
            // No change
            this.noImprovementCount++;
        } else {
            // Changed
            this.noImprovementCount = 0;
            this.previousConvergenceArea = currentArea;
        }
    }

    private Pair<Individual, Individual> doMutate(Individual offspringA, Individual offspringB) {
        Individual newOffspringA = new Individual(
                offspringA.getSequence(),
                this.cities,
                this.pathSpeedLimits
        );
        Individual newOffspringB = new Individual(
                offspringB.getSequence(),
                this.cities,
                this.pathSpeedLimits
        );

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

        return new Individual(sequence, this.cities, this.pathSpeedLimits);
    }

    private Individual rotateMutate(Individual individual) {
        Pair<Integer, Integer> uniqueTowns = getUniqueTowns();

        Integer townA = uniqueTowns.first();
        Integer townB = uniqueTowns.second();

        Integer firstIndex = townA < townB ? townA : townB;
        Integer secondIndex = townA < townB ? townB : townA;

        List<Integer> newSequence = individual.getSequence().subList(0, firstIndex);
        List<Integer> middle = individual
                .getSequence().stream().skip(firstIndex)
                .limit(secondIndex - firstIndex)
                .toList();
        List<Integer> tail = individual.getSequence().stream().skip(secondIndex).toList();

        newSequence.addAll(middle);
        newSequence.addAll(tail);

        return new Individual(newSequence, this.cities, this.pathSpeedLimits);
    }

    private Pair<Integer, Integer> getUniqueTowns() {
        // Generate two unique towns
        int townA = random.nextInt(this.cityCount);
        int townB = random.nextInt(this.cityCount);

        while (townB == townA) {
            townB = random.nextInt(this.cityCount);
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
        int crossoverPosition = random.nextInt(this.cityCount - 1) + 1;

        List<Integer> offspringSequence = new ArrayList<>(
                individualA.getSequence().subList(0, crossoverPosition)
        );

        // Add all the elements from individualB that are not in offspringSequence
        for (int i = 0; i < individualB.getSequence().size(); i++) {
            if (!offspringSequence.contains(individualB.getSequence().get(i))) {
                offspringSequence.add(individualB.getSequence().get(i));
            }
        }

        return new Individual(offspringSequence, this.cities, this.pathSpeedLimits);
    }

    private Individual getParent() {
        Pair<Individual, Individual> candidates = this.getCandidateParents();
        return this.tournamentSelection(candidates.first(), candidates.second());
    }

    public Pair<Individual, Individual> getCandidateParents() {
        Individual candidateA = this.population.get(random.nextInt(this.population.size()));
        Individual candidateB = this.population.get(random.nextInt(this.population.size()));

        while (candidateA == candidateB) {
            candidateB = this.population.get(random.nextInt(this.population.size()));
        }

        return new Pair<>(candidateA, candidateB);
    }

    private Individual tournamentSelection(Individual candidateA, Individual candidateB) {
        if (candidateA.getRank() < candidateB.getRank()) {
            return candidateA;
        } else if (candidateA.getRank() == candidateB.getRank()) {
            return candidateA.getCrowdingDistance() > candidateB.getCrowdingDistance()
                    ? candidateA
                    : candidateB;
        } else {
            return candidateB;
        }
    }

    public Individual getBestIndividual() {
        // We no longer have a 'best' individual, so we are going to show a random one from the first front.
        List<Individual> firstRank = this.population
                .stream()
                .filter(individual -> individual.getRank() == 1)
                .toList();

        return firstRank.get(random.nextInt(firstRank.size()));
    }
}
