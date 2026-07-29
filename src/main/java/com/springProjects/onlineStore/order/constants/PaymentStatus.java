package com.springProjects.onlineStore.order.constants;

public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    FAILED("Failed"),
    REFUND_IN_PROCESS("Refund in Process"),
    REFUNDED("Refunded");

    public final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public boolean canTransitionTo(PaymentStatus updatedStatus) {
        return switch (this) {
            case PENDING -> (PAID.equals(updatedStatus) || FAILED.equals(updatedStatus));
            case PAID -> (REFUND_IN_PROCESS.equals(updatedStatus));
            case FAILED -> PENDING.equals(updatedStatus);
            case REFUND_IN_PROCESS -> (REFUNDED.equals(updatedStatus));
            default -> false;
        };
    }
}
