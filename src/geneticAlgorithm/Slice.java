package geneticAlgorithm;

public class Slice {

    float xLower;
    float xUpper;
    float yLower;
    float yUpper;

    public Slice(float xLower, float yLower, float xUpper, float yUpper) {
        this.xLower = xLower;
        this.yLower = yLower;
        this.xUpper = xUpper;
        this.yUpper = yUpper;
    }

    public float area() {
        return (this.xUpper - this.xLower) * (this.yUpper - this.yLower);
    }

    public float getxLower() {
        return xLower;
    }

    public void setxLower(float xLower) {
        this.xLower = xLower;
    }

    public float getxUpper() {
        return xUpper;
    }

    public void setxUpper(float xUpper) {
        this.xUpper = xUpper;
    }

    public float getyLower() {
        return yLower;
    }

    public void setyLower(float yLower) {
        this.yLower = yLower;
    }

    public float getyUpper() {
        return yUpper;
    }

    public void setyUpper(float yUpper) {
        this.yUpper = yUpper;
    }
}
