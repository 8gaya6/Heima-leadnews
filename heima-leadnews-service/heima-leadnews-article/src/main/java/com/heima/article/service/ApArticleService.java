package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

/**
 * Description: Todo
 * Class Name: ApArticleService
 * Date: 2023/7/4 22:11
 *
 * @author Hao
 * @version 1.1
 */
public interface ApArticleService extends IService<ApArticle> {

    /**
     * 根据参数加载文章列表
     * @param loadtype 1：加载更多  2：加载最新
     * @param dto
     * @return
     */
    ResponseResult load(Short loadtype, ArticleHomeDto dto);
}
