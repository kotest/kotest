package io.kotest.engine.script

import io.kotest.fp.Try
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

/**
 * Creates an instance of a kotlin script.
 */
internal fun createAndInitializeScript(
   clazz: KClass<out ScriptTemplateWithArgs>,
   loader: ClassLoader
): Try<ScriptTemplateWithArgs> = Try {
   javaReflectNewInstance(clazz.qualifiedName!!, loader)
}

internal fun javaReflectNewInstance(name: String, loader: ClassLoader): ScriptTemplateWithArgs {
   try {
      // we must load the class from the runtime classloader, not the discovery classloader.
      val clazz = loader.loadClass(name)
      val constructor = clazz.constructors.find { it.parameters.size == 1 } ?: throw ScriptInstantiationException(
         "Script class has no applicable constructor [constructors=${clazz.constructors}", null
      )
      constructor.isAccessible = true
      return constructor.newInstance(emptyArray<String>()) as ScriptTemplateWithArgs
   } catch (t: Throwable) {
      throw ScriptInstantiationException("Could not create instance of script class $name", t)
   }
}

class ScriptInstantiationException(msg: String, t: Throwable?) : RuntimeException(msg, t)

