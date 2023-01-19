package com.shf.xuecheng.content.service.impl;

import com.shf.xuecheng.content.mapper.CourseCategoryMapper;
import com.shf.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.shf.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * 课程分类查询
     * @param id
     * @return
     */
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
//        得到了根节点下边的所有子节点
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

//        定义一个List作为最终返回节点
        ArrayList<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();

//        为了方便找子节点的父节点，定义一个map
        HashMap<String, CourseCategoryTreeDto> nodeMap = new HashMap<>();

//        将数据封装到List中，只有包括了根节点的直接下属节点
        courseCategoryTreeDtos.stream().forEach(item->{
            nodeMap.put(item.getId(), item);
            if (item.getParentid().equals(id)) {
                categoryTreeDtos.add(item);
            }

//            找到该节点的父节点
            String parentid = item.getParentid();
//            找到该节点的父节点的父节点对象
            CourseCategoryTreeDto parentNode = nodeMap.get(parentid);
            if (parentNode != null) {
                List<CourseCategoryTreeDto> childrenTreeNodes = parentNode.getChildrenTreeNodes();
                if (childrenTreeNodes == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
//              找到子节点，放到它的父节点的childrenTreeNodes属性中
                parentNode.getChildrenTreeNodes().add(item);
            }
        });

//        返回list中只包括了根节点的直接下属节点
        return categoryTreeDtos;
    }
}
