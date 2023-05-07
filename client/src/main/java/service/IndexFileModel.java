package service;

import lombok.Data;

import java.util.ArrayList;

@Data
public class IndexFileModel {
    private String indexName, indexFileName;

    private boolean isUnique;

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
