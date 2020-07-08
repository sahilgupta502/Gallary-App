package com.gallaryapp.model.getImage;

public class Result {private String images;

    private String id;

    public String getImages ()
    {
        return images;
    }

    public void setImages (String images)
    {
        this.images = images;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [images = "+images+", id = "+id+"]";
    }
}

