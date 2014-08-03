/**
 * Created with IntelliJ IDEA.
 * User: rshen
 * Date: 7/19/14
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PointSET {
    private SET<Point2D> pointSet;

    public PointSET() {
        pointSet = new SET<Point2D>();
    }

    public boolean isEmpty() {
        return pointSet.isEmpty();
    }

    public int size() {
        return pointSet.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        pointSet.add(p);
    }

    public boolean  contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return pointSet.contains(p);
    }

    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
         for (Point2D p : pointSet) {
             StdDraw.point(p.x(), p.y());
         }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        SET<Point2D> rangeSet = new SET<Point2D>();
          for (Point2D p : pointSet) {
              if (p.x() >= rect.xmin() && p.x() <= rect.xmax()) {
                  if (p.y() >= rect.ymin() && p.y() <= rect.ymax()) {
                       rangeSet.add(p);
                  }
              }
          }
        return rangeSet;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        double minDistance = Math.sqrt(2);
        Point2D nearest = new Point2D(0, 0);
        for (Point2D neighbor : pointSet) {
             double distance = p.distanceTo(neighbor);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = neighbor;
            }
        }
        return nearest;
    }

//    public static void main(String[] args){
//        PointSET test = new PointSET();
//        Point2D p1 = new Point2D(0.1,0.1);
//        Point2D p2 = new Point2D(0.1,0.1);
//        Point2D p3 = new Point2D(0.5,0.5);
//        Point2D p4 = new Point2D(0.2, 0.2);

//        System.out.println(test.isEmpty());
//        System.out.println(test.size());

//        test.insert(p1);
//        test.insert(p2);
//        test.insert(p3);
//        test.insert(p4);

//        System.out.println(test.isEmpty());
//        System.out.println(test.size());
//        System.out.println(test.contains(p2));

//        test.draw();
//        Point2D nearest = test.nearest(p1);
//        System.out.println(nearest.x());
//
//        RectHV rect = new RectHV(0,0,0.4,0.4);
//        SET<Point2D> result = (SET<Point2D>) test.range(rect);
//        for (Point2D p :  result) {
//            System.out.println(p.x());
//        }
//    }
}
