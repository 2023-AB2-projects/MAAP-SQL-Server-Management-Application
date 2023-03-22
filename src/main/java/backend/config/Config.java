package backend.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Config {
    private final String databaseCatalogPath = System.getProperty("user.dir") + "/src/main/resources/Catalog.xml";
}
