# LoggerProject
日志打印工具类,方便获取类名，快速定位方法执行位置

### 使用方式：
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
```
 if (tag == null || tag.isEmpty() || text == null || text.isEmpty()) return
                val segmentSize = 3 * 1024
                var length = text.length.toLong()
                if (length <= segmentSize) { // 长度小于等于限制直接打印
                    printSegment(level, tag, text)
                } else {
                    while (length > segmentSize) { // 循环分段打印日志
                        val logContent = text?.substring(0, segmentSize)
                        text = logContent?.let { it1 -> text?.replace(it1, "") }

                        text?.length?.toLong()?.let {
                            length = it
                        }

                        printSegment(level, tag, logContent)
                    }
                    printSegment(level, tag, text)

                }
```

### 输出效果
```
2021-10-19 15:41:04.140 21321-21321/com.vincent.loggerproject I/tag: |(MainActivity.kt:12)|onCreate]:打印日志

```
