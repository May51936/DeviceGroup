package com.facesec.devicegroup.deviceGroupLib;

public class PeopleData {
    private int in;
    private int out;
    private int total;

    public PeopleData(int in, int out, int total) {
        this.in = in;
        this.out = out;
        this.total = total;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
