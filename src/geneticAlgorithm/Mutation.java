package geneticAlgorithm;

import java.util.List;
import java.util.Random;

public class Mutation {
    private final int cityCount;

    private final Random random;

    public Mutation(int cityCount) {
        this.cityCount = cityCount;
        this.random = new Random();
    }

    /**
     * Generate a pair of unique city eg. (1, 4)
     */
    private Pair<Integer, Integer> getRandomCityPair() {
        int cityA = random.nextInt(cityCount);
        int cityB = random.nextInt(cityCount);

        /* Make sure they are unique */
        while (cityB == cityA) {
            cityB = random.nextInt(cityCount);
        }

        return new Pair<>(cityA, cityB);
    }


    public Pair<Individual, Individual> mutate(Individual offspringA, Individual offspringB) {
        Individual newOffspringA = new Individual(offspringA.getSequence());
        Individual newOffspringB = new Individual(offspringB.getSequence());

        if (random.nextDouble() < Configuration.MUTATION_CHANCE) {
            newOffspringA = this.doMutate(offspringA);
        }

        if (random.nextDouble()< Configuration.MUTATION_CHANCE) {
            newOffspringB = this.doMutate(offspringB);
        }

        return new Pair<>(newOffspringA, newOffspringB);
    }

    public Individual doMutate(Individual offspring) {
        // 50% chance of choosing the mutation method
        if (random.nextDouble() > 0.5) {
            // Do swap mutate
            return swapMutate(offspring);
        } else {
            // Do rotate mutate
            return rotateMutate(offspring);
        }
    }

    public Individual swapMutate(Individual individual) {
        List<Integer> sequence = individual.getSequence();

        Pair<Integer, Integer> uniqueTowns = this.getRandomCityPair();

        // Swap the towns in the sequence
        int temp = sequence.get(uniqueTowns.first());
        sequence.set(uniqueTowns.first(), sequence.get(uniqueTowns.second()));
        sequence.set(uniqueTowns.second(), temp);

        return new Individual(sequence);
    }

    public Individual rotateMutate(Individual individual) {
        Pair<Integer, Integer> uniqueTowns = this.getRandomCityPair();

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
}
