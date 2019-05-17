# CodeFormatter: 智能的代码修正工具

CodeFormatter 是一个基于抽象语法树的代码修正工具，可用于自动化解决 CheckStyle 检查出来的代码格式问题。

## 特性

* 使用 JetBrain 的编译器来生成语法树，保证语法解析的准确。
* 项目架构与 [KtLint][1] 和 [CheckStyle][2] 类似，先通过语法树定位有问题的代码，再对节点进行修正。但是 [KtLint][3] 只会对 `Kotlin` 文件进行修正， 而 [CheckStyle][4] 只会定位问题但不会修正。所以本项目是对以上两者的扩展和互补。
* 本项目只做对 `Java` 代码文件的分析。
* 支持继续增添/修改规则。

## 使用

引入 **CodeFormatter** 插件后，双击指定模块的 **javaFormatting** 任务，会对模块内所有 `Java` 源码文件进行扫描：
![Gradle Task][5]

## 效果

**CodeFormatter** 主要针对 [AndroidCodeStyle][6] 的规范进行代码修正，规则会逐步完善增加。

### 目前支持自动修正的规则：

- 补全 switch 代码块的 default 语句

    ![default][7]
    
- 修改数组样式

    ![array_bracket][8]
    
- 保证代码块连续 ( `catch` , `else` , `else if` 一定要跟在 `}` 后面)

    ![continues_block][9]
    
- 保证方法表达式连续

    ![continues_statement][10]
    
- 保证没有空代码块

    ![empty_block][11]

- 对过长语句进行重构
    - 过长的类定义，对 extends 和 implements 进行换行

        ![extends][12]
    
        ![implements][13]
    
    - 过长的方法调用，对参数进行换行
    
        ![method_call_line_break][14]

    - 过长的方法定义，对参数进行换行
    
        ![method_define_line_break][15]
        
    - 过长的枚举，对枚举类型进行换行
    
        ![enum][16]
        
    - 过长的 JavaDoc，对文档进行反复二分换行
    
        ![doc_break][17]
    
    - 对于域或变量的过长的行末注释，移到变量的开头
        
        ![move_comment][18]
        
    - 对于方法定义的过长的行末注释，移到方法体中
    
        ![move_comment_to_method][19]
          
    - 移动过的或其他仍然过长的行末注释，对注释内容进行反复二分换行
    
        ![long_comment_line_break][20]
        
    - 过长的含有逻辑运算符的表达式，对逻辑运算符进行换行
    
        ![and_or][21]
        
    - 过长的域或变量，对等于号进行换行
    
        ![equal_line_break][22]
        
    - 过长的三元表达式，对三元运算符进行换行
        
        ![ternary_operator_line_break][23]
        
    - 过长的字符串常量，对字符串进行反复二分换行
    
        ![string_line_break][24]
        
    - 过长的多元表达式，优先对字符串常量前的运算符进行换行
    
        ![poly_line_break][25]
        
    - 依然过长的多元表达式，再次对所有运算符进行换行
    
        ![plus_oper][26]
        
    - 过长的数组初始化语句，对数组元素进行换行
        
        ![array][27]
        
    - 过长的属性引用和方法调用，对点符号进行换行

        ![dot][28]
        
- 补全 static 修饰符

    ![static][29]
    
- 移除无意义的空行

    ![remove_statement][30]
    
- 移除不必要的空格
    
    ![remove_space][31]

- 添加必要的空格
    
    ![add_space][32]

- 替换制表符为空格

    "    " 替换 "\t"

### 正在开发的规则：

- 移除多余或没用的 import
- 简化 **Boolean** 表达式

### 不打算支持的规则：

- 变量名不符合规范

## 安装

1. 在跟目录的 `build.gradle` 添加插件路径

    ```groovy
    buildscript {
        repositories {
            maven { url 'http://repo.yypm.com:8181/nexus/content/groups/public' }
        }
        dependencies {
            //...
            classpath 'com.unionyy.mobile:reformat:1.0.3-SNAPSHOT'
        }
    }
    ```

2. 在需要格式化的模块或者跟项目的模块的 `build.gradle` 中应用插件

    ```groovy
    apply plugin: 'format'
    ```

## 常见问题

**Q: 为什么不用 `AndroidStudio` 自带的 reformat 功能？**

A: **IDE** 的重构功能十分有限，只能处理 **无用import** 和 **缩进** 以及 **代码块加大括号** 等问题，对于 **CheckStyle** 规范来说还有很多缺少的规则。一个大项目的代码规范问题可以多达数万个，由人工来完成非常的费时。
所以建议是先使用 **IDE** 的重构功能来处理缩进问题，然后用 **CodeFormatter** 来处理剩下的规范和项目个性化的规则。

**Q: 引入插件后编译报 Kotlin 找不到系统类的错误？**

A: 先把 **kotlin** 的版本升级到 `1.2.70` 以上的版本。完成代码扫描后可以降回去。
    
**Q: 为什么 CodeFormatter 不能处理变量名的问题？**

A: 一是不知道该起怎样的名字，二是需要全项目搜索变量的引用比较耗时，三是 **IDE** 自带的改名功能已经很出色了。

**Q: CodeFormatter 还能做什么？**

所有模式匹配的修改都能做， **CheckStyle** 只是小试牛刀。比如可以替换项目中所有过时的 **API** ，把 **Support包** 的引用改成 **AndroidX** 的引用，给所有 **RxJava** 的调用加上 **ErrorConsumer** 等等。只要可以在语法树中定位并提炼的模式，都能进行修改。

## Bug上报

使用过程中发现任何问题，可以[在此留下issue][33]。 


  [1]: https://github.com/pinterest/ktlint
  [2]: https://github.com/checkstyle/checkstyle
  [3]: https://github.com/pinterest/ktlint
  [4]: https://github.com/checkstyle/checkstyle
  [5]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/gradle_task.jpg
  [6]: https://git.yy.com/opensource/athena/AndroidCodeStyle
  [7]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/default.jpg
  [8]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/arrayBracket.jpg
  [9]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/continuesBlock.jpg
  [10]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/continusStatement.jpg
  [11]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/emptyBlock.png
  [12]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/extends.jpg
  [13]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/implement.jpg
  [14]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/methodcall.jpg
  [15]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/paramList.jpg
  [16]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/enum.jpg
  [17]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/bbreakdoc.png
  [18]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/moveComment.jpg
  [19]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/moveCommentToMethod.png
  [20]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/long_comment.jpg
  [21]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/andOr.jpg
  [22]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/equal.jpg
  [23]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/triple.jpg
  [24]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/CutLongString.png
  [25]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/breakPlus.jpg
  [26]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/plusOper.jpg
  [27]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/arrayinit.png
  [28]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/dot.jpg
  [29]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/static.png
  [30]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/meaninglessEmptyStatement.jpg
  [31]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/removeWhiteSpace.jpg
  [32]: https://raw.githubusercontent.com/YvesCheung/CheckStyleFormatter/master/picture/addspace.jpg
  [33]: https://git.yy.com/midwares/business/CodeFormatter-android/issues