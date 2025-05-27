package cn.odboy.core.service.tools;

import cn.odboy.common.model.PageResult;
import cn.odboy.core.dal.dataobject.tools.QiniuContentDO;
import cn.odboy.core.service.tools.dto.QiniuQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface QiniuContentService extends IService<QiniuContentDO> {
    /**
     * 分页查询
     *
     * @param args 条件
     * @param page 分页参数
     * @return /
     */
    PageResult<QiniuContentDO> describeQiniuContentPage(QiniuQueryArgs args, Page<Object> page);

    /**
     * 查询全部
     *
     * @param args 条件
     * @return /
     */
    List<QiniuContentDO> describeQiniuContentList(QiniuQueryArgs args);

    /**
     * 上传文件
     *
     * @param file 文件
     * @return QiniuContentDO
     */
    QiniuContentDO uploadFile(MultipartFile file);

    /**
     * 创建文件预览链接
     *
     * @param content 文件信息
     * @return String
     */
    String createFilePreviewUrl(QiniuContentDO content);

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
    void downloadExcel(List<QiniuContentDO> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    void removeFileById(Long id);
}
