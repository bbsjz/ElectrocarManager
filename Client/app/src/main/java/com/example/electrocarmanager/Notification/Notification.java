package com.example.electrocarmanager.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author bbg
 * 一条移动提醒记录的实体
 */

@AllArgsConstructor
@Data
@Getter
public class Notification {
    public String time;// 发生移动的时间
    public String from;// 位移开始的位置
    public String to;// 位移结束的位置
    public double distance;// 位移移动的距离
    public double last;// 位移持续的时间
}
