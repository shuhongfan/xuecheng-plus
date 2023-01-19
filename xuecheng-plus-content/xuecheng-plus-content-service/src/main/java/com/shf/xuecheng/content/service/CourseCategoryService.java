package com.shf.xuecheng.content.service;

import com.shf.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * 课程分类相关service
 */
public interface CourseCategoryService {

    /**
     * 课程分类查询
     * @param id
     * @return
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
