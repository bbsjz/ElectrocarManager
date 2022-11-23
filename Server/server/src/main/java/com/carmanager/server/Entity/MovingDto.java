package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ApiModel("返回给APP当前的位置或移动数据")
public class MovingDto extends Move {

    @ApiModelProperty("是否是位移提醒消息，若不是位移提醒消息，则仅endTime、toLatitude、toLongitude有效，用于表示当前位置")
    boolean alert;

}
