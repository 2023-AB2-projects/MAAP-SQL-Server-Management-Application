package backend.model;

import backend.config.Config;
import backend.model.XMLModel.Database;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
public class CreateDatabaseAction implements DatabaseAction {
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
        File catalog = config.getCatalogFile();

        // new object mapper with indented output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        // SerializationFeature -> Indent Json file

        // reading json file into root obj(tree)
        JsonNode rootNode = mapper.readTree(catalog);
        System.out.println("Before Create Database:" + rootNode.toPrettyString());

        // reading
        JsonNode databaseArrayNode = rootNode.get(config.getDB_CATALOG_ROOT());
        System.out.println("Database node: " + databaseArrayNode.toPrettyString());
        JsonNode newDatabase = JsonNodeFactory.instance.objectNode().putPOJO("Database", this);

        // Get current array of databases stored in 'Databases' json node
        ArrayNode usersArray = (ArrayNode) databaseArrayNode;
        usersArray.add(newDatabase);        // Add the new database

        // Mapper -> Write entire catalog
        mapper.writeValue(catalog, rootNode);

        //FIXME
        // CreateDatabase Exceptions

    }
}
