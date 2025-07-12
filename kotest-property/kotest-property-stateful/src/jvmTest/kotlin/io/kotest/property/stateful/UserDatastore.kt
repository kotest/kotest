package io.kotest.property.stateful

import javax.sql.DataSource

class UserDatastore(ds: DataSource) {

   private val template = org.springframework.jdbc.core.JdbcTemplate(ds)

   fun insert(user: User) {
      template.update("INSERT INTO users (username, password) VALUES (?, ?) ON CONFLICT DO NOTHING", user.username, user.password)
   }

   fun findAll() : List<User> {
      return template.query("SELECT username, password FROM users") { rs ->
         User(
            rs.getString("username"),
            rs.getString("password"),
         )
      }
   }

   fun findByUsername(username: String): User? {
      return template.queryForObject("SELECT username, password FROM users WHERE username = ?", username) { rs ->
         User(
            rs.getString("username"),
            rs.getString("password"),
         )
      }
   }

   fun updatePassword(username: String, newPassword: String) {
      template.update("UPDATE users SET password = ? WHERE username = ?", newPassword, username)
   }

   fun delete(username: String) {
      template.update("DELETE FROM users WHERE username = ?", username)
   }
}
