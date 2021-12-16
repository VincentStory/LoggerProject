package com.vincent.loggerproject

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.util.Log.*
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author vincentStory
 * @date 2021/10/19.
 * GitHub：https://github.com/VincentStory
 * description：打印日志工具
 */

class LogDispatcher {

    companion object {

        const val VERBOSE = 2

        const val DEBUG = 3

        const val INFO = 4

        const val WARN = 5

        const val ERROR = 6

        const val SERVER = 7

        const val MONITOR = 8


        //层级深度
        private const val CUR_DEEP = 4


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



        private val mLogUtilInterceptor: LogUtilInterceptor =
            DefaultLogUtilInterceptor()


        fun i(tag: String?, text: String?) {
            i(tag, text, CUR_DEEP)
        }

        fun i(tag: String?, text: String?, level: Int) {
            if (mLogUtilInterceptor != null) {
                if (mLogUtilInterceptor.intercept(INFO)) {
                    return
                }
                val values: String? = detail(text, level)
                mLogUtilInterceptor.onLog(
                    INFO,
                    tag,
                    values
                )
            }
        }


        fun d(tag: String?, text: String?) {
            d(tag, text, CUR_DEEP)
        }

        private fun d(tag: String?, text: String?, level: Int) {
            if (mLogUtilInterceptor != null) {
                if (mLogUtilInterceptor.intercept(DEBUG)) {
                    return
                }
                val values: String? = detail(text, level)
                mLogUtilInterceptor.onLog(
                    DEBUG,
                    tag,
                    values
                )
            }
        }


        fun d(tag: String?, text: String, e: Throwable?) {
            d(tag, text, e, CUR_DEEP)
        }

        fun d(tag: String?, text: String, e: Throwable?, level: Int) {
            var text = text
            if (mLogUtilInterceptor != null) {
                if (mLogUtilInterceptor.intercept(DEBUG)) {
                    return
                }
                text = """
            $text
            ${getStackTraceString(e)}
            """.trimIndent()
                val values = detail(text, level)
                mLogUtilInterceptor.onLog(DEBUG, tag, values)
            }
        }

        fun v(tag: String?, text: String?) {
            v(tag, text, CUR_DEEP)
        }

        fun v(tag: String?, text: String?, level: Int) {
            if (mLogUtilInterceptor != null) {
                if (mLogUtilInterceptor.intercept(VERBOSE)) {
                    return
                }
                val values = detail(text, level)
                mLogUtilInterceptor.onLog(VERBOSE, tag, values)
            }
        }

        fun w(tag: String?, text: String?) {
            w(tag, text, CUR_DEEP)
        }

        fun w(tag: String?, text: String?, level: Int) {
            if (mLogUtilInterceptor != null) {
                if (mLogUtilInterceptor.intercept(WARN)) {
                    return
                }
                val values = detail(text, level)
                mLogUtilInterceptor.onLog(WARN, tag, values)
            }
        }

        fun e(tag: String?, text: String, e: Throwable?) {
            e(tag, text, e, CUR_DEEP)
        }

        fun e(tag: String?, text: String, e: Throwable?, level: Int) {
            var text = text
            if (mLogUtilInterceptor != null) {
                if (mLogUtilInterceptor.intercept(ERROR)) {
                    return
                }
                text = """
            $text
            ${getStackTraceString(e)}
            """.trimIndent()
                val values = detail(text, level)
                mLogUtilInterceptor.onLog(ERROR, tag, values)
            }
        }

        fun s(tag: String?, text: String?) {
            s(tag, text, CUR_DEEP)
        }

        fun s(tag: String?, text: String?, level: Int) {
            if (mLogUtilInterceptor != null) {
                if (mLogUtilInterceptor.intercept(SERVER)) {
                    return
                }
                val values = detail(text, level)
                mLogUtilInterceptor.onLog(SERVER, tag, values)
            }
        }

    }


    private class DefaultLogUtilInterceptor :
        LogUtilInterceptor {
        @SuppressLint("LogTagMismatch")
        override fun onLog(level: Int, tag: String?, text: String?) {
            var text = text
            if (BuildConfig.DEBUG) {
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
            } else {
                printSegment(level, tag, text)
            }
//            if (!TextUtils.isEmpty(text) && level != VERBOSE) {
//                DebugLogger.getInstance().f(tag, text)
//                //                Crashlytics.log("message");
//            }
        }


        override fun intercept(@Level level: Int): Boolean {
            return false
//            //普遍认为，i和v都是调试，在线上不属于关键信息，v，w，d基本上都是要为跟踪用户关键行为服务，所以在生产环境下也要落地入文件，支持不在控制台打印
//            //s级别的日志，认为是实时日志，要传回给服务器，和tcp或者网络情况做时序校验用的，不过看起来我们产品目前用不到
//            return level == VERBOSE || level == INFO;
        }

        override fun detail(): Boolean {
            return BuildConfig.DEBUG
        }

        companion object {
            private fun printSegment(level: Int, tag: String?, segtext: String?) {
                when (level) {
                    VERBOSE -> Log.v(tag, segtext!!)
                    DEBUG -> if (Log.isLoggable(
                            tag,
                            Log.DEBUG
                        )
                    ) {
                        Log.d(tag, segtext!!)
                    } else {
                        LogI(tag, segtext)
                    }
                    WARN -> Log.w(tag, segtext!!)
                    ERROR -> Log.e(tag, segtext!!)
                    SERVER, INFO -> LogI(
                        tag,
                        segtext
                    )
                    else -> LogI(tag, segtext)
                }
            }

        }
    }


    @IntDef(
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        SERVER
    )
    @Retention(
        RetentionPolicy.SOURCE
    )
    internal annotation class Level

    internal interface LogUtilInterceptor {
        /**
         * @param level enum(VERBOSE,DEBUG,INFO,WARN,ERROR,ASSERT)
         * @param tag
         * @param text
         * @return
         */
        fun onLog(@Level level: Int, tag: String?, text: String?)


        /**
         * @return intercept
         */
        fun intercept(@Level level: Int): Boolean

        /**
         * @return
         */
        fun detail(): Boolean
    }

}
fun LogI(tag: String?, msg: String?) {  //信息太长,分段打印
    //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
    //  把4*1024的MAX字节打印长度改为2001字符数
//    var msg = msg
//    var max_str_length = 3000
////    msg?.length?.let {
////        max_str_length = 2001 - it
////    }
//    //大于4000时
//    var length = 0
//    msg?.length?.let {
//        length = it
//    }
//
//    while (length > max_str_length) {
//        msg?.substring(0, max_str_length)?.let { it1 -> i(tag, it1) }
//        msg = msg?.substring(max_str_length)
//        msg?.length?.let {
//            length = it
//        }
//    }
    //剩余部分
    msg?.let { i(tag, it) }
}