package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.upload.controller.UploadController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * @author: Alaska He
 * Date: 2018/10/24 0024
 * Time: 19:15
 */
@Service
public class UploadService {
    //日志
    public static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    //文件后缀的集合
    public static final List<String> suffixs = Arrays.asList("image/jpeg","image/png");
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public String uploadImage(MultipartFile file) {
        try {
            //验证是否是图片，验证后缀
            String contentType = file.getContentType();
            if(!suffixs.contains(contentType)){
                logger.info("上传失败，文件类型不匹配，{}",contentType);
                return null;
            }
            //验证文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                logger.info("上传失败，文件内容不符");
            }
           //保存上传的文件
            //1.获取后缀
            String extentions = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = this.fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extentions,null);

            return "http://image.leyou.com"+storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            return null;
        }
        return null;

    }
}
