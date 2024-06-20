# QLC-tool

> 失手扑空蓝色的蜻蛉
>
> Author: [chocohQL](https://github.com/chocohQL)
>
> GitHub: [https://github.com/chocohQL/QLC-tool](https://github.com/chocohQL/QLC-tool)

## 什么是 QLC-tool

QLC-tool 是一个轻量级曲线计算模板，使用函数式编程的方式定义曲线计算流程，包括自定义公式计算、多曲线叠加、曲线分组、前后置处理器等功能，在一些场景下可以极大提高开发效率，简化代码。

![](https://fastly.jsdelivr.net/gh/chocohQL/ql-file@main/assets/githubQLC-tool-01.png)

## 什么时候使用 QLC-tool

在一些数据可视化平台中，需要计算生成不同的曲线进行展示，这些曲线的基础数据经常是储存在数据库中，例如横轴为时间的数据曲线，在系统中，你可能需要根据一些变化的参数对曲线进行计算，有时又需要叠加不同类型的曲线，同一种曲线的数据集也可能会根据字段被分为不同的组，如果使用原有的集合或 Stream 流等编写业务代码，需要消耗大量精力维护数据关系，并且经常会导致中间变量集合、循环遍历等满天飞。

QLC-tool 就是为了解决这些问题，使用它可以很轻松的编写曲线计算模板，如果熟悉函数式编程，可以极大提高编写类似代码的效率，更关注数据处理而不是维护数据关系。

## 使用示例

### 曲线模板

```java
/**
 * 【曲线测试】
 *
 * <p>曲线接口 IGroup 继承了 List , 实现类 Curve 继承了 ArrayList , 可以当作普通 List 使用.</p>
 * <p>1. 通过静态 create 方法创建一个曲线, 需要传入数据集合.</p>
 * <p>2. 使用函数式编程的方式实现曲线的自定义公式计算、处理、多曲线叠加等操作.</p>
 * <p>3. 基本操作针对单条数据的, 如果曲线叠加操作则是对相同下标的数据进行处理（默认两条曲线长度相同）.</p>
 * <p>4. 两条曲线的数据可以是不同的类, 即不同类型的曲线也可以进行操作, 但需要提前规定返回值类型.</p>
 * <p>5. Curve 只提供了基础的曲线操作, ProcessorCurve 提供了前后置处理器增强.</p>
 *
 * @author chocoh
 */
public class Demo1 {
    public static void main(String[] args) {
        // 准备数据
        ArrayList<Data1> data1 = data1();
        ArrayList<Data2> data2 = data2();
        // 创建曲线1
        ICurve<Data2, Double> curve2 = Curve.create(data2);
        // 创建曲线2
        ICurve<Data1, Double> curve1 = new ProcessorCurve.Builder<Data1, Double>()
                // 数据
                .data(data1)
                // 前置数据处理器
                .preDataProcessor(d -> System.out.print(d.getVal() + " -> "), "p1")
                // 后置数据处理器
                .postDataProcessor(d -> System.out.print(d.getVal() + "    "), "p2")
                // 前置曲线处理器
                .preCurveProcessor(curve -> System.out.println("process: "), "c1")
                // 后置曲线处理器
                .postCurveProcessor(curve -> System.out.println(), "c2")
                .build();
        // 曲线计算（实现了List）
        curve1
                // 计算
                .process(d -> d.getVal() * 2, Data1::setVal)
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
```

### 分组模板

```java
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
                // 分组叠加计算
                .biProcess(curveGroup2, (key, d1, d2) -> d1.getVal() + d2.getVal(), Data1::setVal)
                // 分组条件叠加计算
                .biProcess(curveGroup2,
                        (key, d1, d2) -> d1.getVal() + d2.getVal() > 0.5 || key.equals("00"),
                        (key, d1, d2) -> d1.getVal() * 2,
                        Data1::setVal)
                // 曲线分组
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
```