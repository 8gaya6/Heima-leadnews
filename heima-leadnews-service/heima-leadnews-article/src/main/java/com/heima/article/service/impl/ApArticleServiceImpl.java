package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Description: Todo
 * Class Name: ApArticleServiceImpl
 * Date: 2023/7/4 22:11
 *
 * @author Hao
 * @version 1.1
 */
@Service
@Slf4j
@Transactional
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;

    // Q：明明已经集成了 MyBaitis-Plus，为什么还要再注入一个 Mapper?
    // A：MyBaitis-Plus 对于多表查询并不友好，因此需要自己手动写
    @Autowired
    private ApArticleMapper apArticleMapper;

    /**
     * 根据参数加载文章列表
     *
     * @param loadtype 1：加载更多  2：加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult load(Short loadtype, ArticleHomeDto dto) {
        // 1. 校验参数
        Integer size = dto.getSize();
        if (size == null || size == 0) size = 10;
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        // 1.1 类型参数检验
        if (!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadtype.equals(ArticleConstants.LOADTYPE_LOAD_NEW))
            loadtype = ArticleConstants.LOADTYPE_LOAD_MORE;

        // 1.2 文章频道校验
        if (StringUtils.isEmpty(dto.getTag())) dto.setTag(ArticleConstants.DEFAULT_TAG);

        // 1.3 时间校验
        if (dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if (dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());

        // 2.查询数据
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);

        // 3.结果封装
        ResponseResult responseResult = ResponseResult.okResult(apArticles);
        return responseResult;
    }
}
