package com.example.teamv.object;

import java.io.Serializable;

public class AttachedFile implements Serializable {
    private String name;
    private String created_at;
    private String size;
    private String url;
    private String format;
    private String extension;

    AttachedFile() {};

    public AttachedFile(String name, String created_at, String size, String url, String format, String extension) {
        this.name = name;
        this.created_at = created_at;
        this.size = size;
        this.url = url;
        this.format = format;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
