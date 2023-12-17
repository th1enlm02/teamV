package com.example.teamv.object;

import java.util.List;

public class Card {
    private String name;
    private int resource_id;
    private String description;
    private List<Tag> label;
    private List<Task> to_do_list;
    private List<String> attached_file_list;
    private String created_at;
    private String deadline_at;
    private String status;

    public Card(String name, int resource_id, String description, List<Tag> label, List<Task> to_do_list, List<String> attached_file_list, String created_at, String deadline_at, String status) {
        this.name = name;
        this.resource_id = resource_id;
        this.description = description;
        this.label = label;
        this.to_do_list = to_do_list;
        this.attached_file_list = attached_file_list;
        this.created_at = created_at;
        this.deadline_at = deadline_at;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getResource_id() {
        return resource_id;
    }

    public String getDescription() {
        return description;
    }

    public List<Tag> getLabel() {
        return label;
    }

    public List<Task> getTo_do_list() {
        return to_do_list;
    }

    public List<String> getAttached_file_list() {
        return attached_file_list;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getDeadline_at() {
        return deadline_at;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLabel(List<Tag> label) {
        this.label = label;
    }

    public void setTo_do_list(List<Task> to_do_list) {
        this.to_do_list = to_do_list;
    }

    public void setAttached_file_list(List<String> attached_file_list) {
        this.attached_file_list = attached_file_list;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setDeadline_at(String deadline_at) {
        this.deadline_at = deadline_at;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
