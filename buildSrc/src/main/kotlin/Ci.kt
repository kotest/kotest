object Ci {

   // this is the version used for building snapshots
   // .buildnumber-snapshot will be appended
   private const val snapshotBase = "5.10.0"


   private val isCI = System.getenv("CI").toBoolean()

   private val snapshotVersion =
      snapshotBase + if (isCI) "-SNAPSHOT" else "-LOCAL"

   private val releaseVersion = System.getenv("RELEASE_VERSION")?.ifBlank { null }

   val isRelease = releaseVersion != null

   /** The published version of Kotest dependencies. */
   val publishVersion = releaseVersion ?: snapshotVersion

   /**
    * Property to flag the build as JVM only, can be used to run checks on local machine much faster.
    */
   const val JVM_ONLY = "jvmOnly"
}
