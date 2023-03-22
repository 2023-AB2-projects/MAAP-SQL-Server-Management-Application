package backend.repository;

import backend.config.Config;
import backend.model.Database;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Slf4j
public class DatabaseRepository {

    // give a Database java object, it creates a database scheme int the database-catalog
    public static void createDataBase(Database database) {
        Config config = new Config();

        try{
        File file = new File(config.getDatabaseCatalogPath());
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
