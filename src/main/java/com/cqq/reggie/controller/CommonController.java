package com.cqq.reggie.controller;


import com.cqq.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("！@@！！参数file：{}",file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);
        String[] split = originalFilename.split("\\.");

        String fileType = "."+split[split.length-1];

        //创建文件目录
        File dir =new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String fileName= UUID.randomUUID().toString();
        try {
            file.transferTo(new File(basePath+fileName+fileType));
//            file.transferTo(new File(basePath+originalFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(fileName+fileType);
    }


    /**
     * 文件下载
     * @param name
     * @param response
     * @return
     */
    @GetMapping("/download")
    public Result<String> download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));

            //输出流，将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
//            response.setContentType("jpg");

            int len=0;
            byte[] bytes=new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success("文件下载成功！");
    }
}
