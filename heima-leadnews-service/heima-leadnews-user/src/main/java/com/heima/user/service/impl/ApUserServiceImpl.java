package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: Todo
 * Class Name: ApUserServiceImpl
 * Date: 2023/7/3 11:01
 *
 * @author Hao
 * @version 1.1
 */
@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    /**
     * App 端登录功能
     *
     * @param loginDto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto loginDto) {
        String phone = loginDto.getPhone();
        String password = loginDto.getPassword();

        // 1. 用户登录：获得输入的手机号和密码
        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(password)) {
            // 1.1 根据手机号查询用户的信息
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, phone));
            if (dbUser == null) return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户信息不存在！");

            // 1.2 比对密码
            String salt = dbUser.getSalt();
            String encryptedPass = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!encryptedPass.equals(dbUser.getPassword()))
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "密码有误！");

            // 1.3 返回数据 jwt
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("data", dbUser);
            return ResponseResult.okResult(map);
        } else {
            // 2. 游客登录
            String token = AppJwtUtil.getToken(0l);
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            return ResponseResult.okResult(map);
        }
    }
}
