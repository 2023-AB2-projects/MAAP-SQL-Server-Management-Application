package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record PrimaryKey(@JsonProperty ArrayList<String> primaryKeyAttributes) {}
