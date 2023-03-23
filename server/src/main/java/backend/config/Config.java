package backend.config;

import lombok.Data;

@Data
public class Config {
    private final String DB_CATALOG_PATH = System.getProperty("user.dir") + "/src/main/resources/Catalog.json";
    private final String DB_CATALOG_ROOT = "Databases";
}
