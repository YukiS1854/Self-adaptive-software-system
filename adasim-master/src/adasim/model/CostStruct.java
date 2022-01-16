package adasim.model;

import java.util.ArrayList;

public class CostStruct {

    ArrayList<RoadSegment> path = new ArrayList<>();
    ArrayList<Integer> pathCost = new ArrayList<>();

    CostStruct(ArrayList<RoadSegment> path, ArrayList<Integer> pathCost) {
        this.path.addAll(path);
        this.pathCost.addAll(pathCost);
    }

    public ArrayList<RoadSegment> getPath() {
        return this.path;
    }

    public ArrayList<Integer> getPathCost() {
        return this.pathCost;
    }

}
