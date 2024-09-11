package com.chocoh.ql.demo;

import com.chocoh.ql.curve.ICurve;
import com.chocoh.ql.curve.Curve;
import com.chocoh.ql.demo.data.Data1;
import com.chocoh.ql.demo.data.Data2;
import com.chocoh.ql.group.CurveGroup;
import com.chocoh.ql.group.ICurveGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author chocoh
 */
public class MainTest {
    public static void main(String[] args) {
        testCurve();
        testCurveGroup();
    }

    public static void testCurve() {
        System.out.println("--- 测试曲线 ---");
        // 获取数据集
        ArrayList<Data1> data1 = data1();
        ArrayList<Data2> data2 = data2();
        // 创建曲线
        ICurve<Data1, Double> curve1 = new Curve<>(data1);
        ICurve<Data2, Double> curve2 = new Curve<>(data2);
        // 曲线计算
        curve1
                // 单曲线计算
                .process(d -> d.getVal() * 2, Data1::setVal)
                // 单曲线条件计算
                .process(d -> d.getVal() > 0.8, d -> d.getVal() * 2, Data1::setVal)
                // 两条曲线叠加
                .biProcess(curve2, (d1, d2) -> d1.getVal() + d2.getVal(), Data1::setVal)
                // 两条曲线条件叠加
                .biProcess(curve2,
                        (d1, d2) -> d1.getVal() + d2.getVal() < 1,
                        (d1, d2) -> d1.getVal() + d2.getVal(),
                        Data1::setVal)
                // 打印消费
                .process(d -> System.out.print(Double.valueOf(df.format(d.getVal())) + " "));
        System.out.println();
    }

    public static void testCurveGroup() {
        System.out.println("--- 测试分组曲线 ---");
        // 获取数据集
        ArrayList<Data1> data1 = data3();
        ArrayList<Data2> data2 = data4();
        // 创建曲线分组
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
                        (key, d1, d2) -> d2.getVal() < 0.2 || key.equals("group1"),
                        (key, d1, d2) -> d1.getVal() * 2,
                        Data1::setVal)
                // 得到分组曲线
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
        System.out.println();
    }

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static ArrayList<Data1> data1() {
        return new ArrayList<>(Arrays.asList(
                new Data1(1, 0.1),
                new Data1(2, 0.2),
                new Data1(3, 0.3),
                new Data1(4, 0.4),
                new Data1(5, 0.5)
        ));
    }

    public static ArrayList<Data2> data2() {
        return new ArrayList<>(Arrays.asList(
                new Data2(1, 0.2),
                new Data2(2, 0.2),
                new Data2(3, 0.2),
                new Data2(4, 0.2),
                new Data2(5, 0.2)
        ));
    }

    public static ArrayList<Data1> data3() {
        return new ArrayList<>(Arrays.asList(
                new Data1(1, 0.1, "group1"),
                new Data1(2, 0.2, "group1"),
                new Data1(3, 0.3, "group1"),
                new Data1(4, 0.4, "group1"),
                new Data1(1, 0.4, "group2"),
                new Data1(2, 0.3, "group2"),
                new Data1(3, 0.2, "group2"),
                new Data1(4, 0.1, "group2"),
                new Data1(1, 0.1, "group3"),
                new Data1(2, 0.4, "group3"),
                new Data1(3, 0.3, "group3"),
                new Data1(4, 0.2, "group3")
        ));
    }

    public static ArrayList<Data2> data4() {
        return new ArrayList<>(Arrays.asList(
                new Data2(1, 0.1, "group1"),
                new Data2(2, 0.1, "group1"),
                new Data2(3, 0.1, "group1"),
                new Data2(4, 0.1, "group1"),
                new Data2(1, 0.2, "group2"),
                new Data2(2, 0.2, "group2"),
                new Data2(3, 0.2, "group2"),
                new Data2(4, 0.2, "group2"),
                new Data2(1, 0.1, "group3"),
                new Data2(2, 0.2, "group3"),
                new Data2(3, 0.2, "group3"),
                new Data2(4, 0.1, "group3")
        ));
    }
}
