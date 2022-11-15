package geneticAlgorithm;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Individual {
    private final List<Integer> sequence;
    private final List<City> cities;
    private final HashMap<Pair<Integer, Integer>, Float> pathSpeedLimits;
    private int rank;
    private double crowdingDistance;
    private final float distanceFitness;
    private final float timeFitness;
    private float normalizedDistanceFitness;
    private float normalizedTimeFitness;

    public Individual(
            List<Integer> sequence,
            List<City> cities,
            HashMap<Pair<Integer, Integer>, Float> pathSpeedLimits
    ) {
        this.sequence = sequence;
        this.cities = cities;
        this.pathSpeedLimits = pathSpeedLimits;
        this.distanceFitness = this.getTotalDistance();
        this.timeFitness = this.getTotalTime();
    }

    public float getTotalDistance() {
        float totalDistance = 0.0f;

        for (int i = 1; i < sequence.size(); i++) {
            City fromTown = this.cities.get(sequence.get(i - 1));
            City toTown = this.cities.get(sequence.get(i));

            float x = toTown.getXCoordinate() - fromTown.getXCoordinate();
            float y = toTown.getYCoordinate() - fromTown.getYCoordinate();

            double d = (float) Math.sqrt(x * x + y * y);

            totalDistance += d;
        }

        totalDistance += this.cities
                .get(this.sequence.get(this.sequence.size() - 1))
                .distanceTo(this.cities.get(this.sequence.get(0)));

        return totalDistance;
    }

    public Float getTotalTime() {
        float totalTime = 0.0f;

        for (int i = 1; i < sequence.size(); i++) {
            City fromTown = this.cities.get(sequence.get(i - 1));
            City toTown = this.cities.get(sequence.get(i));

            float x = toTown.getXCoordinate() - fromTown.getXCoordinate();
            float y = toTown.getYCoordinate() - fromTown.getYCoordinate();

            double d = (float) Math.sqrt(x * x + y * y);

            Float speedLimit = this.pathSpeedLimits.get(new Pair<>(i, i - 1));

            if (this.pathSpeedLimits.containsKey(new Pair<>(i, i - 1))) {
                totalTime += d / speedLimit;
            }
        }

        return totalTime;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Individual individual) {
            return this.sequence.equals(individual.sequence);
        } else {
            return false;
        }
    }

    public List<Integer> getSequence() {
        return sequence;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public float getDistanceFitness() {
        return distanceFitness;
    }

    public float getTimeFitness() {
        return timeFitness;
    }

    public float getNormalizedDistanceFitness() {
        return normalizedDistanceFitness;
    }

    public void setNormalizedDistanceFitness(float normalizedDistanceFitness) {
        this.normalizedDistanceFitness = normalizedDistanceFitness;
    }

    public float getNormalizedTimeFitness() {
        return normalizedTimeFitness;
    }

    public void setNormalizedTimeFitness(float normalizedTimeFitness) {
        this.normalizedTimeFitness = normalizedTimeFitness;
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
