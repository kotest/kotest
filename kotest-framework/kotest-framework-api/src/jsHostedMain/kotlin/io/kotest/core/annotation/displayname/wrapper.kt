package io.kotest.core.annotation.displayname

import io.kotest.core.annotation.DisplayName

actual val DisplayName.wrapper: String?
   get() = this.name
