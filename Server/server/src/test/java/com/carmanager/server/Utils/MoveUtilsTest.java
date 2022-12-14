package com.carmanager.server.Utils;

import com.carmanager.server.Entity.Point;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MoveUtilsTest {

    MoveUtils utils = new MoveUtils();

    @Test
    public void addPointTest() {
        Point[] points = new Point[10];
        points[0] = new Point(null, new Date(10000), 0, 0, 0.315674, 0.467529, 0.891846, 0.1435, -0.4535, 0.047);
        points[1] = new Point(null, new Date(13000), 0, 0, 0.037231, 0.070801, 0.93335, 1.4535, -0.517, -0.229);
        points[2] = new Point(null, new Date(16000), 0, 0, -0.280273, -0.466553, 0.863525, 0.203, 0.4645, -0.063);
        points[3] = new Point(null, new Date(19000), 0, 0, -0.299194, -0.553955, 0.804565, 0.567, 0.0335, -0.115);
        points[4] = new Point(null, new Date(22000), 0, 0, 0.377808, 0.112549, 0.967773, -0.9795, 0.1335, 0.1325);
        points[5] = new Point(null, new Date(25000), 0, 0, -0.058105, -0.384888, 0.928711, 0.1415, -0.4785, -0.0295);
        points[6] = new Point(null, new Date(28000), 0, 0, 0.090576, -0.363159, 1.006592, 0.321, -0.5855, -0.056);
        points[7] = new Point(null, new Date(31000), 0, 0, 0.284302, -0.201416, 0.972046, -0.5205, 0.551, 0.1025);
        points[8] = new Point(null, new Date(34000), 0, 0, -0.051514, -0.522583, 0.963135, 0.035, 0.6215, -0.101);
        points[9] = new Point(null, new Date(37000), 0, 0, -0.088379, -0.501709, 0.935669, -0.0765, 0.895, -0.139);

        for (int i = 0; i < 10; ++i) {
            utils.addPoint(points[i]);
            if (i < 2) {
                assertFalse(utils.isMoving());
            } else {
                assertTrue(utils.isMoving());
            }
        }
    }

}