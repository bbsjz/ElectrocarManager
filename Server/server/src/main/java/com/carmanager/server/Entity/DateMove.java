package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@ApiModel("移动发生的天数列表")
@Entity
public class DateMove {
    @Id
    @ApiModelProperty("发生移动的日期：天")
    Date date;

    @OneToMany
    @ApiModelProperty("当天内发生的移动信息列表")
    List<Move> list;
}
