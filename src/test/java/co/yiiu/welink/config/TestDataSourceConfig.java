package co.yiiu.welink.config;

import co.yiiu.welink.service.ISystemConfigService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 测试环境下的数据源初始化配置
 * <p>
 * schema 初始化由 H2 的 INIT 参数在连接创建时自动执行（见 application-test.yml），
 * 此配置仅用于在 schema 加载完成后强制填充 SystemConfigService 的静态缓存，
 * 确保所有业务 Bean 能读取到完整的系统配置。
 */
@Configuration
@Profile("test")
public class TestDataSourceConfig {

    @Resource
    private ISystemConfigService systemConfigService;

    @PostConstruct
    public void warmUpConfigCache() {
        // schema.sql 已通过 H2 INIT 参数在第一个连接创建时执行
        // 这里主动调用 selectAllConfig() 以填充静态配置缓存
        systemConfigService.selectAllConfig();
    }
}
