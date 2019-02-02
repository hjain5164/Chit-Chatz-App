package com.example.harshjain.chitchatz;

/**
 * Created by Harsh Jain on 06-01-2019.
 */

public class Users {

    public String name;
    public String image;
    public String status;
    private String thumb_image;
    public Users()
    {

    }
    public Users(String name, String image, String status, String thumb_image) {
        this.name = name;
        this.image = image;
        this.thumb_image = thumb_image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}