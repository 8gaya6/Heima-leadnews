package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description: Todo
 * Class Name: WmNewsMaterialMapper
 * Date: 2023/7/10 17:13
 *
 * @author Hao
 * @version 1.1
 */
@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * MyBatis-Plus 不支持批量保存操作，因此需要自己实现
     * @param materialIds
     * @param newsId
     * @param type
     */
    void saveRelations(@Param("materialIds") List<Integer> materialIds, @Param("newsId") Integer newsId, @Param("type") Short type);
}
