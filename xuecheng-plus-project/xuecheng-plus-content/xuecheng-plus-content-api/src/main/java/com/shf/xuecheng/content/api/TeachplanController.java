package com.shf.xuecheng.content.api;

import com.shf.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.shf.xuecheng.content.model.dto.SaveTeachplanDto;
import com.shf.xuecheng.content.model.dto.TeachplanDto;
import com.shf.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程计划管理相关的接口
 */
@ApiOperation(value = "课程计划管理相关的接口", tags = "课程计划关系相关接口")
@Slf4j
@RestController
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable long courseId) {
        return teachplanService.findTeachplayTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto) {
        teachplanService.saveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable long id) {
        teachplanService.deleteTeachplan(id);
    }

    @ApiOperation("课程计划下移")
    @PostMapping("/teachplan/movedown/{id}")
    public void moveDownTeachplan(@PathVariable long id) {
        teachplanService.moveDownTeachplan(id);
    }

    @ApiOperation("课程计划上移")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveUpTeachplan(@PathVariable long id) {
        teachplanService.moveUpTeachplan(id);
    }

    @ApiOperation("课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto) {
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }
}
