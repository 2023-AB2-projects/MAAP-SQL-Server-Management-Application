package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IndexFile(@JsonProperty String indexName) {}
