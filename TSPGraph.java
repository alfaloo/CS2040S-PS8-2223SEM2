import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class TSPGraph implements IApproximateTSP {

    private class Edge implements Comparable<Edge> {
        int from;
        int to;
        TSPMap map;
        public Edge(int f, int t, TSPMap map) {
            this.from = f;
            this.to = t;
            this.map = map;
        }

        @Override
        public int compareTo(Edge other) {
            return this.map.pointDistance(this.from, this.to) < other.map.pointDistance(other.from, other.to)
                    ? -1
                    : this.map.pointDistance(this.from, this.to) > other.map.pointDistance(other.from, other.to)
                    ? 1
                    : 0;
        }
    }

    @Override
    public void MST(TSPMap map) {
        int len = map.getCount();
        PriorityQueue<Edge> queue = new PriorityQueue<>();
        ArrayList<Integer> added = new ArrayList<Integer>();

        ArrayList<Integer> absent = new ArrayList<Integer>();
        for (int i = 1; i < len; i++) {
            absent.add(i);
        }

        added.add(0);
        for (int i = 1; i < map.getCount(); i++) {
            queue.add(new Edge(0, i, map));
        }

        for (int iter = 0; iter < len - 1; iter++) {
            Edge edge = queue.poll();
            if (added.contains(new Integer(edge.to))) {
                iter--;
                continue;
            }
            map.setLink(edge.to, edge.from);
            added.add(new Integer(edge.to));
            absent.remove(new Integer(edge.to));

            for (int i = 0; i < map.getCount(); i++) {
                if (absent.contains(new Integer(i))) {
                    queue.add(new Edge(edge.to, i, map));
                }
            }
        }
    }

    public int findClosest(LinkedList<Integer> leaf, int curr, TSPMap map) {
        if (leaf.isEmpty()) return -1;
        int point = leaf.peek();
        double minDistance = map.pointDistance(point, curr);
        for (Integer i : leaf) {
            if (map.pointDistance(i, curr) < minDistance) {
                point = i;
                minDistance = map.pointDistance(i, curr);
            }
        }
        leaf.remove(new Integer(point));
        return point;
    }

    @Override
    public void TSP(TSPMap map) {
        int len = map.getCount();

        boolean[] been = new boolean[len];
        for (int i = 0; i < len; i++) {
            been[i] = false;
        }

        MST(map);

        LinkedList<Integer> leaf = new LinkedList<Integer>();
        for (int i = 0; i < len; i++) {
            leaf.add(i);
        }
        for (int i = 0; i < len; i++) {
            leaf.remove(new Integer(map.getLink(i)));
        }

        int start = leaf.peek();
        int jumps = leaf.size();
        int curr = leaf.poll();

        for (int i = 0; i < jumps; i++) {
            while (true) {
                been[curr] = true;
                if (map.getLink(curr) == -1) {
                    int next = findClosest(leaf, curr, map);
                    if (next != -1) {
                        map.setLink(curr, next);
                        curr = next;
                        break;
                    } else {
                        map.setLink(curr, start);
                        break;
                    }
                } else if (been[map.getLink(curr)]) {
                    map.eraseLink(curr);
                    int next = findClosest(leaf, curr, map);
                    if (next != -1) {
                        map.setLink(curr, next);
                        curr = next;
                        break;
                    } else {
                        map.setLink(curr, start);
                        break;
                    }
                }
                curr = map.getLink(curr);
            }
            System.out.println(leaf);
        }
    }

    @Override
    public boolean isValidTour(TSPMap map) {
        int len = map.getCount();

        boolean[] been = new boolean[len];
        for (int i = 0; i < len; i++) {
            been[i] = false;
        }

        int curr = 0;

        for (int i = 0; i < len - 1; i++) {
            if (been[curr] || map.getLink(curr) == -1) return false;
            been[curr] = true;
            curr = map.getLink(curr);
        }

        return map.getLink(curr) == 0;
    }

    @Override
    public double tourDistance(TSPMap map) {
        if (!isValidTour(map)) return -1;

        int len = map.getCount();

        int curr = 0;
        double distance = 0;

        for (int i = 0; i < len; i++) {
            int next = map.getLink(curr);
            distance += map.pointDistance(curr, next);
            curr = next;
        }

        return distance;
    }

    public static void main(String[] args) {
        TSPMap map = new TSPMap(args.length > 0 ? args[0] : "hundredpoints.txt");
        TSPGraph graph = new TSPGraph();

        // graph.MST(map);
        graph.TSP(map);
        System.out.println(graph.isValidTour(map));
        System.out.println(graph.tourDistance(map));
    }
}
