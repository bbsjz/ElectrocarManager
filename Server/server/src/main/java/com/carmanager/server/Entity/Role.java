package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("角色")
@Entity
public class Role {

    @Id
    @ApiModelProperty("角色名")
    String name;

    @ApiModelProperty("角色权限")
    String authority;

    @ApiModelProperty("角色对应的用户")
    @ManyToMany(mappedBy = "roles")
    List<User> users;

}
