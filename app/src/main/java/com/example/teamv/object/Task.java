package com.example.teamv.object;

public class Task {
    private String name;
    private boolean is_checked;

    public Task(String name, boolean is_checked) {
        this.name = name;
        this.is_checked = is_checked;
    }

    public String getName() {
        return name;
    }

    public boolean isIs_checked() {
        return is_checked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }
}
