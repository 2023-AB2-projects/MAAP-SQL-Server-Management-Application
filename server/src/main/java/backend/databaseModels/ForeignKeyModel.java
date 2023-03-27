package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record ForeignKeyModel(@JsonProperty String referencedTable, @JsonProperty ArrayList<String> referencedAttributes) { }
