package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record Table(String databaseName, @JsonProperty String tableName, @JsonProperty String fileName,
                    @JsonProperty int rowLength, @JsonProperty ArrayList<Attribute> attributes,
                    @JsonProperty PrimaryKey primaryKey, @JsonProperty ArrayList<ForeignKey> foreignKeys,
                    @JsonProperty ArrayList<String> uniqueAttributes, @JsonProperty IndexFile indexFiles) {}
