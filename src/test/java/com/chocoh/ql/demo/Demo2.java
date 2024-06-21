package com.chocoh.ql.demo;

import com.chocoh.ql.group.CurveGroup;
import com.chocoh.ql.group.ICurveGroup;
import com.chocoh.ql.demo.data.Data1;
import com.chocoh.ql.demo.data.Data2;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 【分组曲线测试】
 *
 * <p>分组曲线接口 ICurveGroup 继承了 Map , 实现类 CurveGroup 继承了 HashMap , 可以当作普通 Map 使用.</p>
 * <p>1. 通过静态 create 方法创建一个分组, 需要传入数据集合和分组规则. </p>
 * <p>2. 通过与 Curve 类似的函数式调用可以实现分组处理, 注意函数式接口会多给出每组的 Key .</p>
 * <p>3. 基本操作与 Curve 一样是针对单条数据的, 只不过它进行了分组, 当然你也可以用 forCurve 方法直接调出对应分组的曲线.</p>
 *
 * @author chocoh
 */
public class Demo2 {
    public static void main(String[] args) {
        // 准备数据
        ArrayList<Data1> data1 = data1();
        ArrayList<Data2> data2 = data2();
        // 创建曲线分组1
        ICurveGroup<String, Data1, Double> curveGroup1 = CurveGroup.create(data1, Data1::getType);
        // 创建曲线分组2
        ICurveGroup<String, Data2, Double> curveGroup2 = CurveGroup.create(data2, Data2::getType);
        // 分组计算（实现了Map）
        curveGroup1
                // 分组计算
                .process((Key, d) -> d.getVal() + 0.1, Data1::setVal)
                // 分组条件计算
                .process((key, d) -> d.getVal() > 0.5, (Key, d) -> d.getVal() + 0.1, Data1::setVal)
                // 分组叠加计算
                .biProcess(curveGroup2, (key, d1, d2) -> d1.getVal() + d2.getVal(), Data1::setVal)
                // 分组条件叠加计算
                .biProcess(curveGroup2,
                        (key, d1, d2) -> d1.getVal() + d2.getVal() > 0.5 || key.equals("00"),
                        (key, d1, d2) -> d1.getVal() * 2,
                        Data1::setVal)
                // 调出分组曲线
                .forCurve((key, curve1) -> System.out.println(key + ":" + curve1))
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
        data2.add(new Data2(0.2, 2, "00"));
        data2.add(new Data2(0.3, 3, "00"));
        data2.add(new Data2(0.4, 4, "00"));

        data2.add(new Data2(0.4, 1, "01"));
        data2.add(new Data2(0.3, 2, "01"));
        data2.add(new Data2(0.2, 3, "01"));
        data2.add(new Data2(0.1, 4, "01"));

        data2.add(new Data2(0.2, 1, "10"));
        data2.add(new Data2(0.2, 2, "10"));
        data2.add(new Data2(0.2, 3, "10"));
        data2.add(new Data2(0.2, 4, "10"));
        return data2;
    }
}
