package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record IndexFileModel(@JsonProperty String indexName, @JsonProperty int keyLength,
                             @JsonProperty boolean isUnique, @JsonProperty String indexType,
                             @JsonProperty ArrayList<String> indexAttributes) {}
