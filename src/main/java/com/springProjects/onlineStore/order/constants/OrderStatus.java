package com.springProjects.onlineStore.order.constants;

// Value used only in visualizing for OrderStatus-stage
public enum OrderStatus {
    CANCELLED("Cancelled"),
    PENDING("Pending"),
    DISPATCHED("Dispatched"),
    DELIVERED("Delivered"),
    RETURN_STARTED("Return Started"),
    RETURNED("Returned"),;

    public final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public boolean canTransitionTo(OrderStatus updatedStatus) {
        switch(this) {
            case PENDING -> {
                return (DISPATCHED.equals(updatedStatus) || CANCELLED.equals(updatedStatus));
            }
            case DISPATCHED -> {
                return (DELIVERED.equals(updatedStatus));
            }
            case DELIVERED -> {
                return (RETURN_STARTED.equals(updatedStatus));
            }
            case RETURN_STARTED -> {
                return (RETURNED.equals(updatedStatus));
            }
            default -> {
                // CANCELLED also coming in this case , can't be updated
                return false;
            }
        }
    }
}
