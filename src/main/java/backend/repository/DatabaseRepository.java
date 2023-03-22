package backend.repository;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import backend.config.Config;
import backend.model.Database;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Scanner;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Slf4j
public class DatabaseRepository {

    // give a Database java object, it creates a database scheme int the database-catalog
    public static void createDataBase(Database database) throws IOException {
        Config config = new Config();




        try{
        File file = new File(config.getDATABASE_CATALOG_PATH());
        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            System.out.println(data);
        }
        myReader.close();
        } catch (
        FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
