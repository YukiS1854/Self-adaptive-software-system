package adasim.algorithm.routing;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import adasim.model.EvaluationHelper;
import adasim.model.RoadSegment;
import adasim.model.Vehicle;

public class SensorErrorRoutingAlgorithm extends AbstractRoutingAlgorithm {

    private final static Logger logger = Logger.getLogger(SensorErrorRoutingAlgorithm.class);

    private final int lookahead;
    private final int recompute;
    private List<RoadSegment> path;
    private int steps;
    private boolean finished = false;

    public SensorErrorRoutingAlgorithm() {
        this(0);
    }

    public SensorErrorRoutingAlgorithm(int lookahead) {
        this(lookahead, lookahead);
    }

    public SensorErrorRoutingAlgorithm(int lookahead, int recomp) {
        this.lookahead = lookahead;
        this.recompute = recomp;
        this.steps = 0;
        logger.info("ConsiderSensorErrorRoutingAlgorithm(" + lookahead + "," + recompute + ")");
    }

    public List<RoadSegment> getPath(RoadSegment source, RoadSegment target) {
        return dijkstra(graph.getRoadSegments(), source, target);
    }

    private List<RoadSegment> dijkstra(List<RoadSegment> nodes, RoadSegment source, RoadSegment target) {

        ArrayList<RoadSegment> open = new ArrayList<RoadSegment>();
        ArrayList<RoadSegment> close = new ArrayList<RoadSegment>();

        source.getNeighbors().forEach((it) -> {
            open.add(it);
        });
        while (!open.contains(target)) {
            open.clear();
            close.add(source);
            source.getNeighbors().forEach((it) -> {
                open.add(it);
            });
            source = getMinDelay(open);
        }

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
        });
        minDelay = delayLs.get(0);
        sortDelayLs.sort(Comparator.naturalOrder());
        EvaluationHelper.getInstance().addPathCost(sortDelayLs.get(0));
        int minIndex = delayLs.lastIndexOf(sortDelayLs.get(0));
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
        List<RoadSegment> p = dijkstra(graph.getRoadSegments(), start, target);
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
}
