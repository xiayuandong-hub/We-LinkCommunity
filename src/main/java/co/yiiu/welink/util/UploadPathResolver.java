package co.yiiu.welink.util;

import co.yiiu.welink.service.ISystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;

/**
 * Resolves the local upload directory and falls back to the project static directory when needed.
 */
@Component
public class UploadPathResolver {

    private static final String DEFAULT_UPLOAD_PATH = "./static/upload/";

    private final Logger log = LoggerFactory.getLogger(UploadPathResolver.class);

    @Resource
    private ISystemConfigService systemConfigService;

    public File resolveUploadRoot() {
        String configuredPath = getConfiguredUploadPath();
        File configuredDirectory = toDirectory(configuredPath);
        if (isUsableDirectory(configuredDirectory)) {
            return configuredDirectory;
        }
        File defaultDirectory = toDirectory(DEFAULT_UPLOAD_PATH);
        if (!isUsableDirectory(defaultDirectory)) {
            log.error("Default upload directory is unavailable: {}", defaultDirectory.getAbsolutePath());
        }
        if (StringUtils.hasText(configuredPath)) {
            log.warn("Configured upload_path [{}] is unavailable, fallback to [{}]",
                    configuredPath, normalize(defaultDirectory));
        }
        return defaultDirectory;
    }

    public File resolveDirectory(String customPath) {
        File directory = StringUtils.hasText(customPath) ? new File(resolveUploadRoot(), customPath) : resolveUploadRoot();
        if (!directory.exists() && !directory.mkdirs()) {
            log.error("Failed to create upload directory: {}", directory.getAbsolutePath());
        }
        return directory;
    }

    public String resolveUploadRootPath() {
        return normalize(resolveUploadRoot());
    }

    private String getConfiguredUploadPath() {
        try {
            Map<String, String> config = systemConfigService.selectAllConfig();
            if (config == null) return null;
            return config.get("upload_path");
        } catch (Exception e) {
            log.warn("Failed to read upload_path from system config", e);
            return null;
        }
    }

    private File toDirectory(String path) {
        if (!StringUtils.hasText(path)) return new File(DEFAULT_UPLOAD_PATH);
        return new File(path);
    }

    private boolean isUsableDirectory(File directory) {
        return directory.exists() ? directory.isDirectory() : directory.mkdirs();
    }

    private String normalize(File directory) {
        String normalized = directory.getAbsolutePath().replace("\\", "/");
        if (!normalized.endsWith("/")) normalized += "/";
        return normalized;
    }
}
