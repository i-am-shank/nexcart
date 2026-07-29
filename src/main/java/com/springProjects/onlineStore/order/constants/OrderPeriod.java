package com.springProjects.onlineStore.order.constants;

public enum OrderPeriod {
    LAST_1_WEEK("last 1 week"),
    LAST_30_DAYS("last 30 days"),
    LAST_3_MONTHS("last 3 months"),
    LAST_1_YEAR("last 1 year");

    public final String value;

    OrderPeriod(String value) {
        this.value = value;
    }
}
