# QLC-tool

## 简介

QLC-tool 是一个曲线计算模板工具，使用函数式编程的方式定义曲线计算流程，包括自定义公式计算、多曲线叠加计算、曲线分组计算、前后置处理器等功能，在一些场景下可以极大提高开发效率，简化代码，使开发者专注数据处理而不是维护数据关系。

![](https://fastly.jsdelivr.net/gh/chocohQL/ql-file@main/assets/githubQLC-tool-08.svg)

## 使用场景

在一些数据可视化平台中，经常需要计算生成不同曲线进行展示，在编写业务代码时你可能会遇下面这些情况：

+ 曲线数据存储在两张不同的表中，数据集合类型不一致，例如数据曲线与参数曲线叠加计算
+ 一次性查询出多条曲线数据需要分组后进行操作，例如随时间变化的曲线，按照每一天的数据绘制一条曲
+ 使用 Stream 流或原生集合遍历的方法进行曲线处理，需要编写很多与核心计算无关的过程来维护数据集合关系
+ 需要进行代码复用、中间过程记录、日志记录等，编写复杂 SQL 或调用其他服务难以满足业务需求

这些场景下使用 QLC-tool 曲线模板工具重构业务代码前后，看起来是这样的：

![](https://fastly.jsdelivr.net/gh/chocohQL/ql-file@main/assets/githubQLC-tool-02.png)

## 基本用法

> 工具无其他依赖，可以修改包名后直接复制源码目录到项目中使用或者通过 Maven 安装到本地仓库后引用依赖

### 创建曲线

ICurve 定义了曲线计算模板方法，它继承了 List ，可以直接当作普通的 List 集合使用。Curve 是该接口的通用实现类，它本身是 ArrayList 的增强。使用 ArrayList 是因为曲线操作需要频繁访问索引，推荐创建曲线后不随意增减元素。

```java
List<Data1> data = getData();
ICurve<Data1, Double> curve2 = new Curve<>(data);
```

ICurve 需要两个泛型，第一个表示存储数据本身的类型，第二个表示曲线数据用于计算的数值类型，也是曲线计算过程的返回值类型。

```java
public class Data1 {
    private Double val;
    private Integer x;
    private String type;
...
```

### 单曲线处理

process 方法针对的是曲线每一个元素，第一个表达式产生结果，第二个表达式对第一个表达式产生的结果进行消费，这个方法实际上是分离了处理和消费的逻辑，第一个表达式的结果并不会真正修改数据，你可以直接在第二个参数中传递 set 方法引用进行赋值，也可以扩展 lambda 表达式手动赋值或进行其他操作。

```java
// 曲线计算 ICurve<T, V> process(Function<T, V> f, BiConsumer<T, V> biC);
curve
        .process(d -> d.getVal() * 2, Data1::setVal);
        .process(d -> d.getVal() * 2, (d, v) -> System.out.println(v));
```

三个参数的 process 方法为需要传递条件断言，满足条件的才会执行后续定义的计算逻辑

```java
// 条件计算 ICurve<T, V> process(Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC)
curve.process(d -> d.getVal() > 0.5, d -> d.getVal() * 2, Data1::setVal)
```

如果你不想要拆分条件、处理和消费逻辑，那么你也可以直接使用单一参数的 process 方法。

```java
// ICurve<T, V> process(Consumer<T> c);
curve.process(d -> {
        // ...
        d.setVal(d.getVal() * 2);
        // ...
    });
```

### 多曲线叠加处理

单曲线处理可以直接使用 Stream 流实现，而多曲线叠加处理才是该工具的核心功能。

![](https://fastly.jsdelivr.net/gh/chocohQL/ql-file@main/assets/githubQLC-tool-03.svg)

如果你需要对两条曲线进行叠加处理，并且它们的在曲线集合中的数据一一对应，那么可以使用 biProcess 方法。biProcess 的使用逻辑和 process 相似，你可以传递不同类型的曲线集合，但是用于计算的泛型需要一致。

计算过程中你可以拿到两个曲线一一对应的数据，但是消费结果时只能拿到调用方的曲线数据，如果想同时修改另一条曲线的数据，可以直接在计算表达式中手动操作。

```java
// 创建曲线
ArrayList<Data1> data1 = data1();
ArrayList<Data2> data2 = data2();
ICurve<Data2, Double> curve2 = new Curve<>(data2);
ICurve<Data1, Double> curve1 = new Curve<>(data1);
// <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);
// <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);
curve1
        // 叠加计算
        .biProcess(curve2, (d1, d2) -> d1.getVal() + d2.getVal(), Data1::setVal)
        // 条件叠加计算
        .biProcess(curve2,
                (d1, d2) -> 1 <= d1.getVal() * d2.getVal(),
                (d1, d2) -> d1.getVal() + d2.getVal(),
                Data1::setVal)
```

如果你想同时叠加多个曲线，那么可以使用 multiProcess 方法。

```java
<U> ICurve<T, V> multiProcess(List<ICurve<U, V>> cs, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

<U> ICurve<T, V> multiProcess(List<ICurve<U, V>> cs, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC)
```

### 创建分组曲线

如果你需要将数据分为多个曲线，且需要对不同组的曲线进行不同的叠加操作，那么使用分组曲线是一个不错的选择。

![](https://fastly.jsdelivr.net/gh/chocohQL/ql-file@main/assets/githubQLC-tool-06.svg)

ICurveGroup 继承了 Map ，它有三个泛型，第一个为分组 Key 的类型，后两个与 Curve 一致，CurveGroup 为通用实现类，提供了创建分组曲线的方法，需要传入数据集合和进行分组的规则。需要注意的是分组并不是指一个 key 对应了多条曲线，分组是针对的数据，它实际上是 Map<K, ICurve<T, V>> 的形式，在两个 CurveGroup 进行叠加计算时的分组才是多条曲线。

```java
ArrayList<Data1> data1 = data1();
// 根据 Data1 的 type 属性进行分组
ICurveGroup<String, Data1, Double> curveGroup1 = CurveGroup.create(data1, Data1::getType);
```

### 分组曲线处理

ICurveGroup 的处理逻辑和 ICurve 类似，它会在表达式中多提供一个 key 辅助你的处理，如果是叠加另一个分组，它则是对同一分组的两条曲线进行叠加处理。

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
                (key, d1, d2) -> key.equals("group1") || d1.getVal() + d2.getVal() > 0.5,
                (key, d1, d2) -> d1.getVal() * 2,
                Data1::setVal)
```

有时候你也许并不想直接对具体数据进行操作，而是想操作不同分组下的曲线，那么你也可以使用 forCurve 方法调出对应分组的曲线来自定义处理过程。

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