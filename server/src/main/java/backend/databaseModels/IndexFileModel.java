package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class IndexFileModel {
    @Getter
    @Setter
    private String indexName;

    @Getter
    @Setter
    private int keyLength;

    @Getter
    @Setter
    private boolean isUnique;

    @Getter
    @Setter
    private String indexType;

    @Getter
    @Setter
    private ArrayList<String> indexAttributes;

    public IndexFileModel() {
        this.indexName = "none";
        this.keyLength = 0;
        this.isUnique = false;
        this.indexType = "none";
        this.indexAttributes = new ArrayList<>();
    }

    public IndexFileModel(String indexName, int keyLength, boolean isUnique,
                          String indexType, ArrayList<String> indexAttributes) {
        this.indexName = indexName;
        this.keyLength = keyLength;
        this.isUnique = isUnique;
        this.indexType = indexType;
        this.indexAttributes = indexAttributes;
    }
}
