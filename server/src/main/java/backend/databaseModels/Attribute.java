package backend.databaseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record Attribute(@JsonProperty String attributeName, @JsonProperty String type,
                        @JsonProperty int length, @JsonProperty boolean isNull,
                        @JsonProperty boolean isNullable) {}
