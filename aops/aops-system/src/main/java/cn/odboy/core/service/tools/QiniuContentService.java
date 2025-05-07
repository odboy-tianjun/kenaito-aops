package cn.odboy.core.service.tools;

import cn.odboy.core.dal.dataobject.tools.QiniuContent;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface QiniuContentService extends IService<QiniuContent> {
    /**
     * 上传文件
     *
     * @param file 文件
     * @return QiniuContent
     */
    QiniuContent uploadFile(MultipartFile file);

    /**
     * 创建文件预览链接
     *
     * @param content 文件信息
     * @return String
     */
    String createFilePreviewUrl(QiniuContent content);

    /**
     * 同步数据
     */
    void synchronize();

    /**
     * 删除文件
     *
     * @param ids 文件ID数组
     */
    void removeFileByIds(Long[] ids);

    /**
     * 导出数据
     *
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void downloadExcel(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    void removeFileById(Long id);
}
