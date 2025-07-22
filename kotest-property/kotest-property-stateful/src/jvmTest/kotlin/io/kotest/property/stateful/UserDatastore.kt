@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS_WARNING", "SqlResolve")

package io.kotest.property.stateful

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

class UserDatastore(ds: DataSource) {

   private val template = NamedParameterJdbcTemplate(ds)

   fun insert(user: User) {
      template.update(
         "INSERT INTO users (username, password, department) VALUES (:username, :password, :department) ON CONFLICT DO NOTHING",
         mapOf(
            "username" to user.username,
            "password" to user.password,
            "department" to user.department,
         )
      )
   }

   fun findAll(): List<User> {
      return template.query("SELECT username, password FROM users") { rs, _ ->
         User(
            username = rs.getString("username"),
            password = rs.getString("password"),
            department = rs.getString("department"),
         )
      }
   }

   fun findByUsername(username: String): User? {
      return template.queryForObject(
         "SELECT username, password FROM users WHERE username = :username",
         mapOf("username" to username),
      ) { rs, _ ->
         User(
            username = rs.getString("username"),
            password = rs.getString("password"),
            department = rs.getString("department"),
         )
      }
   }

   fun updatePassword(username: String, newPassword: String, newDepartment: String): Int {
      return template.update(
         "UPDATE users SET password = :password, department = :department WHERE username = :username",
         mapOf(
            "password" to newPassword,
            "department" to newDepartment,
            "username" to username,
         )
      )
   }

   fun delete(username: String): Int {
      return template.update(
         "DELETE FROM users WHERE username = :username",
         mapOf("username" to username)
      )
   }
}
