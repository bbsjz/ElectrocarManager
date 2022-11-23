package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel("移动发生的天数列表")
@Entity
public class DateMove {
    @Id
    @ApiModelProperty("发生移动的日期：天")
    String date;

    @OneToMany(fetch = FetchType.EAGER ,cascade = CascadeType.ALL)
    @ApiModelProperty("当天内发生的移动信息列表")
    List<Move> list=new ArrayList<>();
}
