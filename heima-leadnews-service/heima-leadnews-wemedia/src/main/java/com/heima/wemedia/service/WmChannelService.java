package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * Description: Todo
 * Class Name: WmChannelService
 * Date: 2023/7/10 15:42
 *
 * @author Hao
 * @version 1.1
 */
public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有频道
     *
     * @return
     */
    public ResponseResult findAll();

}
