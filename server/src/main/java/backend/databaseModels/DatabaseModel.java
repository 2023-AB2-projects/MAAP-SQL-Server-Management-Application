package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record DatabaseModel(@JsonProperty String databaseName, @JsonProperty ArrayList<TableModel> tables) { }
