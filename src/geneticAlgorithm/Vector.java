package geneticAlgorithm;

public record Vector(Float xCoordinate, Float yCoordinate) {

    public Float getXCoordinate() {
        return xCoordinate;
    }

    public Float getYCoordinate() {
        return yCoordinate;
    }

    public double distanceTo(Vector vector) {
        float xDistance = Math.abs(this.getXCoordinate() - vector.getXCoordinate());
        float yDistance = Math.abs(this.getYCoordinate() - vector.getYCoordinate());

        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }
}
