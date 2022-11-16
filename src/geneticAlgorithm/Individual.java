package geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * This record represents a single individual in the population.
 * @param sequence
 */
public record Individual(List<Integer> sequence) {

    public List<Integer> getSequence() {
        return sequence;
    }

    /**
     * Calculates the fitness of the individual using euclidean distance.
     */
    public double calculateFitness(List<City> cities) {
        double totalDistance = 0.0;

        for (int i = 0; i < cities.size(); i++) {
            City city1 = cities.get(sequence.get(i));
            City city2 = cities.get(sequence.get((i + 1) % cities.size()));

            /* Calculate the distance between the two cities and add it to the total distance. */
            totalDistance += city1.distanceTo(city2);
        }

        return totalDistance;
    }

    /**
     * Used for showing the optimal path when the program is stopped
     */
    public List<Integer> printSequence() {
        List<Integer> optimalPath = new ArrayList<>(this.sequence);

        // Add the first city to the list
        optimalPath.add(optimalPath.get(0));

        return optimalPath;
    }
}
