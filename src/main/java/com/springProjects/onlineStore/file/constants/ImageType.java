package com.springProjects.onlineStore.file.constants;

public enum ImageType {
    JPG("image/jpg"),
    JPEG("image/jpeg"),
    PNG("image/png");

    public final String value;

    ImageType(String value) {
        this.value = value;
    }
}
