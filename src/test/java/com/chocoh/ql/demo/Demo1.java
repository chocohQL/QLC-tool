package com.chocoh.ql.demo;

import com.chocoh.ql.curve.ICurve;
import com.chocoh.ql.curve.Curve;
import com.chocoh.ql.curve.ProcessorCurve;
import com.chocoh.ql.demo.data.Data1;
import com.chocoh.ql.demo.data.Data2;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author chocoh
 */
public class Demo1 {
    public static void main(String[] args) {
        // 创建曲线
        ArrayList<Data1> data1 = data1();
        ArrayList<Data2> data2 = data2();
        ICurve<Data2, Double> curve2 = new Curve<>(data2);
        ICurve<Data1, Double> curve1 = new ProcessorCurve.Builder<Data1, Double>()
                .data(data1)
                // 前后置数据处理器
                .preDataProcessor(d -> System.out.print(d.getVal() + " -> "), "p1")
                .postDataProcessor(d -> System.out.print(d.getVal() + "    "), "p2")
                // 前后置曲线处理器
                .preCurveProcessor(curve -> System.out.println("process: "), "c1")
                .postCurveProcessor(curve -> System.out.println(), "c2")
                .build();
        // 曲线计算
        curve1
                // 计算
                .process(d -> d.getVal() * 2, (d, v) -> System.out.println(v))
                // 条件计算
                .process(d -> d.getVal() > 0.5, d -> d.getVal() * 2, Data1::setVal)
                // 叠加计算
                .biProcess(curve2, (d1, d2) -> d1.getVal() + d2.getVal(), Data1::setVal)
                // 条件叠加计算
                .biProcess(curve2,
                        (d1, d2) -> d1.getVal() + d2.getVal() < 1,
                        (d1, d2) -> d1.getVal() + d2.getVal(),
                        Data1::setVal)
                // 消费
                .process(d -> System.out.print(Double.valueOf(df.format(d.getVal())).toString() + ' '));
    }

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static ArrayList<Data1> data1() {
        ArrayList<Data1> data1 = new ArrayList<>();
        data1.add(new Data1(0.1, 1, "00"));
        data1.add(new Data1(0.2, 2, "00"));
        data1.add(new Data1(0.3, 3, "00"));
        data1.add(new Data1(0.4, 4, "00"));
        return data1;
    }

    public static ArrayList<Data2> data2() {
        ArrayList<Data2> data2 = new ArrayList<>();
        data2.add(new Data2(0.2, 1, "00"));
        data2.add(new Data2(0.2, 2, "00"));
        data2.add(new Data2(0.2, 3, "00"));
        data2.add(new Data2(0.2, 4, "00"));
        return data2;
    }
}
