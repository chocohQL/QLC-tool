package com.chocoh.ql.demo.data;

import java.util.Objects;

/**
 * @author chocoh
 */
public class Data1 {
    private Double val;
    private Integer x;
    private String type;

    public Data1() {
    }

    public Data1(Double val, Integer x, String type) {
        this.val = val;
        this.x = x;
        this.type = type;
    }

    public Double getVal() {
        return val;
    }

    public void setVal(Double val) {
        this.val = val;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Data1{" +
                "val=" + val +
                ", x=" + x +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data1 data1 = (Data1) o;
        return Objects.equals(val, data1.val) && Objects.equals(x, data1.x) && Objects.equals(type, data1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val, x, type);
    }
}
