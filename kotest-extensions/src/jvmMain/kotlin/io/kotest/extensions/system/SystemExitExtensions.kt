package io.kotest.extensions.system

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.matchers.shouldBe
import java.io.FileDescriptor
import java.net.InetAddress
import java.security.Permission
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Will replace the [SecurityManager] used by the Java runtime
 * with a [NoExitSecurityManager] that will throw an exception
 * if System.exit() is invoked. This exception can then be
 * tested for with shouldThrow().
 *
 * To use this, override `listeners()` in your [AbstractProjectConfig].
 *
 * Note: This listener will change the security manager
 * for all tests. If you want to change the security manager
 * for just a single [Spec] then consider the
 * alternative [SpecSystemExitListener]
 */
object SystemExitListener : TestListener {
   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
        val previous = System.getSecurityManager()
        System.setSecurityManager(NoExitSecurityManager(previous))
    }
}

/**
 * Will replace the [SecurityManager] used by the Java runtime
 * with a [NoExitSecurityManager] that will throw an exception
 * if System.exit() is invoked. This exception can then be
 * tested for with shouldThrow().
 *
 * After the spec has completed, the original security manager
 * will be set.
 *
 * To use this, override `extensions`() in your [Spec] class.
 *
 * Note: This extension is only suitable for use if parallelism is
 * set to 1 (the default) otherwise a race condition could occur.
 *
 * If you want to change the security manager for the entire
 * project, then use the alternative [SystemExitListener]
 */
object SpecSystemExitListener : TestListener {

   private val previousSecurityManagers = ConcurrentHashMap<Descriptor, SecurityManager>()

   override suspend fun beforeSpec(spec: Spec) {
      val previous = System.getSecurityManager()
      if (previous != null)
         previousSecurityManagers[spec::class.toDescriptor()] = previous
      System.setSecurityManager(NoExitSecurityManager(previous))
   }

   override suspend fun afterSpec(spec: Spec) {
      if (previousSecurityManagers.contains(spec::class.toDescriptor()))
         System.setSecurityManager(previousSecurityManagers[spec::class.toDescriptor()])
      else
         System.setSecurityManager(null)
   }

   fun lastExitCode(): Int? = when (val manager = System.getSecurityManager()) {
      is NoExitSecurityManager -> manager.lastExitCode
      else -> null
   }

   fun shouldHaveExitCode(code: Int) {
      lastExitCode() shouldBe code
   }
}

class SystemExitException(val exitCode: Int) : RuntimeException()

/**
 * An implementation of [SecurityManager] that throws a [SystemExitException]
 * exception whenever System.exit(int) is invoked.
 *
 * Implemenation inspired from
 * https://github.com/stefanbirkner/system-rules/blob/72250d0451f9f1a5c5852502b5e9b0c874aeab42/src/main/java/org/junit/contrib/java/lang/system/internal/NoExitSecurityManager.java
 * Apache Licensed.
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class NoExitSecurityManager(private val originalSecurityManager: SecurityManager?) : SecurityManager() {

   var lastExitCode: Int? = null

   override fun checkExit(status: Int) {
      lastExitCode = status
      throw SystemExitException(status)
   }

    override fun getSecurityContext(): Any {
        return if (originalSecurityManager == null)
            super.getSecurityContext()
        else
            originalSecurityManager.securityContext
    }

    override fun checkPermission(perm: Permission) {
        originalSecurityManager?.checkPermission(perm)
    }

    override fun checkPermission(perm: Permission, context: Any) {
        originalSecurityManager?.checkPermission(perm, context)
    }

    override fun checkCreateClassLoader() {
        originalSecurityManager?.checkCreateClassLoader()
    }

    override fun checkAccess(t: Thread) {
        originalSecurityManager?.checkAccess(t)
    }

    override fun checkAccess(g: ThreadGroup) {
        originalSecurityManager?.checkAccess(g)
    }

    override fun checkExec(cmd: String) {
        originalSecurityManager?.checkExec(cmd)
    }

    override fun checkLink(lib: String) {
        originalSecurityManager?.checkLink(lib)
    }

    override fun checkRead(fd: FileDescriptor) {
        originalSecurityManager?.checkRead(fd)
    }

    override fun checkRead(file: String) {
        originalSecurityManager?.checkRead(file)
    }

    override fun checkRead(file: String, context: Any) {
        originalSecurityManager?.checkRead(file, context)
    }

    override fun checkWrite(fd: FileDescriptor) {
        originalSecurityManager?.checkWrite(fd)
    }

    override fun checkWrite(file: String) {
        originalSecurityManager?.checkWrite(file)
    }

    override fun checkDelete(file: String) {
        originalSecurityManager?.checkDelete(file)
    }

    override fun checkConnect(host: String, port: Int) {
        originalSecurityManager?.checkConnect(host, port)
    }

    override fun checkConnect(host: String, port: Int, context: Any) {
        originalSecurityManager?.checkConnect(host, port, context)
    }

    override fun checkListen(port: Int) {
        originalSecurityManager?.checkListen(port)
    }

    override fun checkAccept(host: String, port: Int) {
        originalSecurityManager?.checkAccept(host, port)
    }

    override fun checkMulticast(maddr: InetAddress) {
        originalSecurityManager?.checkMulticast(maddr)
    }

    override fun checkMulticast(maddr: InetAddress, ttl: Byte) {
        originalSecurityManager?.checkMulticast(maddr, ttl)
    }

    override fun checkPropertiesAccess() {
        originalSecurityManager?.checkPropertiesAccess()
    }

    override fun checkPropertyAccess(key: String) {
        originalSecurityManager?.checkPropertyAccess(key)
    }

    override fun checkPrintJobAccess() {
        originalSecurityManager?.checkPrintJobAccess()
    }

    override fun checkPackageAccess(pkg: String) {
        originalSecurityManager?.checkPackageAccess(pkg)
    }

    override fun checkPackageDefinition(pkg: String) {
        originalSecurityManager?.checkPackageDefinition(pkg)
    }

    override fun checkSetFactory() {
        originalSecurityManager?.checkSetFactory()
    }

    override fun checkSecurityAccess(target: String) {
        originalSecurityManager?.checkSecurityAccess(target)
    }

    override fun getThreadGroup(): ThreadGroup {
        return if (originalSecurityManager == null)
            super.getThreadGroup()
        else
            originalSecurityManager.threadGroup
    }
}
