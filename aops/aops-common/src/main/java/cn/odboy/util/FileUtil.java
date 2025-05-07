package cn.odboy.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.odboy.constant.FileTypeEnum;
import cn.odboy.constant.SystemConst;
import cn.odboy.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static cn.odboy.constant.SystemConst.SYMBOL_ADD;
import static cn.odboy.constant.SystemConst.SYMBOL_AT;
import static cn.odboy.constant.SystemConst.SYMBOL_EQUAL;
import static cn.odboy.constant.SystemConst.SYMBOL_SUBTRACT;

/**
 * File工具类，扩展 hutool 工具包
 */
@Slf4j
public final class FileUtil extends cn.hutool.core.io.FileUtil {

    /**
     * 系统临时目录
     * <br>
     * windows 包含路径分割符，但Linux 不包含,
     * 在windows \\==\ 前提下，
     * 为安全起见 同意拼装 路径分割符，
     * <pre>
     *       java.io.tmpdir
     *       windows : C:\Users/xxx\AppData\Local\Temp\
     *       linux: /temp
     * </pre>
     */
    public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    /**
     * 定义GB的计算常量
     */
    private static final int GB = 1024 * 1024 * 1024;
    /**
     * 定义MB的计算常量
     */
    private static final int MB = 1024 * 1024;
    /**
     * 定义KB的计算常量
     */
    private static final int KB = 1024;

    /**
     * 格式化小数
     */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static void main(String[] args) {
    }

    /**
     * MultipartFile转File -> ok
     */
    public static File toFile(MultipartFile multipartFile) {
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = "." + getSuffix(fileName);
        File file = null;
        try {
            // 用uuid作为文件名，防止生成的临时文件重复
            file = new File(SYS_TEM_DIR + IdUtil.simpleUUID() + prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error("保存临时文件失败", e);
        }
        return file;
    }

    /**
     * 文件大小转换 -> ok
     */
    public static String getSize(long size) {
        String resultSize;
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = DF.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = DF.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = DF.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        return resultSize;
    }

    /**
     * inputStream 转 File -> ok
     */
    public static File inputStreamToFile(InputStream ins, String name) {
        File file = new File(SYS_TEM_DIR + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = null;
        try {
            os = Files.newOutputStream(file.toPath());
            int bytesRead;
            int len = 8192;
            byte[] buffer = new byte[len];
            while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseUtil.close(os);
            CloseUtil.close(ins);
        }
        return file;
    }

    /**
     * 将文件名解析成文件的上传路径 -> ok
     */
    public static File upload(MultipartFile file, String filePath) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmssS");
        // 过滤非法文件名
        String name = getPrefix(verifyFilename(file.getOriginalFilename()));
        String suffix = getSuffix(file.getOriginalFilename());
        String nowStr = "-" + format.format(date);
        try {
            String fileName = name + nowStr + "." + suffix;
            String path = filePath + fileName;
            // getCanonicalFile 可解析正确各种路径
            File dest = new File(path).getCanonicalFile();
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdirs()) {
                    log.error("创建目录失败, {}", dest.getParentFile().getAbsolutePath());
                }
            }
            // 文件写入
            file.transferTo(dest);
            return dest;
        } catch (Exception e) {
            log.error("文件上传失败", e);
        }
        return null;
    }

    /**
     * 导出excel -> ok
     */
    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        String tempPath = SYS_TEM_DIR + IdUtil.fastSimpleUUID() + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer = ExcelUtil.getBigWriter(file);
        // 处理数据以防止CSV注入
        List<Map<String, Object>> sanitizedList = list.parallelStream().map(map -> {
            Map<String, Object> sanitizedMap = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                if (value instanceof String) {
                    String strValue = (String) value;
                    // 检查并处理以特殊字符开头的值
                    if (strValue.startsWith(SYMBOL_EQUAL) || strValue.startsWith(SYMBOL_ADD)
                            || strValue.startsWith(SYMBOL_SUBTRACT) || strValue.startsWith(SYMBOL_AT)) {
                        // 添加单引号前缀
                        strValue = "'" + strValue;
                    }
                    sanitizedMap.put(key, strValue);
                } else {
                    sanitizedMap.put(key, value);
                }
            });
            return sanitizedMap;
        }).collect(Collectors.toList());
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(sanitizedList, true);
        SXSSFSheet sheet = (SXSSFSheet) writer.getSheet();
        //上面需要强转SXSSFSheet  不然没有trackAllColumnsForAutoSizing方法
        sheet.trackAllColumnsForAutoSizing();
        //列宽自适应
        writer.autoSizeColumnAll();
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
        ServletOutputStream out = response.getOutputStream();
        // 终止后删除临时文件
        file.deleteOnExit();
        writer.flush(out, true);
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 获取文件类型 -> ok
     */
    public static String getFileType(String type) {
        String documents = "txt doc pdf ppt pps xlsx xls docx";
        String voice = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(type)) {
            return FileTypeEnum.IMAGE.getCode();
        } else if (documents.contains(type)) {
            return FileTypeEnum.DOC.getCode();
        } else if (voice.contains(type)) {
            return FileTypeEnum.VOICE.getCode();
        } else if (video.contains(type)) {
            return FileTypeEnum.VIDEO.getCode();
        } else {
            return FileTypeEnum.OTHER.getCode();
        }
    }

    /**
     * 检查文件大小是否超出最大值 -> ok
     */
    public static void checkSize(long maxSize, long size) {
        // 1M
        int len = 1024 * 1024;
        if (size > (maxSize * len)) {
            throw new BadRequestException("文件超出规定大小:" + maxSize + "MB");
        }
    }

    /**
     * 判断两个文件是否相同 -> ok
     */
    public static boolean check(File file1, File file2) {
        String img1Md5 = getMd5(file1);
        String img2Md5 = getMd5(file2);
        if (img1Md5 != null) {
            return img1Md5.equals(img2Md5);
        }
        return false;
    }

    /**
     * 判断两个文件是否相同 -> ok
     */
    public static boolean check(String file1Md5, String file2Md5) {
        return file1Md5.equals(file2Md5);
    }

    /**
     * 获取文件字节数组 -> ok
     */
    private static byte[] getByte(File file) {
        byte[] b = new byte[(int) file.length()];
        InputStream in = null;
        try {
            in = Files.newInputStream(file.toPath());
            // 忽略read值
            int read = in.read(b);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.close(in);
        }
        return b;
    }

    /**
     * 获取文件MD5值 -> ok
     */
    private static String getMd5(byte[] bytes) {
        // 16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            // 移位 输出字符串
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error("获取文件MD5值失败", e);
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param request  /
     * @param response /
     * @param file     /
     */
    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, boolean deleteOnExit) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error("下载文件失败", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    if (deleteOnExit) {
                        file.deleteOnExit();
                    }
                } catch (IOException e) {
                    log.error("关闭IO流失败", e);
                }
            }
        }
    }

    /**
     * 验证并过滤非法的文件名
     *
     * @param fileName 文件名
     * @return 文件名
     */
    public static String verifyFilename(String fileName) {
        // 过滤掉特殊字符
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|~\\s]", "");

        // 去掉文件名开头和结尾的空格和点
        fileName = fileName.trim().replaceAll("^[. ]+|[. ]+$", "");

        // 不允许文件名超过255（在Mac和Linux中）或260（在Windows中）个字符
        int maxFileNameLength = 255;
        if (System.getProperty(SystemConst.PROPERTY_OS_NAME).startsWith(SystemConst.OS_NAME_WINDOWS)) {
            maxFileNameLength = 260;
        }
        if (fileName.length() > maxFileNameLength) {
            fileName = fileName.substring(0, maxFileNameLength);
        }

        // 过滤掉控制字符
        fileName = fileName.replaceAll("[\\p{Cntrl}]", "");

        // 过滤掉 ".." 路径
        fileName = fileName.replaceAll("\\.{2,}", "");

        // 去掉文件名开头的 ".."
        fileName = fileName.replaceAll("^\\.+/", "");

        // 保留文件名中最后一个 "." 字符，过滤掉其他 "."
        fileName = fileName.replaceAll("^(.*)(\\.[^.]*)$", "$1").replaceAll("\\.", "") +
                fileName.replaceAll("^(.*)(\\.[^.]*)$", "$2");

        return fileName;
    }

    public static String getMd5(File file) {
        return getMd5(getByte(file));
    }
}
