package com.example.teamv.object;

import java.io.Serializable;
import java.util.List;

public class Card implements Serializable {
    private String card_id;
    private String board_id;
    private String name;
    private int resource_id;
    private String description;
    private String deadline_at;
    private List<ToDoListTask> to_do_list;
    private List<AttachedFile> attached_file_list;
    private String created_at;
    private boolean is_checked_complete;
    private boolean is_pinned;
    private String status;

    public Card() {};

    public Card(String card_id, String board_id, String name, int resource_id, String description, String deadline_at, List<ToDoListTask> to_do_list, List<AttachedFile> attached_file_list, String created_at, boolean is_checked_complete, boolean is_pinned, String status) {
        this.card_id = card_id;
        this.board_id = board_id;
        this.name = name;
        this.resource_id = resource_id;
        this.description = description;
        this.deadline_at = deadline_at;
        this.to_do_list = to_do_list;
        this.attached_file_list = attached_file_list;
        this.created_at = created_at;
        this.is_checked_complete = is_checked_complete;
        this.is_pinned = is_pinned;
        this.status = status;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public boolean isIs_checked_complete() {
        return is_checked_complete;
    }

    public void setIs_checked_complete(boolean is_checked_complete) {
        this.is_checked_complete = is_checked_complete;
    }

    public boolean isIs_pinned() {
        return is_pinned;
    }

    public void setIs_pinned(boolean is_pinned) {
        this.is_pinned = is_pinned;
    }

    public String getBoard_id() {
        return board_id;
    }

    public void setBoard_id(String board_id) {
        this.board_id = board_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResource_id() {
        return resource_id;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ToDoListTask> getTo_do_list() {
        return to_do_list;
    }

    public void setTo_do_list(List<ToDoListTask> to_do_list) {
        this.to_do_list = to_do_list;
    }

    public List<AttachedFile> getAttached_file_list() {
        return attached_file_list;
    }

    public void setAttached_file_list(List<AttachedFile> attached_file_list) {
        this.attached_file_list = attached_file_list;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDeadline_at() {
        return deadline_at;
    }

    public void setDeadline_at(String deadline_at) {
        this.deadline_at = deadline_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
