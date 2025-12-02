package io.kotest.extensions.testcontainers

import java.io.BufferedReader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

@Deprecated("use Flyway or another db migration tool. Will be removed in 6.2")
sealed interface Resource {
   data class Classpath(val resource: String) : Resource
   data class File(val path: Path) : Resource
}

@Deprecated("use Flyway or another db migration tool. Will be removed in 6.2")
fun Resource.endsWith(name: String): Boolean = when (this) {
   is Resource.Classpath -> this.resource.endsWith(name)
   is Resource.File -> this.path.name.endsWith(name)
}

@Deprecated("use Flyway or another db migration tool. Will be removed in 6.2")
fun Resource.loadToReader(): BufferedReader = when (this) {
   is Resource.Classpath -> javaClass.getResourceAsStream(this.resource)?.bufferedReader() ?: error("$this was not found on classpath")
   is Resource.File -> Files.newBufferedReader(this.path)
}

@Deprecated("use Flyway or another db migration tool. Will be removed in 6.2")
class ResourceLoader {

   private fun Path.getDirContentsOrItself(): List<Path> {
      return if (this.isDirectory()) {
         Files.newDirectoryStream(this)
            .use { stream -> stream.toList() }
            .filter { it.isRegularFile() }
      } else {
         listOf(this)
      }
   }

   private fun getFileResourcesFromPath(path: Path): List<Resource.File> {
      if (!path.exists()) error("Resource $path does not exist on the classpath or on the local filesystem")

      return when (path.isRegularFile()) {
         true -> listOf(Resource.File(path))
         else -> Files.walk(path)
            .filter { it.isRegularFile() }
            .sorted()
            .map { Resource.File(it) }
            .collect(Collectors.toList())
      }
   }

   private fun getClasspathResourcesFromJar(resource: String) : List<Resource> {
      val uri = javaClass.getResource(resource)!!.toURI()
      FileSystems.newFileSystem(uri, mutableMapOf<String, Any>()).use { fs ->
         val pathList = fs.getPath(resource).getDirContentsOrItself()
         return pathList.map { Resource.Classpath(it.toString()) }.sortedBy { it.toString() }
      }
   }

   fun resolveResource(resource: String): List<Resource> {
      val url = javaClass.getResource(resource)

      return if (url == null) {
         //Not on classpath, check file system
         val path = Paths.get(resource)
         getFileResourcesFromPath(path)

      } else {
         //On Classpath
         when(url.protocol){
            "jar" -> getClasspathResourcesFromJar(resource)
            "file" -> getFileResourcesFromPath(Paths.get(url.toURI()))
            else -> error("Unhandled protocol: ${url.protocol}")
         }
      }
   }
}

