package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("一条移动信息")
@Entity
public class Move {
    @Id
    @ApiModelProperty("移动信息id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ApiModelProperty("移动开始时间")
    Date beginTime;

    @ApiModelProperty("移动结束时间")
    Date endTime;

    @ApiModelProperty("移动起始纬度")
    Double fromLatitude;

    @ApiModelProperty("移动起始经度")
    Double fromLongitude;

    @ApiModelProperty("移动结束纬度")
    Double toLatitude;

    @ApiModelProperty("移动结束经度")
    Double toLongitude;

}
