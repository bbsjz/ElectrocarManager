package com.carmanager.server.Utils;

import com.carmanager.server.Entity.Point;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MoveUtils {

    private boolean moving = false;

    private Point beginMovingPoint;

    private Point lastMovingPoint;

    private Point lastPoint;

    private final int buffCapacity = 5;

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
     * @param p1 前一个点
     * @param p2 后一个点
     * @return 是否明显移动
     */
    private boolean pointMoving(Point p1, Point p2) {
        // 各参数间的权重
        double latitudeArg = 10;
        double longitudeArg = 10;
        double accXArg = 1;
        double accYArg = 1;
        double accZArg = 1;
        double angularXArg = 1;
        double angularYArg = 1;
        double angularZArg = 1;
        double latitudeDiff = Math.abs(p1.getLatitude() - p2.getLatitude()) * latitudeArg;
        double longitudeDiff = Math.abs(p1.getLongitude() - p2.getLongitude()) * longitudeArg;
        double accDiffX = Math.abs(p2.getAccelerationX()) * accXArg;
        double accDiffY = Math.abs(p2.getAccelerationY()) * accYArg;
        double accDiffZ = Math.abs(p2.getAccelerationZ()) * accZArg;
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
     * @return 是否正在移动
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * 得到开始移动的点
     * @return 开始移动的点
     */
    public Point getBeginMovingPoint() {
        return beginMovingPoint;
    }

    /**
     * 添加新收到的点，更新移动状态，开启同步防止线程不安全操作
     * @param point 新收到的点
     */
    public synchronized void addPoint(Point point) {
        if (lastPoint == null) {
            lastPoint = point;
            beginMovingPoint = null;
            lastMovingPoint = null;
            moving = false;
            return;
        }

        boolean nowMoving = pointMoving(lastPoint, point);

        // 进行滤波，只有最近一段时间移动次数超过阈值才会被认为是明显移动
        if (buff.size() >= buffCapacity) {
            moveCount -= buff.peek() ? 1 : 0;
            buff.remove();
        }
        moveCount += nowMoving ? 1 : 0;
        buff.add(nowMoving);

        // 明显移动的阈值
        int movingThreshold = 3;
        // 最长不移动时间，以ms记
        int timeThreshold = 60 * 1000;

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
            moving = false;
            beginMovingPoint = null;
        }
    }
}
