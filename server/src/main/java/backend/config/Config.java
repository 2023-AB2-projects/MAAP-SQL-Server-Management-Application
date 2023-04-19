package backend.config;

import lombok.Data;

import java.io.File;

@Data
public class Config {

    private static final String DB_CATALOG_PATH = System.getProperty("user.dir") + "/server/src/main/resources/Catalog.json";
    private static final String DB_RECORDS_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "records";
    private static final String DB_CATALOG_ROOT = "databases";

    /* Getters */
    public static String getDbCatalogPath() { return DB_CATALOG_PATH; }
    public static String getDbRecordsPath() { return  DB_RECORDS_PATH; }
    public static String getDbCatalogRoot() { return DB_CATALOG_ROOT; }
    public static File getCatalogFile() { return new File(DB_CATALOG_PATH); }
}
