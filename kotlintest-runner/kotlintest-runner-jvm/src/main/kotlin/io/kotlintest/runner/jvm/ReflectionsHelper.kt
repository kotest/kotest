package io.kotlintest.runner.jvm

import org.reflections.vfs.Vfs
import java.net.URL
import java.nio.file.Paths

/**
 *
 * Taken from
 * https://gist.githubusercontent.com/nonrational/287ed109bb0852f982e8/raw/11a6fa4e35d910f025ca34d963a1fd6ce99a62c2/ReflectionsHelper.java
 *
 * Inspired heavily by
 *
 * <link>https://git-wip-us.apache.org/repos/asf?p=isis.git;a=blob;f=core/applib/src/main/java/org/apache/isis/applib/services/classdiscovery/
 * ClassDiscoveryServiceUsingReflections.java;h=283f053ddb15bfe32f111d88891602820854415e;hb=283f053ddb15bfe32f111d88891602820854415e</link>
 */
object ReflectionsHelper {

  /**
   * OSX contains file:// resources on the classpath including .mar and .jnilib files.
   *
   * Reflections use of Vfs doesn't recognize these URLs and logs warns when it sees them. By registering those file endings, we supress the warns.
   */
  fun registerUrlTypes() {
    val urlTypes = listOf(EmptyIfFileEndingsUrlType(listOf(".mar", ".jnilib")), IgnoreEmptyDirectoryUrlType) + Vfs.DefaultUrlTypes.values().toList()
    Vfs.setDefaultURLTypes(urlTypes)
  }

  fun emptyVfsDir(url: URL): Vfs.Dir {
    return object : Vfs.Dir {
      override fun getPath(): String = url.toExternalForm()
      override fun getFiles(): Iterable<Vfs.File> = emptyList()
      override fun close() {}
    }
  }

  object IgnoreEmptyDirectoryUrlType : Vfs.UrlType {

    override fun createDir(url: URL): Vfs.Dir = emptyVfsDir(url)

    override fun matches(url: URL): Boolean {
      if (url.protocol != "file") {
        return false
      }
      val file = Paths.get(url.toURI()).toFile()
      return !file.exists() || file.list() == null || file.list().isEmpty()
    }
  }

  class EmptyIfFileEndingsUrlType(val fileEndings: List<String>) : Vfs.UrlType {

    override fun matches(url: URL): Boolean {
      val protocol = url.protocol
      val externalForm = url.toExternalForm()
      if (protocol != "file") {
        return false
      }
      for (fileEnding in fileEndings) {
        if (externalForm.endsWith(fileEnding))
          return true
      }
      return false
    }

    override fun createDir(url: URL): Vfs.Dir = emptyVfsDir(url)
  }
}
