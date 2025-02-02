package io.kotest.framework.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.process.ExecOperations
import javax.inject.Inject
//import org.gradle.api.file.ArchiveOperations
//import org.gradle.api.file.FileSystemOperations
//import org.gradle.api.file.ProjectLayout
//import org.gradle.api.model.ObjectFactory
//import org.gradle.api.provider.ProviderFactory
//import org.gradle.workers.WorkerExecutor

abstract class BaseKotestTask internal constructor() : DefaultTask() {

//   @get:Inject
//   protected open val workers: WorkerExecutor get() = error("injected")
//
//   @get:Inject
//   protected open val fs: FileSystemOperations get() = error("injected")
//
//   @get:Inject
//   protected open val archives: ArchiveOperations get() = error("injected")
//
//   @get:Inject
//   protected open val providers: ProviderFactory get() = error("injected")
//
//   @get:Inject
//   protected open val objects: ObjectFactory get() = error("injected")
//
//   @get:Inject
//   protected open val layout: ProjectLayout get() = error("injected")

   @get:Inject
   protected open val executors: ExecOperations get() = error("injected")

}
