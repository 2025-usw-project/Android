package com.su.washcall.network;

public class MachineRequest {
    private int id;
    private int value;

    public MachineRequest(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }
}
