package com.satyam.mailer.model;

import org.apache.commons.collections4.map.HashedMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Satyam on 9/10/2016.
 */
public class Repository implements Serializable{

    private static final Long serialVersionUID = 113112312232355521L;

    private String identifier;
    private RepositoryType repositoryType;
    private Map<String, DataTable> dataTables;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public RepositoryType getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(RepositoryType repositoryType) {
        this.repositoryType = repositoryType;
    }

    public Map<String, DataTable> getDataTables() {
        return dataTables;
    }

    public void setDataTables(Map<String, DataTable> dataTables) {
        this.dataTables = dataTables;
    }

    public void addDataTables (DataTable dataTable) {
        if (this.dataTables == null) {
            this.dataTables = new HashedMap<>();
        }
        this.dataTables.put(dataTable.getId(), dataTable);
    }
}
