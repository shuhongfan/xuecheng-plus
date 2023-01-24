package com.shf.xuecheng.media.api;

import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.base.model.PageParams;
import com.shf.xuecheng.base.model.PageResult;
import com.shf.xuecheng.base.model.RestResponse;
import com.shf.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.shf.xuecheng.media.model.dto.UploadFileParamsDto;
import com.shf.xuecheng.media.model.dto.UploadFileResultDto;
import com.shf.xuecheng.media.model.po.MediaFiles;
import com.shf.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {

        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    /**
     * 使用 RequestPart 来标记 multipart/form-data
     * @param filedata
     * @param folder
     * @param objectName
     * @return
     */
    @ApiOperation("文件上传接口")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
                                      @RequestParam(value = "folder",required=false) String folder,
                                      @RequestParam(value= "objectName",required=false) String objectName) {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        String contentType = filedata.getContentType();
        uploadFileParamsDto.setContentType(contentType);
        uploadFileParamsDto.setFileSize(filedata.getSize());//文件大小
        if (contentType.indexOf("image") >= 0) {
            //是个图片
            uploadFileParamsDto.setFileType("001001");
        } else {
            uploadFileParamsDto.setFileType("001003");
        }
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());//文件名称

        UploadFileResultDto uploadFileResultDto = null;
        try {
            uploadFileResultDto = mediaFileService.uploadFile(
                    companyId,
                    uploadFileParamsDto,
                    filedata.getBytes(),
                    folder,
                    objectName);
        } catch (Exception e) {
            XueChengPlusException.cast("上传文件过程中出错，请重新上传");
        }

        return uploadFileResultDto;
    }

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

        //调用service查询文件的url
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        return RestResponse.success(mediaFiles.getUrl());
    }

}
