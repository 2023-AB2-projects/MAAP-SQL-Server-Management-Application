package backend.model;

import backend.config.Config;
import backend.model.XMLModel.Database;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import javax.sql.rowset.spi.XmlWriter;
import java.io.File;
import java.io.IOException;

@Data
@JacksonXmlRootElement(localName = "Database")
public class CreateDatabaseAction implements DatabaseAction{
    @JacksonXmlProperty(isAttribute = true)
    private String databaseName;
    @JacksonXmlProperty(isAttribute = false)
    private Integer nrTables;

    public CreateDatabaseAction(String databaseName) {
        this.databaseName = databaseName;
        this.nrTables = 10;
    }

    @Override
    public void actionPerform() throws IOException {
        // accessing the Catalog.xml file from config
        Config config = new Config();

        // opening the Catalog file
        File catalog = new File(config.getDB_CATALOG_PATH());

        // creating the database xml model
        //Database database = new Database();
        //database.setDatabaseName(databaseName);

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(catalog, this);
    }
}
