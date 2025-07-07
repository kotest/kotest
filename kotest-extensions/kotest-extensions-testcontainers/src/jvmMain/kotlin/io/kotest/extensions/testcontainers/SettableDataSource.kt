package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariDataSource
import java.io.PrintWriter
import java.sql.Connection
import java.util.logging.Logger
import javax.sql.DataSource

@Deprecated("Use TestContainerProjectExtension or TestContainerSpeccExtension instead")
class SettableDataSource(private var ds: HikariDataSource?) : DataSource {

   private fun getDs(): DataSource = ds ?: error("DataSource is not ready")

   fun setDataSource(ds: HikariDataSource?) {
      this.ds?.close()
      this.ds = ds
   }

   override fun getLogWriter(): PrintWriter {
      return getDs().logWriter
   }

   override fun setLogWriter(out: PrintWriter?) {
      getDs().logWriter = out
   }

   override fun setLoginTimeout(seconds: Int) {
      getDs().loginTimeout = seconds
   }

   override fun getLoginTimeout(): Int {
      return getDs().loginTimeout
   }

   override fun getParentLogger(): Logger {
      return getDs().parentLogger
   }

   override fun <T : Any?> unwrap(iface: Class<T>?): T {
      return getDs().unwrap(iface)
   }

   override fun isWrapperFor(iface: Class<*>?): Boolean {
      return getDs().isWrapperFor(iface)
   }

   override fun getConnection(): Connection {
      return getDs().connection
   }

   override fun getConnection(username: String?, password: String?): Connection {
      return getDs().getConnection(username, password)
   }

}
