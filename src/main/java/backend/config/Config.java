package backend.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Config {
    private final String DATABASE_CATALOG_PATH = System.getProperty("user.dir") + "/src/main/resources/Catalog.xml";
    private final String DATABASE_CATALOG_ROOT = "Databases";
}
