package geneticAlgorithm;

import java.util.*;
import java.util.stream.Collectors;

public class MultiObjective {
    public static void updatePopulationFitness(List<Individual> population) {
        // clear the existing ranks and crowding distances
        for (Individual individual : population) {
            individual.setRank(-1);
            individual.setCrowdingDistance(-1);
        }

        normalizeFitnessValues(population);

        List<Individual> remainingToBeRanked = new ArrayList<>(population);

        int rank = 1;
        while (!remainingToBeRanked.isEmpty()) {
            List<Individual> inRanks = new ArrayList<>();

            for (int i = 0; i < remainingToBeRanked.size(); i++) {
                Individual individual = remainingToBeRanked.get(i);
                if (isNotDominated(individual, remainingToBeRanked)) {
                    individual.setRank(rank);
                    inRanks.add(individual);
                }
            }

            remainingToBeRanked.removeAll(inRanks);
            rank++;
        }

        // For each rank, calculate the crowding distance for each individual
        Map<Integer, List<Individual>> ranks = population.stream()
                .collect(Collectors.groupingBy(Individual::getRank));

        ranks.forEach((key, value) -> {
            calculateCrowdingDistance(new HashMap<>(Map.of(key, value)));
        });

    }

    private static void normalizeFitnessValues(List<Individual> population) {
        // Get the maximum distance from the population
        float maxDistance = Objects.requireNonNull(population.stream()
                .max(Comparator.comparing(Individual::getDistanceFitness))
                .orElse(null))
                .getDistanceFitness();
        float maxTime = Objects.requireNonNull(population.stream()
                .max(Comparator.comparing(Individual::getTimeFitness))
                .orElse(null)).getTimeFitness();

        for (Individual individual : population) {
            individual.setNormalizedDistanceFitness(individual.getDistanceFitness() / maxDistance);
            individual.setNormalizedTimeFitness(individual.getTimeFitness() / maxTime);
        }
    }

    private static void calculateCrowdingDistance(Map<Integer, List<Individual>> singleRank) {
        // As we only have two objectives, ordering individuals along one front allows us to make assumptions
        // about the locations of the neighbouring individuals in the array.
        List<Individual> orderedIndividuals = singleRank.values().stream().flatMap(Collection::stream)
                .sorted(Comparator.comparing(Individual::getNormalizedDistanceFitness))
                .toList();

        int individualInFront = orderedIndividuals.size();

        for (int i = 0; i < individualInFront; i++) {
            // If we are at the start or end of a front, it should have infinite crowding distance
            if (i == 0 || i == individualInFront - 1) {
                orderedIndividuals.get(i).setCrowdingDistance(Double.POSITIVE_INFINITY);
            } else {
                // Grab a reference to each individual to make the next section a bit cleaner.
                Individual current = orderedIndividuals.get(i);
                Individual left = orderedIndividuals.get(i - 1);
                Individual right = orderedIndividuals.get(i + 1);

                // Get the positions on the 2D fitness graph, where
                // time is our X axis and distance is our Y.
                Vector currentPosition = new Vector(
                        current.getNormalizedTimeFitness(),
                        current.getNormalizedDistanceFitness()
                );
                Vector leftPosition = new Vector(
                        left.getNormalizedTimeFitness(),
                        left.getNormalizedDistanceFitness()
                );
                Vector rightPosition = new Vector(
                        right.getNormalizedTimeFitness(),
                        right.getNormalizedDistanceFitness()
                );

                // Calculate the distance to the neighbours on each side
                double distanceLeft = currentPosition.distanceTo(leftPosition);
                double distanceRight = currentPosition.distanceTo(rightPosition);

                // Set the crowding distance for the current individual
                orderedIndividuals.get(i).setCrowdingDistance(distanceLeft + distanceRight);
            }
        }
    }

    private static boolean isNotDominated(Individual individualA, List<Individual> remainingToBeRanked) {
        for (Individual individualB : remainingToBeRanked) {
            if (individualA.getSequence() == individualB.getSequence()) {
                continue;
            }

            if (individualB.getDistanceFitness() < individualA.getDistanceFitness()
                    && individualB.getTimeFitness() < individualA.getTimeFitness()) {
                return false;
            }
        }

        return true;
    }

    public static float calculateArea(List<Individual> firstRank) {
        // Create all the slices
        List<Slice> slices = getSlices(firstRank);

        // Return the sum of the slices
        return slices.stream().map(Slice::area).reduce(0f, Float::sum);
    }

    private static List<Slice> getSlices(List<Individual> firstRank) {
        Slice previousSlice = new Slice(0, 0, 0, 0);

        List<Slice> trackedSlices = new ArrayList<>();

        // Update individual in first rank but keep track of the previous slices
        for (Individual individual : firstRank) {
            Slice currentSlice = new Slice(
                    previousSlice.getxUpper(),
                    0,
                    individual.getTimeFitness(),
                    individual.getDistanceFitness()
            );
            trackedSlices.add(currentSlice);
        }

        return trackedSlices;
    }
}
