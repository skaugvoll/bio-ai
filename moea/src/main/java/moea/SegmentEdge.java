package moea;

public class SegmentEdge implements Comparable<SegmentEdge> {

    double distance;
    Segment s1;
    Segment s2;

    public SegmentEdge(Segment s1, Segment s2){
        this.s1 = s1;
        this.s2 = s2;
        this.distance = this.euclideanDistance(s1,s2);
    }

    public double euclideanDistance(Segment s1, Segment s2){
        return Math.sqrt((Math.pow(s1.centroid[0] - s2.centroid[0], 2) + Math.pow((double) (s1.centroid[1] - s2.centroid[1]), 2.0)));

    }

    @Override
    public int compareTo(SegmentEdge o) {
        return (int) (this.distance - o.distance);
    }
}
