package adasim.model;

public class EvaluationHelper {
    private static EvaluationHelper instance = new EvaluationHelper();
    private double pathCost;

    private EvaluationHelper() {
        this.pathCost = 0;
    }

    public static EvaluationHelper getInstance() {
        return instance;
    }

    public void setPathCost(Double cost) {
        this.pathCost = cost;
    }

    public void addPathCost(Double cost) {
        this.pathCost += cost;
    }

    public Double getPathCost() {
        return this.pathCost;
    }

}
