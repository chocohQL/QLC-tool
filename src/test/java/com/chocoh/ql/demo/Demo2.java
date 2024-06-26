package com.chocoh.ql.demo;

import com.chocoh.ql.group.CurveGroup;
import com.chocoh.ql.group.ICurveGroup;
import com.chocoh.ql.demo.data.Data1;
import com.chocoh.ql.demo.data.Data2;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author chocoh
 */
public class Demo2 {
    public static void main(String[] args) {
        // 创建曲线分组
        ArrayList<Data1> data1 = data1();
        ArrayList<Data2> data2 = data2();
        ICurveGroup<String, Data1, Double> curveGroup1 = CurveGroup.create(data1, Data1::getType);
        ICurveGroup<String, Data2, Double> curveGroup2 = CurveGroup.create(data2, Data2::getType);
        // 分组计算
        curveGroup1
                // 分组计算
                .process((Key, d) -> d.getVal() + 0.1, Data1::setVal)
                // 分组条件计算
                .process((key, d) -> d.getVal() < 0.4, (Key, d) -> d.getVal() + 0.1, Data1::setVal)
                // 分组叠加计算
                .biProcess(curveGroup2, (key, d1, d2) -> d1.getVal() + d2.getVal(), Data1::setVal)
                // 分组条件叠加计算
                .biProcess(curveGroup2,
                        (key, d1, d2) -> d2.getVal() < 0.2 || key.equals("00"),
                        (key, d1, d2) -> d1.getVal() * 2,
                        Data1::setVal)
                // 得到曲线
                .forCurve(curveGroup2, (key, curve1, curve2) -> {
                    System.out.println(key + ":");
                    curve1
                            .process(d -> Double.valueOf(df.format(d.getVal())), Data1::setVal)
                            .process(d -> System.out.print(d.getVal() + "\t"));
                    System.out.println();
                    curve2
                            .process(d -> System.out.print(d.getVal() + "\t"));
                    System.out.println();
                });
    }

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static ArrayList<Data1> data1() {
        ArrayList<Data1> data1 = new ArrayList<>();
        data1.add(new Data1(0.1, 1, "00"));
        data1.add(new Data1(0.2, 2, "00"));
        data1.add(new Data1(0.3, 3, "00"));
        data1.add(new Data1(0.4, 4, "00"));

        data1.add(new Data1(0.4, 1, "01"));
        data1.add(new Data1(0.3, 2, "01"));
        data1.add(new Data1(0.2, 3, "01"));
        data1.add(new Data1(0.1, 4, "01"));

        data1.add(new Data1(0.1, 1, "10"));
        data1.add(new Data1(0.4, 2, "10"));
        data1.add(new Data1(0.3, 3, "10"));
        data1.add(new Data1(0.2, 4, "10"));
        return data1;
    }

    public static ArrayList<Data2> data2() {
        ArrayList<Data2> data2 = new ArrayList<>();
        data2.add(new Data2(0.1, 1, "00"));
        data2.add(new Data2(0.1, 2, "00"));
        data2.add(new Data2(0.1, 3, "00"));
        data2.add(new Data2(0.1, 4, "00"));

        data2.add(new Data2(0.2, 1, "01"));
        data2.add(new Data2(0.2, 2, "01"));
        data2.add(new Data2(0.2, 3, "01"));
        data2.add(new Data2(0.2, 4, "01"));

        data2.add(new Data2(0.1, 1, "10"));
        data2.add(new Data2(0.2, 2, "10"));
        data2.add(new Data2(0.2, 3, "10"));
        data2.add(new Data2(0.1, 4, "10"));
        return data2;
    }
}
