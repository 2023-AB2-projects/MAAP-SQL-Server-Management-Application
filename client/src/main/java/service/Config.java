package service;

import lombok.Data;

import java.io.File;

@Data
public class Config {
    private static final String DB_CATALOG_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "Catalog.json";
    private static final String DB_RECORDS_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "records";
    private static final String IMAGES_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "images";
    private static final String USER_SCRIPTS_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "user_scripts";
    private static final String DB_CATALOG_ROOT = "databases";

    /* Getters */
    public static String getDbCatalogPath() { return DB_CATALOG_PATH; }
    public static String getDbRecordsPath() { return  DB_RECORDS_PATH; }
    public static String getImagesPath() { return IMAGES_PATH; }
    public static String getUserScriptsPath() { return USER_SCRIPTS_PATH; }
    public static String getDbCatalogRoot() { return DB_CATALOG_ROOT; }
    public static File getCatalogFile() { return new File(DB_CATALOG_PATH); }
}
