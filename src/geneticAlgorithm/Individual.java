package geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

public record Individual(List<Integer> sequence) {

    public List<Integer> getSequence() {
        return sequence;
    }

    public double calculateFitness(List<City> cities) {
        double totalDistance = 0.0;

        for (int i = 1; i < sequence.size(); i++) {
            City fromTown = cities.get(sequence.get(i - 1));
            City toTown = cities.get(sequence.get(i));

            int x = toTown.getXCoordinate() - fromTown.getXCoordinate();
            int y = toTown.getYCoordinate() - fromTown.getYCoordinate();

            double d = Math.sqrt(x * x + y * y);

            totalDistance += d;
        }

        totalDistance += cities
                .get(this.sequence.get(this.sequence.size() - 1))
                .distanceTo(cities.get(this.sequence.get(0)));

        return totalDistance;
    }

    public List<Integer> printSequence() {
        List<Integer> cities = new ArrayList<>();

        for (Integer city : this.sequence) {
            cities.add(city + 1);
        }

        cities.add(cities.get(0));

        return cities;
    }
}
