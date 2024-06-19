package com.chocoh.ql.demo;

import com.chocoh.ql.core.curve.ICurve;
import com.chocoh.ql.core.curve.Curve;
import com.chocoh.ql.demo.data.Data1;
import com.chocoh.ql.demo.data.Data2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author chocoh
 */
public class Demo1 {
    public static void main(String[] args) {
        // 准备数据
        ArrayList<Data1> d1 = data1();
        ArrayList<Data2> d2 = data2();
        // 创建曲线（继承了List）
        ICurve<Data1, Double> curve1 = Curve.create(d1);
        ICurve<Data2, Double> curve2 = Curve.create(d2);
        // 曲线计算
        curve1
                // 计算
                .process(c -> c.getVal() * 2, Data1::setVal)
                // 条件计算
                .process(c -> c.getVal() > 0.5, c -> c.getVal() * 2, Data1::setVal)
                // 叠加计算
                .biProcess(curve2, (c1, c2) -> c1.getVal() + c2.getVal(), Data1::setVal)
                // 条件叠加计算
                .biProcess(curve2,
                        (c1, c2) -> c1.getVal() + c2.getVal() < 1,
                        (c1, c2) -> c1.getVal() + c2.getVal(),
                        Data1::setVal)
                // 消费
                .process(c -> System.out.print(Double.valueOf(df.format(c.getVal())).toString() + ' '));
        // processWhile biProcessWhile sort
    }

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static ArrayList<Data1> data1() {
        ArrayList<Data1> d1 = new ArrayList<>();
        d1.add(new Data1(0.1, 1, "00"));
        d1.add(new Data1(0.2, 2, "00"));
        d1.add(new Data1(0.3, 3, "00"));
        d1.add(new Data1(0.4, 4, "00"));
        return d1;
    }

    public static ArrayList<Data2> data2() {
        ArrayList<Data2> d2 = new ArrayList<>();
        d2.add(new Data2(0.2, 1, "00"));
        d2.add(new Data2(0.2, 2, "00"));
        d2.add(new Data2(0.2, 3, "00"));
        d2.add(new Data2(0.2, 4, "00"));
        return d2;
    }
}
