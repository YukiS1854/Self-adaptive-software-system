package adasim.algorithm.routing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import adasim.model.RoadSegment;
import adasim.model.Vehicle;

/**
 * This car strategy is the base implementation for all strategies
 * using Dijkstras shortest path algorithm on node weights and
 * adasim delays.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class ConsiderSensorErrorRoutingAlgorithm extends AbstractRoutingAlgorithm {

    private final static Logger logger = Logger.getLogger(ConsiderSensorErrorRoutingAlgorithm.class);

    private final int lookahead;
    private final int recompute;
    private List<RoadSegment> path;
    private int steps;
    private boolean finished = false;

    /**
     * The default constructor builds this strategy with a lookahead of 0.
     * <p>
     * A lookahead of 0 means that the strategy will only consider the
     * unmodified weight of each node, and for this reason will never recompute
     * the path.
     */
    public ConsiderSensorErrorRoutingAlgorithm() {
        this(0);
    }

    /**
     * Creates a new strategy object.
     * The lookahead parameter defines how far ahead the strategy considers adasim
     * in addition to node
     * delays. This parameter also defines how often the strategy recomputes the
     * path
     * it follows.
     * <p>
     * For a lookahead of <em>n</em> it will consider adasim for <em>n</em> nodes
     * from the
     * current node, and will recompute the path every <em>n</em> moves.
     * 
     * @param lookahead
     */
    public ConsiderSensorErrorRoutingAlgorithm(int lookahead) {
        this(lookahead, lookahead);
    }

    /**
     * Creates a new strategy object.
     * The lookahead parameter defines how far ahead the strategy considers adasim
     * in addition to node
     * delays. This parameter also defines how often the strategy recomputes the
     * path
     * it follows.
     * <p>
     * For a lookahead of <em>n</em> it will consider adasim for <em>n</em> nodes
     * from the
     * current node, and will recompute the path every <em>recomp</em> moves.
     * 
     * @param lookahead
     */
    public ConsiderSensorErrorRoutingAlgorithm(int lookahead, int recomp) {
        this.lookahead = lookahead;
        this.recompute = recomp;
        this.steps = 0;
        logger.info("ConsiderSensorErrorRoutingAlgorithm(" + lookahead + "," + recompute + ")");
    }

    public List<RoadSegment> getPath(RoadSegment source, RoadSegment target) {
        return dijkstra(graph.getRoadSegments(), source, target);
    }

    /**
     * Computes Dijkstra's shortest path algorithm on the graph represented by
     * <code>nodes</code>, and returns a list of nodes that represent
     * the shortest past from <code>source</code> to <code>target</code>.
     * 
     * @param nodes
     * @param source
     * @param target
     * @param l
     * @return the shortest past from <code>source</code> to <code>target</code>
     */
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
