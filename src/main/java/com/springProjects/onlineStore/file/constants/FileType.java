package com.springProjects.onlineStore.file.constants;

public enum FileType {
    USER_IMAGE("userImage"),
    CATEGORY_COVER_IMAGE("categoryCoverImage"),
    PRODUCT_IMAGE("productImage");

    public final String value;

    FileType(String value) {
        this.value = value;
    }
}
