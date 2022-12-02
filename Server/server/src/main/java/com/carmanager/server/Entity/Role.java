package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Getter
@Setter
@ApiModel("角色")
@Entity
public class Role {

    @Id
    @ApiModelProperty("角色名")
    String name;

    @ApiModelProperty("角色权限")
    @Convert(converter = RoleConverter.class)
    List<String> authorities;

    @ApiModelProperty("角色对应的用户")
    @ManyToMany(mappedBy = "roles")
    List<User> users;

}
