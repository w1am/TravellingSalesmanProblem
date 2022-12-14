package geneticAlgorithm;

public class City {
    private int xCoordinate;
    private int yCoordinate;

    public City(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double distanceTo(City city) {
        int xDistance = Math.abs(getXCoordinate() - city.getXCoordinate());
        int yDistance = Math.abs(getYCoordinate() - city.getYCoordinate());

        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }
}
