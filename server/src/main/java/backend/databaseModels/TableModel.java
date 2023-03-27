package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record TableModel(@JsonProperty String tableName, @JsonProperty String fileName,
                         @JsonProperty int rowLength, @JsonProperty ArrayList<AttributeModel> attributes,
                         @JsonProperty PrimaryKeyModel primaryKey, @JsonProperty ArrayList<ForeignKeyModel> foreignKeys,
                         @JsonProperty ArrayList<String> uniqueAttributes, @JsonProperty ArrayList<IndexFileModel> indexFiles) {}
