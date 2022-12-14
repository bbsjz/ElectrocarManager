package com.carmanager.server.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ApiModel("用户")
@Proxy(lazy = false)
@Entity
public class User {

    @Id
    @ApiModelProperty("用户名")
    String name;

    @ApiModelProperty("密码")
    String password;

    @ApiModelProperty("用户角色")
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_name"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    List<Role> roles;

}
