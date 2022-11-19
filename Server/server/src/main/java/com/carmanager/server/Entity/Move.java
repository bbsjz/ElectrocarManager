package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@ApiModel("一条移动信息")
@Entity
public class Move {
    @Id
    @ApiModelProperty("移动信息id")
    String id;

    @ApiModelProperty("移动开始时间")
    Date begin;

    @ApiModelProperty("移动结束时间")
    Date end;

    @ApiModelProperty("移动起始位置")
    String from;

    @ApiModelProperty("移动结束位置")
    String to;

}
