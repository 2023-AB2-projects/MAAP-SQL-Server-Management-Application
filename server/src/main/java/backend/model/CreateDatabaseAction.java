package backend.model;

import backend.config.Config;
import backend.model.XMLModel.Database;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import javax.sql.rowset.spi.XmlWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Data
@JsonRootName(value = "Database")
public class CreateDatabaseAction implements DatabaseAction{
    @JsonProperty
    private String databaseName;
    @JsonProperty
    private Integer nrTables;

    public CreateDatabaseAction(String databaseName) {
        this.databaseName = databaseName;
        this.nrTables = 10;
    }

    @Override
    public void actionPerform() throws IOException {
        // accessing the Catalog.json file from config
        Config config = new Config();

        // opening the Catalog file
        File catalog = new File(config.getDB_CATALOG_PATH());

        // creating the database xml model
        // Database database = new Database();
        // database.setDatabaseName(databaseName);

        // XmlMapper xmlMapper = new XmlMapper();
        // xmlMapper.writeValue(catalog, this);

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
        // mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        // ObjectNode node = (ObjectNode) mapper.readTree(new File(config.getDB_CATALOG_PATH()));

        // node.putPOJO("Database",this);
        // mapper.writeValue(new File(config.getDB_CATALOG_PATH()),node);

        JsonNode rootNode = mapper.readTree(new File(config.getDB_CATALOG_PATH()));
        System.out.println("Before Create Datbase:");
        System.out.println(rootNode);

        JsonNode databaseArrayNode = rootNode.get(config.getDB_CATALOG_ROOT());
        JsonNode newDatabase = mapper.createObjectNode().putPOJO("Database" , this);

        ArrayNode usersArray = (ArrayNode) databaseArrayNode;
        usersArray.add(newDatabase);

        mapper.writeValue(new File(config.getDB_CATALOG_PATH()), rootNode);
    }
}
