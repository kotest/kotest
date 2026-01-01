package com.sksamuel.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.JsPlatformInfo

object DefaultJsPlatformInfo : JsPlatformInfo {
   override val platformName: String = "js"
}
