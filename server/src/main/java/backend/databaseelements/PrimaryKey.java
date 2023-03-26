package backend.databaseelements;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record PrimaryKey(@JsonProperty ArrayList<String> primaryKeyAttributes) {}
