package com.example.electrocarmanager.Utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

public class BDLocUtil {
    /**
     * 将GPS设备采集的原始GPS坐标转换成百度经纬度坐标
     * 即GPS坐标转换为BD09LL坐标
     * @param sourceLatLng
     * @return
     */
    public static LatLng GPStoBD09LL(LatLng sourceLatLng) {
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatLng);
        return converter.convert();
    }
}
