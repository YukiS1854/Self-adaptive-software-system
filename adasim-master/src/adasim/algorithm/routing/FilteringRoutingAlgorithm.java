package adasim.algorithm.routing;

import adasim.model.EvaluationHelper;
import adasim.model.RoadSegment;
import adasim.model.Vehicle;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class FilteringRoutingAlgorithm extends AbstractRoutingAlgorithm {

    private final static Logger logger = Logger.getLogger(FilteringRoutingAlgorithm.class);

    private final int lookahead;
    private final int recompute;
    private List<RoadSegment> path;
    private int steps;
    private boolean finished = false;
    private EvaluationHelper evaluationHelper = EvaluationHelper.getInstance();

    public FilteringRoutingAlgorithm() {
        this(0);
    }

    public FilteringRoutingAlgorithm(int lookahead) {
        this(lookahead, lookahead);
    }

    public FilteringRoutingAlgorithm(int lookahead, int recomp) {
        this.lookahead = lookahead;
        this.recompute = recomp;
        this.steps = 0;
        logger.info("SensorErrorRoutingAlgorithm(" + lookahead + "," + recompute + ")");
    }

    public List<RoadSegment> getPath(RoadSegment source, RoadSegment target) {
        return routing(graph.getRoadSegments(), source, target);
    }

    private List<RoadSegment> routing(List<RoadSegment> nodes, RoadSegment source, RoadSegment target) {

        ArrayList<RoadSegment> open = new ArrayList<RoadSegment>();
        ArrayList<RoadSegment> close = new ArrayList<RoadSegment>();
        ArrayList<RoadSegment> full = new ArrayList<RoadSegment>();

        logger.info("routing start. source :" + source + ", target :" + target);
        source.getNeighbors().forEach((it) -> {
            open.add(it);
        });
        while (!open.contains(target)) {
            open.clear();
            source.getNeighbors().forEach((it) -> {
                open.add(it);
            });
            source = getMinDelay(open);
            close.add(source);
        }
        full.addAll(close);
        full.add(target);
        evaluationHelper.setPathList(full);
        evaluationHelper.setCostMap(vehicle.getID());
        return close;
    }

    private RoadSegment getMinDelay(List<RoadSegment> neighbors) {
        ArrayList<Integer> delayLs = new ArrayList<Integer>();
        ArrayList<Integer> errorTestLs = new ArrayList<Integer>();
        ArrayList<Integer> sortDelayLs = new ArrayList<Integer>();
        int minDelay;
        neighbors.forEach((neighbor) -> {
            int delay;
            errorTestLs.add(neighbor.getCurrentDelay(Vehicle.class));
            errorTestLs.add(neighbor.getCurrentDelay(Vehicle.class));
            // for (int i = 0; i < 100; i++) {
            // errorTestLs.add(neighbor.getCurrentDelay(Vehicle.class));
            // }

            if (errorTestLs.get(1) != errorTestLs.get(0)) {
                int newDelay = neighbor.getCurrentDelay(Vehicle.class);
                while (!errorTestLs.contains(newDelay)) {
                    errorTestLs.add(newDelay);
                    newDelay = neighbor.getCurrentDelay(Vehicle.class);
                }
                delay = newDelay;
            } else
                delay = errorTestLs.get(0);

            delayLs.add(delay);
            sortDelayLs.add(delay);
            errorTestLs.clear();
        });
        minDelay = delayLs.get(0);
        sortDelayLs.sort(Comparator.naturalOrder());
        evaluationHelper.setVehiclePathCostList(sortDelayLs.get(0));
        int minIndex = delayLs.lastIndexOf(sortDelayLs.get(0));

        delayLs.clear();
        errorTestLs.clear();
        sortDelayLs.clear();
        return neighbors.get(minIndex);
    }

    /**
     * @param nodes
     * @param node
     * @return the index of the node in the list, -1 if the node cannot be found
     */
    private int getIndex(List<RoadSegment> nodes, RoadSegment node) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).equals(node))
                return i;
        }

        return -1;
    }

    /**
     * Computes the array index of the smallest element
     * 
     * @param q
     * @return the index of the smalles element
     */
    private int getIndexOfMin(Set<Integer> q, int[] dist) {
        int min = q.iterator().next();
        for (int i : q) {
            if (dist[min] > dist[i])
                min = i;
        }
        return min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see adasim.algorithm.CarStrategy#getNextNode()
     */
    @Override
    public RoadSegment getNextNode() {
        if (finished)
            return null;
        if (path == null) {
            path = getPath(source);
            logger.info(pathLogMessage());
            logger.info(evaluationHelper.getCostMapPathList(vehicle.getID()));
            logger.info(evaluationHelper.getCostMapCostList(vehicle.getID()));
        }
        assert path != null || finished;
        if (path == null || path.size() == 0) {
            finished = true;
            return null;
        }
        if (++steps == recompute) {
            RoadSegment next = path.remove(0);
            path = getPath(next);
            logger.info("UPDATE: " + pathLogMessage());
            steps = 0;
            return next;
        } else {
            return path.remove(0);
        }
    }

    /**
     * Computes a path to the configured target node starting from
     * the passed <code>start</code> node.
     * 
     * @param start
     */
    private List<RoadSegment> getPath(RoadSegment start) {
        List<RoadSegment> p = routing(graph.getRoadSegments(), start, target);
        if (p == null) {
            finished = true;
        }
        return p;
    }

    private String pathLogMessage() {
        StringBuffer buf = new StringBuffer("PATH: Vehicle: ");
        buf.append(vehicle.getID());
        buf.append(" From: ");
        buf.append(source.getID());
        buf.append(" To: ");
        buf.append(target.getID());
        buf.append(" Path: ");
        buf.append(path == null ? "[]" : path);
        return buf.toString();
    }

    @Override
    public int isFilter(){
        return 1;
    }
}
