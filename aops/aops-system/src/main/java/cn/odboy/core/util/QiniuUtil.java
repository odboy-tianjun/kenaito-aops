package cn.odboy.core.util;

import cn.odboy.util.FileUtil;
import com.qiniu.storage.Region;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 七牛云存储工具类
 */
public final class QiniuUtil {

    private static final String HUAD = "华东";

    private static final String HUAB = "华北";

    /**
     * 得到机房的对应关系
     *
     * @param zone 机房名称
     * @return Region
     */
    public static Region getRegion(String zone) {

        if (HUAD.equals(zone)) {
            return Region.huadong();
        } else if (HUAB.equals(zone)) {
            return Region.huabei();
        } else {
            // 否则就是东南亚
            return Region.qvmHuadong();
        }
    }

    /**
     * 默认不指定key的情况下，以文件内容的hash值作为文件名
     *
     * @param file 文件名
     * @return String
     */
    public static String getKey(String file) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return FileUtil.getPrefix(file) + "-" +
                sdf.format(date) +
                "." +
                FileUtil.getSuffix(file);
    }
}
