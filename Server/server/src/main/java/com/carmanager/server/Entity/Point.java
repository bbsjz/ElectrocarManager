package com.carmanager.server.Entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@ToString
@ApiModel("一条来自硬件发送的坐标消息")
@Entity
public class Point {

    @Id
    @ApiModelProperty("消息id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ApiModelProperty("消息生成时间")
    Date createTime;

    @ApiModelProperty("纬度")
    double latitude;

    @ApiModelProperty("经度")
    double longitude;

    @ApiModelProperty("X轴方向加速度")
    double accelerationX;

    @ApiModelProperty("Y轴方向加速度")
    double accelerationY;

    @ApiModelProperty("Z轴方向加速度")
    double accelerationZ;

    @ApiModelProperty("围绕X轴旋转的角速度")
    double angularVelocityX;

    @ApiModelProperty("围绕Y轴旋转的角速度")
    double angularVelocityY;

    @ApiModelProperty("围绕Z轴旋转的角速度")
    double angularVelocityZ;

}
