import org.gradle.internal.os.OperatingSystem

object Ci {

   private const val snapshotBase = "4.1.0"
   val os: OperatingSystem = OperatingSystem.current()

   private val githubBuildNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "0"
   private val snapshotVersion = "$snapshotBase.${githubBuildNumber}-SNAPSHOT"

   private val releaseTag = detectReleaseTag()

   private fun detectReleaseTag(): String? {
      return try {
         val process = Runtime.getRuntime().exec("git tag --points-at master")
         val reader = process.inputStream.bufferedReader()
         val tag: String? = reader.readLine()
         if (tag != null && tag.isNotBlank() && tag.startsWith("v")) tag.removePrefix("v") else null
      } catch (e: Exception) {
         println(e.message)
         e.printStackTrace()
         null
      }
   }

   val isRelease = releaseTag != null

   val publishVersion: String = when (releaseTag) {
      null -> snapshotVersion
      else -> releaseTag
   }
}
