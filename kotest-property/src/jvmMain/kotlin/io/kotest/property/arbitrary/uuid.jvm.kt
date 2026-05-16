package io.kotest.property.arbitrary

import java.util.UUID

actual typealias PlatformUuid = UUID

internal actual fun String.toPlatformUuid(): PlatformUuid = UUID.fromString(this)
