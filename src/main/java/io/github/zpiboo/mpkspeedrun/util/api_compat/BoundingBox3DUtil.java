package io.github.zpiboo.mpkspeedrun.util.api_compat;

import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.util.misc.XZGeometry;

public class BoundingBox3DUtil {
    public static boolean contains(BoundingBox3D bb, Vector3D vec) {
        return
                bb.minX() <= vec.getX() && vec.getX() <= bb.maxX() &&
                bb.minY() <= vec.getY() && vec.getY() <= bb.maxY() &&
                bb.minZ() <= vec.getZ() && vec.getZ() <= bb.maxZ();
    }

    public static boolean intersect(BoundingBox3D bb1, BoundingBox3D bb2) {
        return
                bb1.maxX() >= bb2.minX() &&
                bb1.minX() <= bb2.maxX() &&
                bb1.maxY() >= bb2.minY() &&
                bb1.minY() <= bb2.maxY() &&
                bb1.maxZ() >= bb2.minZ() &&
                bb1.minZ() <= bb2.maxZ();
    }

    public static double posSubtick(Vector3D start, Vector3D end, BoundingBox3D box) {
        // Start pos is above or below
        if (box.minX() < start.getX() && start.getX() < box.maxX() &&
                box.minZ() < start.getZ() && start.getZ() < box.maxZ() &&
                (start.getY() < box.minY() || box.maxY() < start.getY())) {
            return 0.0;
        }

        return XZGeometry.slabMethod(
                XZGeometry.xzVec(start),
                XZGeometry.xzVec(end),
                XZGeometry.xzBox(box)
        );
    }

    public static double boxSubtick(BoundingBox3D start, BoundingBox3D end, BoundingBox3D box) {
        return posSubtick(
                new Vector3D(start.midX(), start.midY(), start.midZ()),
                new Vector3D(end.midX(), end.midY(), end.midZ()),
                new BoundingBox3D(
                        new Vector3D(start.minX() - 0.3F, start.minY(), start.minZ() - 0.3F),
                        new Vector3D(start.maxX() + 0.3F, start.maxY(), start.maxZ() + 0.3F)
                )
        );
    }
}
