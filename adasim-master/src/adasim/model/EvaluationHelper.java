package adasim.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EvaluationHelper {
    private static EvaluationHelper instance = new EvaluationHelper();
    private int pathCost;
    private HashMap<Integer, CostStruct> costMap;
    private ArrayList<Integer> tempCostList = new ArrayList<Integer>();
    private ArrayList<RoadSegment> tempPathList = new ArrayList<>();

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

    public void setCostMap(Integer vehicleID) {
        this.costMap.put(vehicleID, new CostStruct(tempPathList, tempCostList));
        this.clearTemp();
    }

    public ArrayList<Integer> getCostMapCostList(Integer vehicleID) {
        return this.costMap.get(vehicleID).pathCost;
    }

    public ArrayList<RoadSegment> getCostMapPathList(Integer vehicleID) {
        return this.costMap.get(vehicleID).path;
    }

    public void setVehiclePathCostList(Integer cost) {
        this.tempCostList.add(cost);
    }

    public void clearVehiclePathCostList() {
        this.tempCostList.clear();
    }

    public void setPathList(List<RoadSegment> pathList) {
        this.tempPathList.addAll(pathList);
    }

    public void clearPathList() {
        this.tempPathList.clear();
    }

    public void clearTemp() {
        this.tempCostList.clear();
        this.tempPathList.clear();
    }

    public Integer getPathCost() {
        return this.pathCost;
    }

    class CostStruct {
        ArrayList<RoadSegment> path = new ArrayList<>();
        ArrayList<Integer> pathCost = new ArrayList<>();

        CostStruct(ArrayList<RoadSegment> path, ArrayList<Integer> pathCost) {
            this.path = path;
            this.pathCost = pathCost;
        }

    }
}
