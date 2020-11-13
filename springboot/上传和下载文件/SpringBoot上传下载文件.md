# SpringBoot 上传和下载文件



## 上传文件

### 步骤：

* 使用MultipartFile接收前端传送过来的文件
* 生成存放到服务器的目录
* 在服务器创建一个同名本地文件
* 将上传的文件内容输入到本地文件
* 将服务器文件路径返回给前端



### 代码

```java
/** 
 *	这里只简易写出关键流程，不考虑统一返回值、全局异常处理、日志记录
 **/
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
```



**文件名和目录生成工具类**

```java
/**
 *	文件工具类，用来生成文件名和存放目录
 **/
public class MyFileUtil {

    /**
     * Linux 文件存放目录
     */
    public static final String DATA_FILE_PATH = "/data/file/";

    /**
     * Windows 文件存放目录
     * 方便测试用
     */
//    public static final String DATA_FILE_PATH = "C:\\";

    /**
     * 生成上传到目录下的文件名
     * @param oryFileName 如：aaa.txt
     * @return  如：/data/file/2020/11/6/asfsasds/dsadastd_aaa.txt
     */
    public static String generateFileName(String oryFileName) {
        oryFileName = oryFileName == null ? "" : oryFileName;

        String path = new StringBuilder(createRepairFileDir())
                .append(RandomStringUtils.randomAlphanumeric(8))
                .append("_")
                .append(oryFileName.length() > 100 ? oryFileName.substring(oryFileName.length() / 2) : oryFileName)
                .toString();

        return path;
    }

    /**
     * 目录格式：/data/file/year/month/day/随机数/
     * 如: /data/file/2020/11/11/asfsasds/
     */
    public static String createRepairFileDir() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;

        return new StringBuilder().append(DATA_FILE_PATH)
                .append(File.separator)
                .append(calendar.get(Calendar.YEAR))
                .append(File.separator)
                .append(month > 9 ? "" : "0")
                .append(month).append(File.separator)
                .append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(File.separator)
                .append(RandomStringUtils.randomAlphanumeric(8))
                .append(File.separator)
                .toString();
    }
}
```



## 下载文件

### 步骤

* 前端传入文件的路径url
* 根据url创建一个File类对象
* 设置头部信息和编码
* 通过HttpServletResponse获取输出流
* 将File内容写入到输出流
* 关闭输出流



### 代码

```java
@RequestMapping("/download")
public void download(@RequestParam("url") String url, HttpServletResponse response) 
    throws IOException {

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
    response.setHeader("Content-Disposition", 
                       "attachment;fileName=" + URLEncoder.encode(url, "UTF-8"));

    // 从response 获取输出流
    ServletOutputStream os = response.getOutputStream();

    // 利用hutool工具包将文件内容写入到输出流
    FileUtil.writeToStream(file, os);

    // 关闭输出流
    os.close();
}
```

