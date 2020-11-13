package controller;

import cn.hutool.core.io.FileUtil;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.MyFileUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author: TRQ 2020/11/13
 *
 * 带注释版本
 */
@RestController
public class TestController {

    @RequestMapping("/upload")
    private String upload(@RequestParam("file") MultipartFile file) {

        if (file == null && file.isEmpty()) {
            return "文件不能为空";
        }

        // 获取原始文件名，如：aaa.txt
        String fileName = file.getOriginalFilename();

        // 生成存放到服务器上的目录
        String path = MyFileUtil.generateFileName(fileName);

        // 使用hutool工具包创建文件
        FileUtil.mkParentDirs(path);

        // 获取刚刚创建的文件对象，用来进行写入数据
        File localFile = new File(path);

        // 将上传的文件内容写入到在服务器创建的文件
        try {
            file.transferTo(localFile);
        } catch (Exception e) {
            System.out.println("上传异常");
            e.printStackTrace();
        }

        // 把文件路径返回给前端
        return path;
    }

    @RequestMapping("/download")
    public void download(@RequestParam("url") String url, HttpServletResponse response) throws IOException {

        // 创建文件对象
        File file = new File(url);

        // 获取url中的文件 名称.后缀
        String fileName = url.substring(url.lastIndexOf(File.separator));

        // 根据文件后缀获取对应头部的 content-type类型
        MediaType mediaType = MediaTypeFactory.getMediaType(fileName).get();

        // 编码格式，防止乱码
        response.setCharacterEncoding("UTF-8");

        // 设置头部 content-type 类型，不同文件不同类型
        response.setHeader("content-type", mediaType.toString());

        // 设置文件在浏览器中打开还是下载，由前端去控制
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(url, "UTF-8"));

        // 从response 获取输出流
        ServletOutputStream os = response.getOutputStream();

        // 利用hutool工具包将文件内容写入到输出流
        FileUtil.writeToStream(file, os);

        // 关闭输出流
        os.close();
    }

}
