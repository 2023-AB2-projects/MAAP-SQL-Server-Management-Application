package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record PrimaryKeyModel(@JsonProperty ArrayList<String> primaryKeyAttributes) {}
