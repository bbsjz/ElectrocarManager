package com.carmanager.server.Utils;

import com.carmanager.server.Entity.Point;
import org.springframework.stereotype.Component;

@Component
public class CoordinateUtils {

    // 赤道半径
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * 计算弧度角
     * @param degree 度数
     * @return 弧度角
     */
    private static double rad(final double degree) {
        return degree * Math.PI / 180.0;
    }

    /**
     * 计算两个坐标相距距离 ( 单位: 米 )
     * <pre>
     *     计算点与点直线间距离
     * </pre>
     * @param originPoint 起点
     * @param targetPoint 目标点
     * @return 两个坐标相距距离 ( 单位: 米 )
     */
    public static double getDistance(Point originPoint, Point targetPoint) {
        final double originLng = originPoint.getLongitude();
        final double originLat = originPoint.getLatitude();
        final double targetLng = targetPoint.getLongitude();
        final double targetLat = targetPoint.getLatitude();
        double radLat1 = rad(originLat);
        double radLat2 = rad(targetLat);
        double a       = radLat1 - radLat2;
        double b       = rad(originLng) - rad(targetLng);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        // 保留两位小数
        s = Math.round(s * 100D) / 100D;
        s = s * 1000;
        return s;
    }

}
