package com.heima.model.article.dtos;

import lombok.Data;
import java.util.Date;

/**
 * Description: Todo
 * Class Name: ArticleHomeDto
 * Date: 2023/7/4 21:48
 *
 * @author Hao
 * @version 1.1
 */
@Data
public class ArticleHomeDto {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    // 分页 size
    Integer size;
    // 频道 ID
    String tag;
}