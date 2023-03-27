package backend.service;

import backend.config.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ServerController {
    //sql command set from
    @Getter
    @Setter
    private String sqlCommand;

    @Getter
    @Setter
    private String currentDatabase;

    @Getter
    @Setter
    private List<String> databases;

    public ServerController() {
        log.info("Server Started!");

        init();
    }

    private void init() {
        accessCatalog();
        loadDatabases();
        setDefaultDatabase();
    }

    private void setDefaultDatabase() {
        setCurrentDatabase("master");
    }

    private void loadDatabases() {

    }

    private void accessCatalog() {
        File catalog = Config.getCatalogFile();
        try {
            if (catalog.createNewFile()) {
                log.info("Catalog.json Created Succesfully!");
                initCatalog(catalog);
            } else {
                log.info("Catalog.json Already Exists!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initCatalog(File catalog) throws IOException {
        String jsonCreate = "{\"databases\":[{\"database\":{\"databaseName\":\"master\", \"tables\":[]}}]}";
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        Object jsonObject = mapper.readValue(jsonCreate, Object.class);
        mapper.writeValue(catalog, jsonObject);
    }
}
