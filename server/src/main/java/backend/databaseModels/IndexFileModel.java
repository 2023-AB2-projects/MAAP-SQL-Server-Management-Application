package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class IndexFileModel {
    @Getter
    @Setter
    private String indexName, indexFileName;

    @Getter
    @Setter
    private boolean isUnique;

    @Getter
    @Setter
    private ArrayList<String> indexFields;

    public IndexFileModel() {
        this.indexName = "none";
        this.indexFileName = "none";
        this.isUnique = false;
        this.indexFields = new ArrayList<>();
    }

    public IndexFileModel(String indexName, String indexFileName, boolean isUnique, ArrayList<String> indexFields) {
        this.indexName = indexName;
        this.indexFileName = indexFileName;
        this.isUnique = isUnique;
        this.indexFields = indexFields;
    }
}
