package backend.databaseelements;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IndexFile {
    @JsonProperty
    private String indexName;

    public IndexFile(String indexName) {
        this.indexName = indexName;
    }
}
