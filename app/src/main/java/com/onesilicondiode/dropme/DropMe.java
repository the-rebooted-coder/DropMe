package com.onesilicondiode.dropme;

public class DropMe {
    private String myUID;
    private String imageUrl;

    public DropMe(){

    }

    public String getMyUID() {
        return myUID;
    }

    public void setMyUID(String employeeName) {
        this.myUID = employeeName;
    }
    public DropMe(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
