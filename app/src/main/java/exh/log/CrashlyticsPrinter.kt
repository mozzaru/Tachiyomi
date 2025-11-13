package exh.log

import com.elvishew.xlog.printer.Printer
import android.util.Log
import eu.kanade.tachiyomi.BuildConfig

class DummyPrinter(private val logLevel: Int) : Printer {
    /**
     * Print log in new line.
     *
     * @param logLevel the level of log
     * @param tag the tag of log
     * @param msg the msg of log
     */
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
