package com.shf.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shf.xuecheng.media.model.po.MediaFiles;
import com.shf.xuecheng.media.model.po.MediaProcess;
import com.shf.xuecheng.media.model.po.MediaProcessHistory;
import com.shf.xuecheng.media.service.MediaFileProcessService;
import com.shf.xuecheng.media.mapper.MediaFilesMapper;
import com.shf.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.shf.xuecheng.media.mapper.MediaProcessMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/10/16 15:11
 */
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    private MediaProcessMapper mediaProcessMapper;
    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    @Transactional
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {

        //查询这个任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess==null){
            log.debug("更新任务状态时此任务:{}为空",taskId);
            return ;
        }

        // 判断任务成功还是失败
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<>();
        queryWrapperById.eq(MediaProcess::getId, taskId);
        if("3".equals(status)){
            //任务失败,更新任务失败原因
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");//处理失败
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcess_u.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.update(mediaProcess_u,queryWrapperById);
            return ;
        }
        //处理成功，更新状态
        if("2".equals(status)){
            mediaProcess.setStatus("2");
            mediaProcess.setUrl(url);
            mediaProcess.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.updateById(mediaProcess);
            //更新文件表中的url
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }

        //如果处理成功将任务添加到历史记录表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);

        //如果处理成功将待处理表的记录删除
        mediaProcessMapper.deleteById(taskId);
    }
}
