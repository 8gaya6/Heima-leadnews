package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description: Todo
 * Class Name: WmMaterialService
 * Date: 2023/7/10 13:50
 *
 * @author Hao
 * @version 1.1
 */
public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 素材列表查询
     * @param dto
     * @return
     */
    public ResponseResult findList(WmMaterialDto dto);

}
