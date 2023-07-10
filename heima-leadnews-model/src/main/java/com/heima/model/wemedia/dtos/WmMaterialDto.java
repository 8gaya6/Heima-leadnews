package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * Description: Todo
 * Class Name: WmMaterialDto
 * Date: 2023/7/10 14:59
 *
 * @author Hao
 * @version 1.1
 */
@Data
public class WmMaterialDto extends PageRequestDto {

    /**
     * 1 收藏
     * 0 未收藏
     */
    private Short isCollection;
}
