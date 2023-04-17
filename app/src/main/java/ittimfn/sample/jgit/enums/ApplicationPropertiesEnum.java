package ittimfn.sample.jgit.enums;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;

@Getter
public enum ApplicationPropertiesEnum {
    ORGANIZATION("azure.devops.organization");
    
    private static Logger logger = LogManager.getLogger();

    private static Properties properties;
    
    private final String key;

    private ApplicationPropertiesEnum(String key) {
        this.key = key;
    }
    
    public static void load(Path propertiesPath) throws IOException {
        logger.info("Properties load : {}", propertiesPath);
        properties = new Properties();
        properties.load(
            Files.newBufferedReader(propertiesPath, StandardCharsets.UTF_8)
        );
    }
    
    public String getPropertiesValue() {
        return properties.getProperty(this.key);
    }
}
