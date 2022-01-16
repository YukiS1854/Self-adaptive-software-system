package adasim.model;

public class EvaluationHelper {
    private static EvaluationHelper instance = new EvaluationHelper();
    private int pathCost;

    private EvaluationHelper() {
        this.pathCost = 0;
    }

    public static EvaluationHelper getInstance() {
        return instance;
    }

    public void setPathCost(Integer cost) {
        this.pathCost = cost;
    }

    public void addPathCost(Integer cost) {
        this.pathCost += cost;
    }

    public Integer getPathCost() {
        return this.pathCost;
    }

}
