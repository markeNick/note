package utils;

import java.io.File;
import java.util.Calendar;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author: TRQ 2020/11/13
 *
 * 文件工具类
 */
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
