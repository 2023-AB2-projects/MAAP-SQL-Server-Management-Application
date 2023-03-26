package backend.databaseelements;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record ForeignKey(@JsonProperty String referencedTable, @JsonProperty ArrayList<String> referencedAttributes) { }
