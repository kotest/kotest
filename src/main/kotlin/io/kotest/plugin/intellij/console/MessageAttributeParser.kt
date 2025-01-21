package io.kotest.plugin.intellij.console

import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object MessageAttributeParser {

   fun parse(msg: ServiceMessage): MessageAttributes {
      val id = msg.attributes["id"] ?: error("id is a required service message attribute")
      val parentId = msg.attributes["parent_id"] // is null for specs
      val name = msg.attributes["name"] ?: error("name is a required service message attribute")
      val location = msg.attributes["locationHint"]
      val message = msg.attributes["message"]
      val duration = msg.attributes["duration"]?.toLongOrNull()?.milliseconds
      return MessageAttributes(id, parentId, name, location, duration, message)
   }
}

data class MessageAttributes(
   val id: String,
   val parentId: String?,
   val name: String,
   val location: String?,
   val duration: Duration?,
   val message: String?,
)
