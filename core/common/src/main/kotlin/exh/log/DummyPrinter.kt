package exh.log

import com.elvishew.xlog.printer.Printer
import android.util.Log

class DummyPrinter(
    private val logLevel: Int,
    private val isDebug: Boolean = false
) : Printer {
    override fun println(logLevel: Int, tag: String?, msg: String?) {
        if (logLevel >= this.logLevel) {
            try {
                Log.println(logLevel, tag ?: "DummyPrinter", msg ?: "")
            } catch (t: Throwable) {
                if (isDebug) throw t
            }
        }
    }
}
