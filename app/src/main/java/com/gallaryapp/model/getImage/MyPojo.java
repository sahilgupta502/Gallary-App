package com.gallaryapp.model.getImage;

public class MyPojo
{
    private Result[] result;

    public Result[] getResult ()
    {
        return result;
    }

    public void setResult (Result[] result)
    {
        this.result = result;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [result = "+result+"]";
    }
}

