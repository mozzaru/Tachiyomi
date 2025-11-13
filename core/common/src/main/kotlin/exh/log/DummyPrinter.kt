package exh.log

import com.elvishew.xlog.printer.Printer
import android.util.Log
import eu.kanade.tachiyomi.BuildConfig

class DummyPrinter(private val logLevel: Int) : Printer {
    override fun println(logLevel: Int, tag: String?, msg: String?) {
        if (logLevel >= this.logLevel) {
            try {
                Log.println(logLevel, tag ?: "DummyPrinter", msg ?: "")
            } catch (t: Throwable) {
                if (BuildConfig.DEBUG) throw t
            }
        }
    }
}
