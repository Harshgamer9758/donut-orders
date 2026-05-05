package com.blockmart.donutorders.models;

public class DonutOrder {
    private final String playerUuid;
    private final String donutFlavor;
    private final String donutTopping;
    private final double cost;
    private final long orderTime;

    public DonutOrder(String playerUuid, String donutFlavor, String donutTopping, double cost, long orderTime) {
        this.playerUuid = playerUuid;
        this.donutFlavor = donutFlavor;
        this.donutTopping = donutTopping;
        this.cost = cost;
        this.orderTime = orderTime;
    }

    public String getPlayerUuid() {
        return playerUuid;
    }

    public String getDonutFlavor() {
        return donutFlavor;
    }

    public String getDonutTopping() {
        return donutTopping;
    }

    public double getCost() {
        return cost;
    }

    public long getOrderTime() {
        return orderTime;
    }
}