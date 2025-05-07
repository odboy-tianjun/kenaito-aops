package cn.odboy.core.framework.mybatisplus.config;

import cn.odboy.core.framework.mybatisplus.context.InsertBatchSqlInjector;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // MySQL的分页拦截器
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(500L);
        /// 这个配置会导致：当分页没数据时，游标从0开始，会导致无论page为什么值，就算分页超出最大页数，依然能够查询到
        /// paginationInnerInterceptor.setOverflow(true);
        paginationInnerInterceptor.setOptimizeJoin(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    /**
     * Mapper支持批量插入
     */
    @Bean
    public InsertBatchSqlInjector insertBatchSqlInjector() {
        return new InsertBatchSqlInjector();
    }
}
