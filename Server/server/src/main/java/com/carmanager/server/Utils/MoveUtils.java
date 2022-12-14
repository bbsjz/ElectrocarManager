package com.carmanager.server.Utils;

import com.carmanager.server.Entity.Point;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;

@Component
public class MoveUtils {

    private boolean moving = false; //当前是否在移动状态

    private Point beginMovingPoint; //开始移动的位置坐标

    private Point lastMovingPoint; //最后一次移动的位置坐标

    private Point lastPoint; //上一次移动的位置坐标

    private double distance; //移动总长度

    private boolean needToRecord; //这条移动记录是否可见

    private boolean stopAndShouldBeStore;//判断已经停止，且这条记录应该被保存

    private final int buffCapacity = 5; //缓冲区长度

    final int movingThreshold = 2; // 明显移动的阈值

    final int timeThreshold = 60 * 1000; // 最长不移动时间，以ms记

    /**
     * 缓冲区中出现了多少次移动
     */
    private int moveCount = 0;

    /**
     * 对数据进行滤波的缓冲区
     */
    private final ArrayBlockingQueue<Boolean> buff = new ArrayBlockingQueue<>(buffCapacity);

    /**
     * 判断两点之间是否发生明显移动
     *
     * @param p1 前一个点
     * @param p2 后一个点
     * @return 是否明显移动
     */
    private boolean pointMoving(Point p1, Point p2) {
        // 各参数间的权重
        double latitudeArg = 800;
        double longitudeArg = 800;
        double accXArg = 5;
        double accYArg = 5;
        double accZArg = 5;
        double angularXArg = 3;
        double angularYArg = 3;
        double angularZArg = 3;
        double latitudeDiff = Math.abs(p1.getLatitude() - p2.getLatitude()) * latitudeArg;
        double longitudeDiff = Math.abs(p1.getLongitude() - p2.getLongitude()) * longitudeArg;
        double accDiffX = Math.abs(p1.getAccelerationX() - p2.getAccelerationX()) * accXArg;
        double accDiffY = Math.abs(p1.getAccelerationY() - p2.getAccelerationY()) * accYArg;
        double accDiffZ = Math.abs(p1.getAccelerationZ() - p2.getAccelerationZ()) * accZArg;
        double angularDiffX = Math.abs(p2.getAngularVelocityX()) * angularXArg;
        double angularDiffY = Math.abs(p2.getAngularVelocityY()) * angularYArg;
        double angularDiffZ = Math.abs(p2.getAngularVelocityZ()) * angularZArg;
        double score = latitudeDiff + longitudeDiff +
                accDiffX + accDiffY + accDiffZ +
                angularDiffX + angularDiffY + angularDiffZ;
        return score > 1;
    }

    /**
     * 经过滤波后得出是否正在移动
     *
     * @return 是否正在移动
     */
    public boolean isMoving() {
        return moving;
    }


    /**
     * 得到开始移动的点
     *
     * @return 开始移动的点
     */
    public Point getBeginMovingPoint() {
        return beginMovingPoint;
    }

    /**
     * 清除是否需要保存的标志位
     */
    public void clearStopAndShouldBeStore() {
        stopAndShouldBeStore = false;
        moving = false;
        beginMovingPoint = null;
    }

    /**
     * 返回是否需要保存本条记录
     *
     * @return 是否需要保存本条记录
     */
    public boolean getStopAndNeedToStore() {
        return stopAndShouldBeStore;
    }

    /**
     * 获取移动总距离, 不在移动则返回0
     *
     * @return 移动总距离
     */
    public double getDistance() {
        return distance;
    }

    /**
     * 获取本记录是否应该可见
     * 没有移动默认为false
     *
     * @return 可见
     */
    public boolean isNeedToRecord() {
        return needToRecord;
    }

    /**
     * 设置本记录是否应该可见
     * 在一段移动记录中假如出现一个人在某一个时刻开启了移动提醒
     * 则整段记录都应该可见
     *
     * @param open 某时刻是否有人开启移动提醒
     */
    public void updateRecordStatus(boolean open) {
        if (moving && open) {
            needToRecord = true;
        }
    }

    /**
     * 添加新收到的点，更新移动状态，开启同步防止线程不安全操作
     *
     * @param point 新收到的点
     */
    public synchronized void addPoint(Point point) {
        if (lastPoint == null) {
            lastPoint = point;
            beginMovingPoint = null;
            lastMovingPoint = point;
            moving = false;
            distance = 0;
            return;
        }

        // 当GPS无信号时假设车辆经纬度不发生改变
        if (point.getLatitude() == 0 && point.getLongitude() == 0) {
            point.setLatitude(lastPoint.getLatitude());
            point.setLatitude(lastPoint.getLongitude());
        }

        boolean nowMoving = pointMoving(lastPoint, point);

        // 进行滤波，只有最近一段时间移动次数超过阈值才会被认为是明显移动
        if (buff.size() >= buffCapacity) {
            moveCount -= buff.peek() ? 1 : 0;
            buff.remove();
        }
        moveCount += nowMoving ? 1 : 0;
        buff.add(nowMoving);

        // 上一次明显移动的点
        if (nowMoving && moveCount >= movingThreshold) {
            lastMovingPoint = point;
        }
        long notMovingTime = point.getCreateTime().getTime() -
                lastMovingPoint.getCreateTime().getTime();

        if (!moving && moveCount >= movingThreshold) {
            // 上一个时刻不在移动状态 and 近几次发生移动次数高于阈值
            // 判断为开始移动
            moving = true;
            beginMovingPoint = point;
        } else if (moving && notMovingTime >= timeThreshold) {
            // 上一个时刻在移动状态 and 距离最后一次明显移动的时长超过阈值
            // 判断为结束移动
            if (needToRecord) {
                stopAndShouldBeStore = true;
            }
            needToRecord = false;
        }

        // 更新距离
        if (moving) {
            distance += CoordinateUtils.getDistance(lastPoint, point);
        } else {
            distance = 0;
        }

        lastPoint = point;
    }
}
