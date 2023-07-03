package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;

/**
 * Description: Todo
 * Class Name: ApUserService
 * Date: 2023/7/3 10:59
 *
 * @author Hao
 * @version 1.1
 */
public interface ApUserService extends IService<ApUser> {

    public ResponseResult login(LoginDto loginDto);
}
