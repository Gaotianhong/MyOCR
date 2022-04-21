package me.myocr.ocr.configuration;

import lombok.Data;
import me.myocr.ocr.utils.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /**
     * File size limitation
     */
    private Long maxSize;

    private ElPath mac;

    private ElPath linux;

    private ElPath windows;

    public ElPath getPath() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith(Constants.WIN)) {
            return windows;
        } else if (os.toLowerCase().startsWith(Constants.MAC)) {
            return mac;
        }
        return linux;
    }

    @Data
    public static class ElPath {

        private String path;
    }
}
