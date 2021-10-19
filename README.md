# LoggerProject
日志打印工具类

### 试用方式：
```
 LogDispatcher.d("tag", "打印日志");
```

### 核心代码
```
  private fun detail(text: String?, level: Int): String? {
            return if (mLogUtilInterceptor != null && mLogUtilInterceptor.detail()) {
                getFileLineMethod(level) +
                        ":" +
                        text
            } else text
        }

        private fun getFileLineMethod(level: Int): String? {
            //通过传入层级获取对应的类名，行数，方法名
            val element = Exception().stackTrace[level]
            val buffer = StringBuffer()
                .append("|(")
                .append(element.fileName)
                .append(":")
                .append(element.lineNumber)
                .append(")|")
                .append(element.methodName)
                .append("]")
            return buffer.toString()
        }

```

### 输出效果
```
2021-10-19 15:41:04.140 21321-21321/com.vincent.loggerproject I/tag: |(MainActivity.kt:12)|onCreate]:打印日志

```
