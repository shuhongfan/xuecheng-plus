package com.shf.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.base.model.PageParams;
import com.shf.xuecheng.base.model.PageResult;
import com.shf.xuecheng.base.model.RestResponse;
import com.shf.xuecheng.media.mapper.MediaFilesMapper;
import com.shf.xuecheng.media.mapper.MediaProcessMapper;
import com.shf.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.shf.xuecheng.media.model.dto.UploadFileParamsDto;
import com.shf.xuecheng.media.model.dto.UploadFileResultDto;
import com.shf.xuecheng.media.model.po.MediaFiles;
import com.shf.xuecheng.media.model.po.MediaProcess;
import com.shf.xuecheng.media.service.MediaFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    private MinioClient minioClient;

    //普通文件存储的桶
    @Value("${minio.bucket.files}")
    private String bucket_files;
    //视频文件存储的桶
    @Value("${minio.bucket.videofiles}")
    private String bucket_videofiles;

    /**
     * 注入自己 --------->  代理对象
     */
    @Autowired
    private MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    /**
     * 目前是在uploadFile方法上添加@Transactional，当调用uploadFile方法前会开启数据库事务，
     * 如果上传文件时间较长那么数据库的事务持续时间就会变长，这样数据库连接释放就慢，最终导致数据库连接不够用。
     * 所以我们只讲addMeadiFilesToDb方法添加事务控制即可,uploadFile方法上的@Transactional注解去掉
     * @description 上传文件的通用接口
     * @param companyId  机构id
     * @param uploadFileParamsDto  文件信息
     * @param bytes  文件字节数组
     * @param folder 桶下边的子目录
     * @param objectName 对象名称
     * @return com.xuecheng.media.model.dto.UploadFileResultDto
     * @author Mr.M
     * @date 2022/10/13 15:51
     */
//    @Transactional
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {

        //得到文件的md5值
        String fileMd5 = DigestUtils.md5Hex(bytes);

        if (StringUtils.isEmpty(folder)) {
            //自动生成目录的路径 按年月日生成，
            folder = getFileFolder(new Date(), true, true, true);
        } else if (folder.indexOf("/") < 0) {
            folder = folder + "/";
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();

        if (StringUtils.isEmpty(objectName)) {
            //如果objectName为空，使用文件的md5值为objectName
            objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
        }

        objectName = folder + objectName;

        try {
//            将文件上传到分布式文件系统
            addMediaFilesToMinIO(bytes, bucket_files, objectName);
//            将文件信息入库  通过代理对象调用
            MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(
                    companyId,
                    fileMd5,
                    uploadFileParamsDto,
                    bucket_files,
                    objectName);
            //准备返回数据
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            log.debug("上传文件失败：{}", e.getMessage());
            XueChengPlusException.cast(e.getMessage());
        }
        return null;
    }

    public UploadFileResultDto uploadFileOld(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {
        //得到文件的md5值
        String fileMd5 = DigestUtils.md5Hex(bytes);

        if(StringUtils.isEmpty(folder)){
            //自动生成目录的路径 按年月日生成，
            folder = getFileFolder(new Date(), true, true, true);
        }else if(folder.indexOf("/")<0){
            folder = folder+"/";
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();

        if(StringUtils.isEmpty(objectName)){
            //如果objectName为空，使用文件的md5值为objectName
            objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
        }

        objectName = folder + objectName;

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            String contentType = uploadFileParamsDto.getContentType();

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket_files)
                    .object(objectName)
                    //InputStream stream, long objectSize 对象大小, long partSize 分片大小(-1表示5M,最大不要超过5T，最多10000)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            //上传到minio
            minioClient.putObject(putObjectArgs);

            //保存到数据库
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
            if(mediaFiles == null){
                mediaFiles = new MediaFiles();

                //封装数据
                BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);
                mediaFiles.setId(fileMd5);
                mediaFiles.setFileId(fileMd5);
                mediaFiles.setCompanyId(companyId);
                mediaFiles.setFilename(filename);
                mediaFiles.setBucket(bucket_files);
                mediaFiles.setFilePath(objectName);
                mediaFiles.setUrl("/"+bucket_files+"/"+objectName);
                mediaFiles.setCreateDate(LocalDateTime.now());
                mediaFiles.setStatus("1");
                mediaFiles.setAuditStatus("002003");

                //插入文件表
                mediaFilesMapper.insert(mediaFiles);
            }
            //准备返回数据
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            log.debug("上传文件失败：{}",e.getMessage());
            XueChengPlusException.cast(e.getMessage());
        }
        return null;
    }

    /**
     * 如果是在uploadFile方法上添加@Transactional注解就可以控制事务，去掉则不行。
     * 现在的问题其实是一个非事务方法调同类一个事务方法，事务无法控制，这是为什么？
     *
     * 如果在uploadFile方法上添加@Transactional注解，代理对象执行此方法前会开启事务，
     * 如果在uploadFile方法上没有@Transactional注解，代理对象执行此方法前不进行事务控制。
     * 所以判断该方法是否可以事务控制必须保证是通过代理对象调用此方法，且此方法上添加了@Transactional注解。
     *
     * 现在在addMediaFilesToDb方法上添加@Transactional注解，也不会进行事务控制是因为并不是通过代理对象执行的addMediaFilesToDb方法。
     * 为了判断在uploadFile方法中去调用addMediaFilesToDb方法是否是通过代理对象去调用，我们可以打断点跟踪。
     * 我们发现在uploadFile方法中去调用addMediaFilesToDb方法不是通过代理对象去调用。
     *
     * 如何解决呢？通过代理对象去调用addMediaFilesToDb方法即可解决。
     * 在MediaFileService的实现类中注入MediaFileService的代理对象，通过代理对象调用方法
     *
     * 判断能否回滚，是否被spring事务控制
     * 1. 是不是通过代理对象调用
     * 2. 方法上是否有@Transactional
     * @param companyId
     * @param fileId
     * @param uploadFileParamsDto
     * @param bucket
     * @param objectName
     * @return com.xuecheng.media.model.po.MediaFiles
     * @description 将文件信息入库
     * @author Mr.M
     * @date 2022/10/14 9:14
     */
    @Transactional(rollbackFor = {Exception.class})
    public MediaFiles addMediaFilesToDb(Long companyId, String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //保存到数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();

            //封装数据
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);

            //获取扩展名
            String extension = null;
            String filename = uploadFileParamsDto.getFilename();
            if(StringUtils.isNotEmpty(filename) && filename.indexOf(".")>=0){
                extension = filename.substring(filename.lastIndexOf("."));
            }
            //媒体类型
            String mimeType = getMimeTypeByextension(extension);
            //图片、mp4视频可以设置URL
            if(mimeType.indexOf("image")>=0 || mimeType.indexOf("mp4")>=0){
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }

            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");

            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                XueChengPlusException.cast("保存文件信息失败");
            }

//            TODO 人为制造异常
//            int i = 1 / 0;

            //对avi视频添加到待处理任务表
            if(mimeType.equals("video/x-msvideo")){
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles,mediaProcess);
                //设置一个状态
                mediaProcess.setStatus("1");//未处理
                mediaProcessMapper.insert(mediaProcess);
            }
        }
        return mediaFiles;
    }

    /**
     * 检查文件是否存在
     * @param fileMd5 文件的md5
     * @return
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {

        //在文件表存在，并且在文件系统存在，此文件才存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles==null){
            return RestResponse.success(false);
        }
        //查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(mediaFiles.getBucket())
                .object(mediaFiles.getFilePath())
                .build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if(inputStream==null){
                //文件不存在
                return RestResponse.success(false);
            }
        }catch (Exception e){
             //文件不存在
            return RestResponse.success(false);
        }
        //文件已存在
        return RestResponse.success(true);
    }

    /**
     * 检查分块是否存在
     * video/3/a/3a5a861d1c745d05166132c47b44f9e4
     * @param fileMd5  文件的md5
     * @param chunkIndex  分块序号
     * @return
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
//        数据库不存在
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        //查询文件系统分块文件是否存在
        //查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket_videofiles)
                .object(chunkFilePath)
                .build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if(inputStream==null){
                //文件不存在
                return RestResponse.success(false);
            }
        }catch (Exception e){
            //文件不存在
            return RestResponse.success(false);
        }


        return RestResponse.success(true);
    }

    /**
     * 上传分块
     * @param fileMd5  文件md5
     * @param chunk  分块序号
     * @param bytes  文件字节
     * @return
     */
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {

        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将分块上传到文件系统
            addMediaFilesToMinIO(bytes, bucket_videofiles, chunkFilePath);
            //上传成功
            return RestResponse.success(true);
        } catch (Exception e) {
            log.debug("上传分块文件失败：{}", e.getMessage());
            return RestResponse.validfail(false,"上传分块失败");
        }

    }

    /**
     * 合并分块
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //下载分块
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);

        //得到合并后文件的扩展名
        String filename = uploadFileParamsDto.getFilename();
        //扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        File tempMergeFile = null;
        try {
            try {
                //创建一个临时文件作为合并文件
                tempMergeFile = File.createTempFile("'merge'", extension);
            } catch (IOException e) {
                XueChengPlusException.cast("创建临时合并文件出错");
            }

            //创建合并文件的流对象
            try( RandomAccessFile raf_write  =new RandomAccessFile(tempMergeFile, "rw")) {
                byte[] b = new byte[1024];
                for (File file : chunkFiles) {
                    //读取分块文件的流对象
                    try(RandomAccessFile raf_read = new RandomAccessFile(file, "r");) {
                        int len = -1;
                        while ((len = raf_read.read(b)) != -1) {
                            //向合并文件写数据
                            raf_write.write(b, 0, len);
                        }
                    }

                }
            } catch (IOException e) {
                XueChengPlusException.cast("合并文件过程出错");
            }


            //校验合并后的文件是否正确
            try {
                FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
                String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                if (!fileMd5.equals(mergeMd5Hex)) {
                    log.debug("合并文件校验不通过,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                    XueChengPlusException.cast("合并文件校验不通过");
                }
            } catch (IOException e) {
                log.debug("合并文件校验出错,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                XueChengPlusException.cast("合并文件校验出错");
            }


            //拿到合并文件在minio的存储路径
            String mergeFilePath = getFilePathByMd5(fileMd5, extension);
            //将合并后的文件上传到文件系统
            addMediaFilesToMinIO(tempMergeFile.getAbsolutePath(), bucket_videofiles, mergeFilePath);

            //将文件信息入库保存
            uploadFileParamsDto.setFileSize(tempMergeFile.length());//合并文件的大小
            addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videofiles, mergeFilePath);

            return RestResponse.success(true);
        }finally {
            //删除临时分块文件
            if(chunkFiles!=null){
                for (File chunkFile : chunkFiles) {
                    if(chunkFile.exists()){
                        chunkFile.delete();
                    }
                }
            }
            //删除合并的临时文件
            if(tempMergeFile!=null){
                tempMergeFile.delete();
            }
        }
    }

    @Override
    public MediaFiles getFileById(String id) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        if(mediaFiles==null){
            XueChengPlusException.cast("文件不存在");
        }
        String url = mediaFiles.getUrl();
        if(StringUtils.isEmpty(url)){
            XueChengPlusException.cast("文件还没有处理，请稍后预览");
        }

        return mediaFiles;
    }

    /**
     * 拿到合并文件在minio的存储路径
     * @param fileMd5
     * @param fileExt
     * @return
     */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return  fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    /**
     * @description 下载分块
     * @param fileMd5
     * @param chunkTotal 分块数量
     * @return java.io.File[] 分块文件数组
     * @author Mr.M
     * @date 2022/10/14 15:07
    */
    private File[] checkChunkStatus(String fileMd5,int chunkTotal ){

        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件数组
        File[] chunkFiles = new File[chunkTotal];
        //开始下载
        for (int i = 0; i < chunkTotal; i++) {
            //分块文件的路径
            String chunkFilePath = chunkFileFolderPath + i;
            //分块文件
            File chunkFile = null;
            try {
//                创建临时分块文件
                chunkFile = File.createTempFile("chunk", null);
            } catch (IOException e) {
                e.printStackTrace();
                XueChengPlusException.cast("创建分块临时文件出错"+e.getMessage());
            }

            //下载分块文件
            downloadFileFromMinIO(chunkFile, bucket_videofiles, chunkFilePath);
            chunkFiles[i] = chunkFile;

        }

       return chunkFiles;

    }

    //根据桶和文件路径从minio下载文件
    public File downloadFileFromMinIO(File file,String bucket,String objectName){

        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();
        try(
                InputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream =new FileOutputStream(file);
        ) {
            IOUtils.copy(inputStream,outputStream);
            return file;
        }catch (Exception e){
            e.printStackTrace();
            XueChengPlusException.cast("查询分块文件出错");
        }
        return null;
    }

    /**
     * 得到分块文件的目录
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    //将文件上传到文件系统
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("文件上传成功:{}",filePath);
        } catch (Exception e) {
           XueChengPlusException.cast("文件上传到文件系统失败");
        }
    }

    //将文件上传到分布式文件系统
    private void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName) {

        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流

        if (objectName.indexOf(".") >= 0) {
            //取objectName中的扩展名
            String extension = objectName.substring(objectName.lastIndexOf("."));
//            根据扩展名得到content-type
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    //InputStream stream, long objectSize 对象大小, long partSize 分片大小(-1表示5M,最大不要超过5T，最多10000)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            //上传到minio
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("上传文件到文件系统出错:{}", e.getMessage());
            XueChengPlusException.cast("上传文件到文件系统出错");
        }
    }

    /**
     * 根据扩展名拿匹配的媒体类型
     */
    private String getMimeTypeByextension(String extension){
        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流
        if(StringUtils.isNotEmpty(extension)){
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return  contentType;
    }

    /**
     * 根据日期拼接目录
     */
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

    public static void main(String[] args) {
        String extension = ".jpg";
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        if (extensionMatch != null) {
            System.out.println(extensionMatch.getMimeType());
        }
    }
}
