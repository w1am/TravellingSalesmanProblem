package geneticAlgorithm;

public record City(int xCoordinate, int yCoordinate) {

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public double distanceTo(City city) {
        int xDistance = Math.abs(getXCoordinate() - city.getXCoordinate());
        int yDistance = Math.abs(getYCoordinate() - city.getYCoordinate());

        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }
}
