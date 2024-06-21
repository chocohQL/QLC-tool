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

## 基本用法

### 创建曲线

ICurve 继承了 List ，你可以直接将它当作普通的 List 使用。Curve 本身是 ArrayList 的增强，之所以使用 ArrayList，是因为多曲线叠加操作时需要频繁访问索引，且推荐创建曲线后不随意增减元素。

```java
List<Data1> data = getData();
ICurve<Data1, Double> curve2 = new Curve<>(data);
```

曲线需要两个泛型，第一个表示存储的数据本身，第二个表示曲线计算过程的返回值类型，即 Y 轴的数值类型。

### 曲线处理

ICurve 定义了基本曲线处理方法，主要为单曲线处理和多曲线叠加处理。

process 方法针对的是曲线每一个元素，第一个表达式产生结果，并通过第二个表达式进行消费，三个参数的方法为条件计算。

如果你想直接修改曲线数据，可直接传递 set 方法引用，也可以手动编写匿名内部类进行赋值和其他操作，第二个表达式提供的就是原数据和第一个表达式产生的结果。

```java
// 曲线计算
curve.process(d -> d.getVal() * 2, Data1::setVal);

curve.process(d -> d.getVal() * 2, (d, v) -> System.out.println(v));

// 条件计算（ val 属性大于 0.5 才执行后续操作）
curve.process(d -> d.getVal() > 0.5, d -> d.getVal() * 2, Data1::setVal)
```

如果你不想要拆分条件、处理和消费逻辑，那么你也可以直接使用单一参数的 process 方法。

```java
curve.process(d -> d.setVal(d.getVal() * 2))
        
curve.process(d -> {
        // ...
        d.setVal(d.getVal() * 2);
        // ...
    });
```

### 多曲线叠加处理

如果你需要对两条曲线进行叠加处理，并且它们的数据一一对应（横轴对应），那么可以使用 biProcess 方法，它们的元素类型可以不相同。

biProcess 的使用逻辑和 process 相似，只不过需要传递另一条曲线，拿到两个对应的数据，结果消费只能拿到调用方的数据，如果想同时修改另一条曲线的数据，可以直接在计算表达式中操作。

```java
// 创建曲线1
ArrayList<Data1> data1 = data1();
ICurve<Data2, Double> curve2 = new Curve<>(data2);
// 创建曲线2
ArrayList<Data2> data2 = data2();
ICurve<Data1, Double> curve1 = new Curve<>(data1);

curve1
        // 叠加计算
        .biProcess(curve2, (d1, d2) -> d1.getVal() + d2.getVal(), Data1::setVal)
        // 条件叠加计算
        .biProcess(curve2,
                (d1, d2) -> d1.getVal() + d2.getVal() < 1,
                (d1, d2) -> d1.getVal() + d2.getVal(),
                Data1::setVal)
```

如果你想同时叠加多个曲线，那么可以使用 multiProcess 方法。

### 创建分组曲线

何时需要使用分组曲线？有时需要从数据库中一次性查询出多条曲线，它们可能是根据某个列进行区分，例如横轴为时间的数据曲线，根据日期进行分组，又或者是多个城市的统计曲线。如果你需要对不同组别的曲线进行不同的叠加操作，那么使用分组曲线是一个不错的选择。

ICurveGroup 继承了 Map ，它有三个泛型，第一个为 Key 的类型，后两个与 Curve 一致，其实现类 CurveGroup 提供了创建分组曲线的方法，需要传入数据集合和进行分组的规则。需要注意的是分组并不是指一个 key 对应了多条曲线，分组是针对的数据，因此它实际上是 Map<K, ICurve<T, V>> 的形式。

下面的方法为根据数据的 type 属性进行分组。

```java
ArrayList<Data1> data1 = data1();
ICurveGroup<String, Data1, Double> curveGroup1 = CurveGroup.create(data1, Data1::getType);
```

### 分组曲线操作

分组曲线的处理逻辑和 ICurve 类似，它会多提供一个 key 辅助你进行自定义的分组计算，如果是叠加另一个分组，它默认是相同分组的曲线进行叠加处理。

```java
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
```

有时候并不想直接对相同分组的具体数据进行操作，而是想操作对应的曲线，那么你也可以使用 forCurve 方法调出对应分组的曲线来。

```java
curveGroup1.forCurve((key, curve1) -> System.out.println(key + ":" + curve1))

curveGroup1.forCurve(curveGroup2, (key, curve1, curve2) -> {
            System.out.println(key + ":");
            curve1
                    .process(d -> Double.valueOf(df.format(d.getVal())), Data1::setVal)
                    .process(d -> System.out.print(d.getVal() + "\t"));
            System.out.println();
            curve2
                    .process(d -> System.out.print(d.getVal() + "\t"));
            System.out.println();
        });
```

### ProcessorCurve

ProcessorCurve 为 Curve 的增强，提供了前后置处理器链，你可以添加多个处理器。数据处理器在每一个数据处理前后触发，拿到的是曲线数据；线处理器在一次曲线处理前后触发，拿到的是曲线本身。

```java
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
    // 是否开启前后置数据处理器（默认开启）
    .enableDataProcessor(true)
    // 是否开启前后置曲线处理器（默认开启）
    .enableCurveProcessor(true)
    .build();
```