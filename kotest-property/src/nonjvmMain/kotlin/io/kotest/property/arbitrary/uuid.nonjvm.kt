package io.kotest.property.arbitrary

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual typealias PlatformUuid = Uuid

@OptIn(ExperimentalUuidApi::class)
internal actual fun String.toPlatformUuid(): PlatformUuid = Uuid.parse(this)
