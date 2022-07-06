package model;

import java.util.Objects;

public class Order {

    private Integer price;

    private int size;

    private Status status;

    public Order() {
    }

    public Order(Integer price, int size, Status status) {
        this.price = price;
        this.size = size;
        this.status = status;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return size == order.size && Objects.equals(price, order.price) && status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, size, status);
    }

    @Override
    public String toString() {
        return price + "," + size;
    }
}
