package backend.config;

import lombok.Data;

import java.io.File;

@Data
public class Config {
    private static final String DB_CATALOG_PATH = System.getProperty("user.dir") + "/src/main/resources/Catalog.json";
    private static final String DB_CATALOG_ROOT = "databases";

    /* Getters */
    public static String getDbCatalogPath() { return DB_CATALOG_PATH; }
    public static String getDbCatalogRoot() { return DB_CATALOG_ROOT; }
    public static File getCatalogFile() { return new File(DB_CATALOG_PATH); }
}
