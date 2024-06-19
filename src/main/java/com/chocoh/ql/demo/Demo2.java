package com.chocoh.ql.demo;

import com.chocoh.ql.core.curve.CurveGroup;
import com.chocoh.ql.core.curve.ICurveGroup;
import com.chocoh.ql.demo.data.Data1;
import com.chocoh.ql.demo.data.Data2;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author chocoh
 */
public class Demo2 {
    public static void main(String[] args) {
        // 准备数据
        ArrayList<Data1> d1 = data1();
        ArrayList<Data2> d2 = data2();
        // 创建曲线分组（继承了Map）
        ICurveGroup<String, Data1, Double> g1 = CurveGroup.create(d1, Data1::getType);
        ICurveGroup<String, Data2, Double> g2 = CurveGroup.create(d2, Data2::getType);
        // 分组计算
        g1
                // 分组叠加计算
                .biProcess(g2, (key, c1, c2) -> c1.getVal() + c2.getVal(), Data1::setVal)
                // 分组条件计算
                .biProcess(g2,
                        (key, c1, c2) -> c1.getVal() + c1.getVal() > 1 || key.equals("00"),
                        (key, c1, c2) -> c1.getVal() * 2,
                        Data1::setVal)
                // 分组处理
                .process((key, curve) -> {
                    System.out.print("\n" + key + ": ");
                    // 曲线计算...
                    curve
                            .process(c -> Double.valueOf(df.format(c.getVal())), Data1::setVal)
                            .process(c -> System.out.print(c.getVal().toString() + " "));
                });
    }

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static ArrayList<Data1> data1() {
        ArrayList<Data1> d1 = new ArrayList<>();
        d1.add(new Data1(0.1, 1, "00"));
        d1.add(new Data1(0.2, 2, "00"));
        d1.add(new Data1(0.3, 3, "00"));
        d1.add(new Data1(0.4, 4, "01"));

        d1.add(new Data1(0.4, 1, "01"));
        d1.add(new Data1(0.3, 2, "01"));
        d1.add(new Data1(0.2, 3, "01"));
        d1.add(new Data1(0.1, 4, "01"));

        d1.add(new Data1(0.1, 1, "10"));
        d1.add(new Data1(0.4, 2, "10"));
        d1.add(new Data1(0.3, 3, "10"));
        d1.add(new Data1(0.2, 4, "10"));
        return d1;
    }

    public static ArrayList<Data2> data2() {
        ArrayList<Data2> d2 = new ArrayList<>();
        d2.add(new Data2(0.1, 1, "00"));
        d2.add(new Data2(0.2, 2, "00"));
        d2.add(new Data2(0.3, 3, "00"));
        d2.add(new Data2(0.4, 4, "01"));

        d2.add(new Data2(0.4, 1, "01"));
        d2.add(new Data2(0.3, 2, "01"));
        d2.add(new Data2(0.2, 3, "01"));
        d2.add(new Data2(0.1, 4, "01"));

        d2.add(new Data2(0.2, 1, "10"));
        d2.add(new Data2(0.2, 2, "10"));
        d2.add(new Data2(0.2, 3, "10"));
        d2.add(new Data2(0.2, 4, "10"));
        return d2;
    }
}
