package com.heima.model.user.dtos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: Todo
 * Class Name: LoginDto
 * Date: 2023/7/3 10:52
 *
 * @author Hao
 * @version 1.1
 */

@Data
public class LoginDto {

    // 手机号
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    // 密码
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
