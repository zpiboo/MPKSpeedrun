package io.github.zpiboo.mpkspeedrun.util.misc;

import io.github.kurrycat.mpkmod.util.BoundingBox2D;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.kurrycat.mpkmod.util.Vector3D;

public class XZGeometry {
    public static Vector2D xzVec(Vector3D vec) {
        return new Vector2D(vec.getX(), vec.getZ());
    }

    public static BoundingBox2D xzBox(BoundingBox3D box) {
        return new BoundingBox2D(
                xzVec(box.getMin()),
                xzVec(box.getMax())
        );
    }

    public static double slabMethod(Vector2D start, Vector2D end, BoundingBox2D box) {
        double[] startArr = new double[2];
        double[] endArr = new double[2];
        double[] direction = new double[2];
        double[] boxMin = new double[2];
        double[] boxMax = new double[2];

        startArr[0] = start.getX();
        startArr[1] = start.getY();
        endArr[0] = end.getX();
        endArr[1] = end.getY();

        boxMin[0] = box.minX();
        boxMin[1] = box.minY();
        boxMax[0] = box.maxX();
        boxMax[1] = box.maxY();

        for (int i = 0; i < 2; i++) {
            direction[i] = endArr[i] - startArr[i];
        }

        double tMin = 0.0;
        double tMax = 1.0;

        for (int i = 0; i < 2; i++) {
            double boxMinCoord = boxMin[i];
            double boxMaxCoord = boxMax[i];

            if (Math.abs(direction[i]) == 0) {
                // Line is parallel to edge
                if (startArr[i] < boxMinCoord || startArr[i] > boxMaxCoord) {
                    return 1.0;
                }
            } else {
                double t1 = (boxMinCoord - startArr[i]) / direction[i];
                double t2 = (boxMaxCoord - startArr[i]) / direction[i];

                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);

                if (tMin > tMax) { return 1.0; }
            }
        }

        if (tMax < 0 || tMin > 1) {
            return 1.0;
        }

        return Math.max(tMin, 0.0);
    }
}
