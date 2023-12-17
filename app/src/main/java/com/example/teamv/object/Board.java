package com.example.teamv.object;

import java.io.Serializable;

public class Board implements Serializable {
    private String id;
    private String name;
    private int resource_id;
    private String created_at;
    public Board() {};
    public Board(String id, String name, int resource_id, String created_at) {
        this.id = id;
        this.name = name;
        this.resource_id = resource_id;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public int getResource_id() {
        return resource_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
