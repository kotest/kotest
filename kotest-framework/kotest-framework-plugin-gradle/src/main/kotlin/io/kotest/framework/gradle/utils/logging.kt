package io.kotest.framework.gradle.utils

import org.gradle.api.logging.Logger

/** Only evaluate and log [msg] when [Logger.isWarnEnabled] is `true`. */
internal fun Logger.warn(msg: () -> String) {
   if (isWarnEnabled) warn(msg())
}

///** Only evaluate and log [msg] when [Logger.isInfoEnabled] is `true`. */
//internal  fun Logger.info(msg: () -> String) {
//   if (isInfoEnabled) warn(msg())
//}
