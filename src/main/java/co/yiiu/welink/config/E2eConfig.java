package co.yiiu.welink.config;

import co.yiiu.welink.service.ISystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;

/**
 * E2E 测试环境配置 - 仅在 e2e profile 下加载
 * <p>
 * 在 ApplicationContext 刷新完成后刷新 SystemConfigService 的静态缓存，
 * 确保 schema.sql 加载后的系统配置可被正常读取。
 */
@Configuration
@Profile("e2e")
public class E2eConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger log = LoggerFactory.getLogger(E2eConfig.class);

    @Resource
    private ISystemConfigService systemConfigService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        systemConfigService.refreshCache();
        int size = systemConfigService.selectAllConfig().size();
        log.info("E2E config cache refreshed, {} items loaded", size);
    }
}
