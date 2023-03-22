package backend;

import backend.model.Database;
import backend.repository.DatabaseRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class Main {
    public static void main(String[] args) {
        DatabaseRepository.createDataBase(new Database());
    }
}