package backend.config;

import lombok.Data;

import java.io.File;

@Data
public class Config {
    private final String DB_CATALOG_PATH = System.getProperty("user.dir") + "/src/main/resources/Catalog.json";
    private final String DB_CATALOG_ROOT = "Databases";

    public File getCatalogFile() { return new File(DB_CATALOG_PATH); }
}
