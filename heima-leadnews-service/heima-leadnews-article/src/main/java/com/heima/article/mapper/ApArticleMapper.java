package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description: Todo
 * Class Name: ApArticleMapper
 * Date: 2023/7/4 22:00
 *
 * @author Hao
 * @version 1.1
 */
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    /**
     * Description: 加载文章列表
     * @param dto
     * @param type 1：加载更多 2：加载最新
     * @return
     */
    public List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDto dto, @Param("type") Short type);
}
