import java.awt.geom.*;

/**
 * Created with IntelliJ IDEA.
 * User: rshen
 * Date: 7/19/14
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class KdTree {
    private class Node {
        private Point2D point;
        private Node right;
        private Node left;
        private boolean isVertical = true;

        private boolean getVertical() {
            return isVertical;
        }

        private void setVertical(boolean value) {
            isVertical = value;
        }

        private Point2D getPoint() {
            return point;
        }

        private void setPoint(Point2D value) {
            point = value;
        }

        private Node getRight() {
            return right;
        }

        private void setRight(Node value) {
            right =  value;
        }

        private Node getLeft() {
            return left;
        }

        private void setLeft(Node value) {
            left = value;
        }

        private boolean insideRect(RectHV rect) {
            return point.x() >= rect.xmin() && point.x() <= rect.xmax() && point.y() >= rect.ymin() && point.y() <= rect.ymax();
        }

        private double getX() {
            return point.x();
        }

        private double getY() {
            return point.y();
        }

        private boolean sameAs(Point2D p) {
            return point.x() == p.x() && point.y() == p.y();
        }
    }

    private Node root;
    private int size;

    public KdTree() {
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        Node newNode = new Node();
        newNode.setPoint(p);
        root = insertNode(root, newNode);
    }

    private Node insertNode(Node rootNode, Node newNode) {
        if (rootNode == null) {
            size++;
            return newNode;
        }
        if (rootNode.isVertical) {
            newNode.setVertical(false);
           if (newNode.getX() < rootNode.getX()) {
               rootNode.setLeft(insertNode(rootNode.getLeft(), newNode));
           }
             else {
               if (!rootNode.sameAs(newNode.point)) {
               rootNode.setRight(insertNode(rootNode.getRight(), newNode));
              } }
        }
         else {
            newNode.setVertical(true);
            if (newNode.getY() < rootNode.getY()) {
                rootNode.setLeft(insertNode(rootNode.getLeft(), newNode));
            }  else {
                if (!rootNode.sameAs(newNode.point)) {
                rootNode.setRight(insertNode(rootNode.getRight(), newNode));
               }
            }
        }
        return rootNode;
    }

    public boolean  contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return containsPoint(root, p, true);
    }

    private boolean containsPoint(Node rootNode, Point2D p, boolean isVertical) {
        if (rootNode == null) {
            return false;
        }
        if (rootNode.sameAs(p)) {
            return true;
        }
        if (isVertical) {
            if (rootNode.getX() > p.x()) {
                return containsPoint(rootNode.getLeft(), p, false);
            } else {
                return containsPoint(rootNode.getRight(), p, false);
            }
        } else {
            if (rootNode.getY() > p.y()) {
                return containsPoint(rootNode.getLeft(), p, true);
            } else {
                return containsPoint(rootNode.getRight(), p, true);
            }
        }
    }

    public void draw() {
        drawKdTree(root, true, 0, 0, 1, 1);
    }

    private void drawKdTree(Node rootNode, boolean isVertical, double xmin, double ymin, double xmax, double ymax) {
        if (rootNode == null) {
            return;
        }
         if (isVertical) {
             StdDraw.setPenColor(StdDraw.RED);
             StdDraw.setPenRadius();
             StdDraw.line(rootNode.getX(), ymin, rootNode.getX(), ymax);
             StdDraw.setPenColor(StdDraw.BLACK);
             StdDraw.setPenRadius(0.01);
             StdDraw.point(rootNode.getX(), rootNode.getY());
             drawKdTree(rootNode.getLeft(), false, xmin, ymin, rootNode.getX(), ymax);
             drawKdTree(rootNode.getRight(), false, rootNode.getX(), ymin, xmax, ymax);
         } else {
             StdDraw.setPenColor(StdDraw.BLUE);
             StdDraw.setPenRadius();
             StdDraw.line(xmin, rootNode.getY(), xmax, rootNode.getY());
             StdDraw.setPenColor(StdDraw.BLACK);
             StdDraw.setPenRadius(0.01);
             StdDraw.point(rootNode.getX(), rootNode.getY());
             drawKdTree(rootNode.getLeft(), true, xmin, ymin, xmax, rootNode.getY());
             drawKdTree(rootNode.getRight(), true, xmin, rootNode.getY(), xmax, ymax);
         }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        SET<Point2D> rangeSet = new SET<Point2D>();
        rangeSearch(root, rect, rangeSet, true);
        return rangeSet;

    }

    private void rangeSearch(Node rootNode, RectHV rect, SET<Point2D> rangeSet, boolean isVertical) {
         if (rootNode == null) {
             return;
         }
         if (isVertical) {
             if (rootNode.getX() < rect.xmin()) {
                 rangeSearch(rootNode.getRight(), rect, rangeSet, false);

             } else if (rootNode.getX() > rect.xmax()) {
                 rangeSearch(rootNode.getLeft(), rect, rangeSet, false);

             } else {
                 if (rootNode.insideRect(rect)) {
                     rangeSet.add(rootNode.getPoint()); }
                     rangeSearch(rootNode.getLeft(), rect, rangeSet, false);
                     rangeSearch(rootNode.getRight(), rect, rangeSet, false);
             }
         } else {
             if (rootNode.getY() < rect.ymin()) {
                 rangeSearch(rootNode.getRight(), rect, rangeSet, true);

             } else if (rootNode.getY() > rect.ymax()) {
                 rangeSearch(rootNode.getLeft(), rect, rangeSet, true);

             } else {
                 if (rootNode.insideRect(rect)) {
                     rangeSet.add(rootNode.getPoint()); }
                     rangeSearch(rootNode.getLeft(), rect, rangeSet, true);
                     rangeSearch(rootNode.getRight(), rect, rangeSet, true);
             }
         }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return null;
        }
        Node nearestNode = findNearest(root, p, root, p.distanceSquaredTo(root.getPoint()));
        if (nearestNode == null) {
            return null;
        }
        return nearestNode.getPoint();
    }

    private Node findNearest(Node rootNode, Point2D p, Node minNode, double minDistance) {
        if (rootNode == null) {
            return minNode;
        }

//        double newDistance = p.distanceSquaredTo(rootNode.getPoint());
//        if (newDistance < minDistance) {
//            minDistance = newDistance;
//            minNode =  rootNode;
//        }
//
//        minNode = findNearest(rootNode.getLeft(), p, minNode, minDistance);
//        minNode = findNearest(rootNode.getRight(), p, minNode, minDistance);
        return minNode;
     }


    public static void main(String[] args) {
        KdTree test = new KdTree();

        Point2D p1 = new Point2D(0.1, 0.1);
        Point2D p2 = new Point2D(0.6, 0.6);
        Point2D p3 = new Point2D(0.4, 0.4);
        Point2D p4 = new Point2D(0.9, 0.9);
        Point2D p5 = new Point2D(0.1, 0.08);

        Point2D p6 = new Point2D(0.15, 0.15);
        Point2D p7 = new Point2D(0.65, 0.65);
        Point2D p8 = new Point2D(0.45, 0.45);
        Point2D p9 = new Point2D(0.95, 0.95);
        Point2D p10 = new Point2D(0.12, 0.12);
        Point2D p11 = new Point2D(0.05, 0.05);

        test.insert(p1);
        test.insert(p2);
        test.insert(p3);
        test.insert(p4);
        test.insert(p5);
        test.insert(p6);
        test.insert(p7);
        test.insert(p8);
        test.insert(p9);
        test.insert(p10);
        test.insert(p11);

        Point2D n1 = test.nearest(new Point2D(0.1, 0.2));
        Point2D n2 = test.nearest(new Point2D(0.8, 0.8));
        Point2D n3 = test.nearest(new Point2D(0.4, 0.4));
        Point2D n4 = test.nearest(new Point2D(0.55, 0.6));
        Point2D n5 = test.nearest(new Point2D(0.07, 0.08));

        System.out.println(n1.x() + " " + n1.y());
        System.out.println(n2.x() + " " + n2.y());
        System.out.println(n3.x() + " " + n3.y());
        System.out.println(n4.x() + " " + n4.y());
        System.out.println(n5.x() + " " + n5.y());
    }
}
