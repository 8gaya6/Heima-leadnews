package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Description: Todo
 * Class Name: MinIOTest
 * Date: 2023/7/7 14:49
 *
 * @author Hao
 * @version 1.1
 */

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void testUpdateImgFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream("D:\\Picture\\毕业一周年.png");
            String filePath = fileStorageService.uploadImgFile("", "oneyear.png", fileInputStream);
            System.out.println(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 目标：把之前生成的 list.html 文件上传到 minio 中，并且可以在浏览器中访问
     *
     * @param args
     */
    public static void main(String[] args) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("D:\\Learning\\后端\\项目\\黑马头条\\资料\\plugins\\js\\axios.min.js");

            // 1. 创建 minio 链接客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.128:9000").build();
            // 2. 上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/js/axios.min.js") // 文件名
                    .contentType("text/js") // 文件类型
                    .bucket("leadnews") // 桶名词 与 minio 创建的名词一致
                    .stream(fileInputStream, fileInputStream.available(), -1) // 文件流
                    .build();
            minioClient.putObject(putObjectArgs);
            System.out.println("http://192.168.200.128:9000/leadnews/list.html");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
