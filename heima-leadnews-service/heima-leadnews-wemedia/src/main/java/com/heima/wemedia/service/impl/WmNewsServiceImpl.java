package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: Todo
 * Class Name: WmNewsService
 * Date: 2023/7/10 16:24
 *
 * @author Hao
 * @version 1.1
 */
@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    /**
     * 查询文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {

        // 1. 检查参数
        if (dto == null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 分页参数检查
        dto.checkParam();
        // 获取当前登录人的信息
        WmUser user = WmThreadLocalUtils.getUser();
        if (user == null) return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 2. 分页条件查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 状态精确查询
        if (dto.getStatus() != null) lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());

        // 频道精确查询
        if (dto.getChannelId() != null) lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());

        // 时间范围查询
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null)
            lambdaQueryWrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());

        // 关键字模糊查询
        if (StringUtils.isNotBlank(dto.getKeyword())) lambdaQueryWrapper.like(WmNews::getTitle, dto.getKeyword());

        // 查询当前登录用户的文章
        lambdaQueryWrapper.eq(WmNews::getUserId, user.getId());

        // 发布时间倒序查询
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);

        page = page(page, lambdaQueryWrapper);

        // 3. 结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }

    /**
     * 发布修改文章或保存为草稿
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        // 0. 条件判断
        if (dto == null || dto.getContent() == null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 1. 保存或修改文章
        WmNews wmNews = new WmNews();
        // BeanUtils.copyProperties：如果属性的名词和类型相同，则进行拷贝
        BeanUtils.copyProperties(dto, wmNews);
        // 封面图片：list → string
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        // 如果当前封面类型为自动：-1，则将类型设为 null
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) wmNews.setType(null);

        saveOrUpdateWmNews(wmNews);

        // 2. 判断是否为草稿：如果为草稿则直接结束当前方法
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

        // 3. 不是草稿，则需要保存文章内容图片与素材的关系
        // 提取文章内容中的图片信息
        List<String> materials = extractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(materials, wmNews.getId());

        // 4. 不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        saveRelativeInfoForCover(dto, wmNews, materials);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * a，如果内容图片数量 [1,3) → 单图 → type 1
     * b，如果内容图片数量 [3, +∞) → 多图 → type 3
     * c，如果内容没有图片 → 无图  → type 0
     *
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {

        List<String> images = dto.getImages();

        // 如果当前封面类型为自动，则设置封面类型的数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            // 多图
            if (materials.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else if (materials.size() >= 1 && materials.size() < 3) {
                // 单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else {
                // 无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            // 修改文章
            if (images != null && images.size() > 0) wmNews.setImages(StringUtils.join(images, ","));
            updateById(wmNews);
        }
        if (images != null && images.size() > 0) saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
    }


    /**
     * 处理文章内容图片与素材的关系
     *
     * @param materials
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存文章图片与素材的关系到数据库中
     *
     * @param materials
     * @param newsId
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if (materials != null && !materials.isEmpty()) {
            // 通过图片的 url 查询素材的 id
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

            // 判断是否有素材失效，并手动抛出异常：既能提示调用者素材失效，又可以进行数据的回滚
            if (dbMaterials == null || dbMaterials.size() != materials.size())
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);

            List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            // 批量保存
            wmNewsMaterialMapper.saveRelations(idList, newsId, type);
        }
    }

    /**
     * 提取文章内容中的图片 url
     *
     * @param content
     * @return
     */
    private List<String> extractUrlInfo(String content) {
        List<String> imgsUrl = new ArrayList<>();

        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String imgUrl = (String) map.get("value");
                imgsUrl.add(imgUrl);
            }
        }
        return imgsUrl;
    }

    /**
     * 保存或修改文章
     *
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        // 补全属性
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1); // 默认上架

        // 保存操作
        if (wmNews.getId() == null) save(wmNews);
        else { // 修改操作，需要删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        }
    }
}
