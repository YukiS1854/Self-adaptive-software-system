package adasim.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.TextOutputCallback;

public class EvaluationHelper {
    private static EvaluationHelper instance = new EvaluationHelper();
    private int pathCost;
    private HashMap<Integer, CostStruct> costMap = new HashMap<Integer, CostStruct>();
    private ArrayList<Integer> tempCostList = new ArrayList<Integer>();
    private ArrayList<RoadSegment> tempPathList = new ArrayList<>();

    private EvaluationHelper() {
        this.pathCost = 0;
    }

    public int getTotalCost() {
        int totalCost = 0;
        for (Map.Entry<Integer, CostStruct> entry : costMap.entrySet()) {
            CostStruct costStruct = entry.getValue();
            for (int k = 0; k < costStruct.pathCost.size(); k++) {
                totalCost += costStruct.pathCost.get(k);
            }
        }
        return totalCost;
    }

    public HashMap<Integer, CostStruct> getMap() {
        return this.costMap;
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

}
