package com.example.harshjain.chitchatz;

/**
 * Created by Harsh Jain on 30-01-2019.
 */

public class Request {

    public String name;
    public String image;
    public Request()
    {

    }
    public Request(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
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

}
