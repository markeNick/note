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
 * 无注释版本
 */
@RestController
public class TController {

    @RequestMapping("/upload")
    private String upload(@RequestParam("file") MultipartFile file) {

        if (file == null && file.isEmpty()) {
            return "文件不能为空";
        }

        String fileName = file.getOriginalFilename();
        String path = MyFileUtil.generateFileName(fileName);

        FileUtil.mkParentDirs(path);
        File localFile = new File(path);

        try {
            file.transferTo(localFile);
        } catch (Exception e) {
            System.out.println("上传异常");
            e.printStackTrace();
        }

        return path;
    }

    @RequestMapping("/download")
    public void download(@RequestParam("url") String url, HttpServletResponse response) throws IOException {

        File file = new File(url);
        String fileName = url.substring(url.lastIndexOf(File.separator));
        MediaType mediaType = MediaTypeFactory.getMediaType(fileName).get();

        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", mediaType.toString());
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(url, "UTF-8"));
        ServletOutputStream os = response.getOutputStream();
        FileUtil.writeToStream(file, os);
        os.close();
    }

}
