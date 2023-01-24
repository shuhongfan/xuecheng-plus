package com.shf.xuecheng.media.service.jobhandler;

import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.base.utils.Mp4VideoUtil;
import com.shf.xuecheng.media.model.po.MediaProcess;
import com.shf.xuecheng.media.service.MediaFileProcessService;
import com.shf.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class VideoTask {


    @Autowired
    private MediaFileProcessService mediaFileProcessService;

    @Autowired
    private MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        // 分片序号，从0开始
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();

        //查询待处理任务,一次处理的任务数和cpu核心数一样
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, 2);

        if(mediaProcessList==null || mediaProcessList.size()<=0){
            log.debug("查询到的待处理视频任务为0");
            return ;
        }
        //要处理的任务数
        int size = mediaProcessList.size();

        //创建size个线程数量的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);

        //遍历mediaProcessList，将任务放入线程池
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(()->{
                //视频处理状态
                String status = mediaProcess.getStatus();
                //保证幂等性
                if("2".equals(status)){
                    log.debug("视频已经处理不用再次处理,视频信息:{}",mediaProcess);
                    countDownLatch.countDown();//计数器减1
                    return ;
                }
                //桶
                String bucket = mediaProcess.getBucket();
                //存储路径
                String filePath = mediaProcess.getFilePath();
                //原始视频的md5值
                String fileId = mediaProcess.getFileId();
                //原始文件名称
                String filename = mediaProcess.getFilename();

                //将要处理的文件下载到服务器上
                File originalFile = null;
                //处理结束的视频文件
                File mp4File = null;

                try {
                    originalFile = File.createTempFile("original", null);
                    mp4File = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    log.error("处理视频前创建临时文件失败");
                    countDownLatch.countDown();//计数器减1
                    return;
                }
                try {
                    //将原始视频下载到本地
                    mediaFileService.downloadFileFromMinIO(originalFile,bucket,filePath);
                } catch (Exception e) {
                    log.error("下载源始文件过程出错:{},文件信息:{}",e.getMessage(),mediaProcess);
                    countDownLatch.countDown();//计数器减1
                    return ;
                }

                //调用工具类将avi转成mp4

                //转换后mp4文件的名称
                String mp4_name = fileId+".mp4";
                //转换后mp4文件的路径
                String mp4_path = mp4File.getAbsolutePath();
                //创建工具类对象
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath,originalFile.getAbsolutePath(),mp4_name,mp4_path);
                //开始视频转换，成功将返回success,失败返回失败原因
                String result = videoUtil.generateMp4();
                String statusNew ="3";
                String url = null;//最终访问路径
                if("success".equals(result)){
                    //转换成功
                    //上传到minio的路径
                    String objectName = getFilePath(fileId, ".mp4");
                    try {
                        //上传到minIO
                        mediaFileService.addMediaFilesToMinIO(mp4_path,bucket,objectName);
                    } catch (Exception e) {
                        log.debug("上传文件出错:{}",e.getMessage());
                        countDownLatch.countDown();//计数器减1
                        return ;
                    }
                    statusNew = "2";//处理成功
                    url = "/"+bucket+"/"+objectName;

                }


                try {
                    //记录任务处理结果
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(),statusNew,fileId,url,result);
                } catch (Exception e) {
                    log.debug("保存任务处理结果出错:{}",e.getMessage());
                    countDownLatch.countDown();//计数器减1
                    return ;
                }

                //计数器减去1
                countDownLatch.countDown();
            });
        });

        //阻塞到任务执行完成,当countDownLatch计数器归零，这里的阻塞解除
        //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);

    }

    /**
     * 通过MD5值生成路径
     * @param fileMd5
     * @param fileExt
     * @return
     */
    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    @XxlJob("MyVideoJobHandler")
    public void MyVideoJobHandler() throws InterruptedException {
//        分片需要，从0开始
        int shardIndex = XxlJobHelper.getShardIndex();
//        总分片数
        int shardTotal = XxlJobHelper.getShardTotal();

//        查询待处理任务,一次处理的任务和CPU核心数一样
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, 2);

        if (mediaProcessList == null || mediaProcessList.size() <= 0) {
            log.debug("查询到的待处理视频任务为0");
            return;
        }

//        要处理的任务数
        int size = mediaProcessList.size();

//        创建size个线程数量的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);

//        计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);

        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(()->{
//                任务的执行逻辑
                String status = mediaProcess.getStatus();
                if ("2".equals(status)) {
                    log.debug("视频已经处理不用再次处理，视频信息：{}", mediaProcess);
                    countDownLatch.countDown();
                    return;
                }

                String bucket = mediaProcess.getBucket();// 桶
                String filePath = mediaProcess.getFilePath();// 存储路径
                String fileId = mediaProcess.getFileId(); // 原始视频的md5值
                String filename = mediaProcess.getFilename(); // 源文件名称
                File originalFile=null; //将要处理的文件下载到服务器上
                File mp4File=null; // 处理结束的视频文件
                try {
                    originalFile = File.createTempFile("original", null);
                    mp4File = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    log.error("处理视频前创建临时文件失败");
                    countDownLatch.countDown();
                    XueChengPlusException.cast("处理视频前创建临时文件失败");
                }

                try {
                    mediaFileService.downloadFileFromMinIO(originalFile, bucket, filePath);
                } catch (Exception e) {
                    countDownLatch.countDown();
                    log.error("下载源始文件过程出错:{},文件信息:{}",e.getMessage(),mediaProcess);
                    XueChengPlusException.cast("下载源始文件过程出错:"+e.getMessage()+",文件信息:"+mediaProcess);
                }

                //转换后mp4文件的名称
                String mp4_name = fileId+".mp4";
                //转换后mp4文件的路径
                String mp4_path = mp4File.getAbsolutePath();
                //创建工具类对象
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath,originalFile.getAbsolutePath(),mp4_name,mp4_path);
//                开始视频转换，成功将返回success
                String result = videoUtil.generateMp4();
                String statusNew = "3"; // 处理成功状态
                String url = null; // 最终访问路径
                if ("success".equals(result)) {
//                    上传到minio的路径
                    String objectName = getFilePath(fileId, ".mp4");
                    try {
                        mediaFileService.addMediaFilesToMinIO(mp4_path, bucket, objectName);
                    } catch (Exception e) {
                        countDownLatch.countDown();
                        log.error("文件上传出错",e.getMessage());
                        XueChengPlusException.cast("文件上传出错"+e.getMessage());
                    }
                    statusNew = "2"; // 处理成功状态
                    url = "/" + bucket + "/" + objectName;
                }

//                记录任务处理结果
                try {
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(),statusNew,fileId,url,result);
                } catch (Exception e) {
                    countDownLatch.countDown();
                    log.error("保存任务处理结果出错：{}",e.getMessage());
                    XueChengPlusException.cast("保存任务处理结果出错:"+e.getMessage());
                }

//                计数器减1
                countDownLatch.countDown();
            });
        });

        //阻塞到任务执行完成,当countDownLatch计数器归零，这里的阻塞解除
        //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);
    }
}
