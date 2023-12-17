package com.example.teamv.object;

public class Tag {
    private String name;
    private String color_code;

    public Tag(String name, String color_code) {
        this.name = name;
        this.color_code = color_code;
    }

    public String getName() {
        return name;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }
}
