/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.kotest.extensions.testcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an internal testing utility.<br>
 * You are welcome to use this class for your own purposes,<br>
 * but if there is some feature/enhancement you need for your own usage,<br>
 * please make and modify your own copy instead of sending us an enhancement request.<br>
 *
 * @author Clinton Begin
 * @deprecated
 */
@Deprecated
public class ScriptRunner {

   private static final String LINE_SEPARATOR = System.lineSeparator();

   private static final String DEFAULT_DELIMITER = ";";

   private static final Pattern DELIMITER_PATTERN = Pattern.compile("^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE);

   private final Connection connection;

   private boolean stopOnError;
   private boolean throwWarning;
   private boolean autoCommit;
   private boolean sendFullScript;
   private boolean removeCRs;
   private boolean escapeProcessing = true;

   private static final Logger LOGGER = LoggerFactory.getLogger(ScriptRunner.class);

   private String delimiter = DEFAULT_DELIMITER;
   private boolean fullLineDelimiter;

   public ScriptRunner(Connection connection) {
      this.connection = connection;
   }

   public void setStopOnError(boolean stopOnError) {
      this.stopOnError = stopOnError;
   }

   public void setThrowWarning(boolean throwWarning) {
      this.throwWarning = throwWarning;
   }

   public void setAutoCommit(boolean autoCommit) {
      this.autoCommit = autoCommit;
   }

   public void setSendFullScript(boolean sendFullScript) {
      this.sendFullScript = sendFullScript;
   }

   public void setRemoveCRs(boolean removeCRs) {
      this.removeCRs = removeCRs;
   }

   /**
    * Sets the escape processing.
    *
    * @param escapeProcessing the new escape processing
    * @since 3.1.1
    */
   public void setEscapeProcessing(boolean escapeProcessing) {
      this.escapeProcessing = escapeProcessing;
   }

   public void setDelimiter(String delimiter) {
      this.delimiter = delimiter;
   }

   public void setFullLineDelimiter(boolean fullLineDelimiter) {
      this.fullLineDelimiter = fullLineDelimiter;
   }

   public void runScript(Reader reader) {
      setAutoCommit();

      try {
         if (sendFullScript) {
            executeFullScript(reader);
         } else {
            executeLineByLine(reader);
         }
      } finally {
         rollbackConnection();
      }
   }

   private void executeFullScript(Reader reader) {
      StringBuilder script = new StringBuilder();
      try {
         BufferedReader lineReader = new BufferedReader(reader);
         String line;
         while ((line = lineReader.readLine()) != null) {
            script.append(line);
            script.append(LINE_SEPARATOR);
         }
         String command = script.toString();
         LOGGER.debug(command);
         executeStatement(command);
         commitConnection();
      } catch (Exception e) {
         String message = "Error executing: " + script + ".  Cause: " + e;
         LOGGER.error(message);
         throw new RuntimeSqlException(message, e);
      }
   }

   private void executeLineByLine(Reader reader) {
      StringBuilder command = new StringBuilder();
      try {
         BufferedReader lineReader = new BufferedReader(reader);
         String line;
         while ((line = lineReader.readLine()) != null) {
            handleLine(command, line);
         }
         commitConnection();
         checkForMissingLineTerminator(command);
      } catch (Exception e) {
         String message = "Error executing: " + command + ".  Cause: " + e;
         LOGGER.error(message);
         throw new RuntimeSqlException(message, e);
      }
   }

   private void setAutoCommit() {
      try {
         if (autoCommit != connection.getAutoCommit()) {
            connection.setAutoCommit(autoCommit);
         }
      } catch (Exception t) {
         throw new RuntimeSqlException("Could not set AutoCommit to " + autoCommit + ". Cause: " + t, t);
      }
   }

   private void commitConnection() {
      try {
         if (!connection.getAutoCommit()) {
            connection.commit();
         }
      } catch (Exception t) {
         throw new RuntimeSqlException("Could not commit transaction. Cause: " + t, t);
      }
   }

   private void rollbackConnection() {
      try {
         if (!connection.getAutoCommit()) {
            connection.rollback();
         }
      } catch (Exception t) {
         // ignore
      }
   }

   private void checkForMissingLineTerminator(StringBuilder command) {
      if (command != null && command.toString().trim().length() > 0) {
         throw new RuntimeSqlException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
      }
   }

   private void handleLine(StringBuilder command, String line) throws SQLException {
      String trimmedLine = line.trim();
      if (lineIsComment(trimmedLine)) {
         Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
         if (matcher.find()) {
            delimiter = matcher.group(5);
         }
         LOGGER.debug(trimmedLine);
      } else if (commandReadyToExecute(trimmedLine)) {
         command.append(line, 0, line.lastIndexOf(delimiter));
         command.append(LINE_SEPARATOR);
         LOGGER.debug(command.toString());
         executeStatement(command.toString());
         command.setLength(0);
      } else if (trimmedLine.length() > 0) {
         command.append(line);
         command.append(LINE_SEPARATOR);
      }
   }

   private boolean lineIsComment(String trimmedLine) {
      return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
   }

   private boolean commandReadyToExecute(String trimmedLine) {
      // issue #561 remove anything after the delimiter
      return !fullLineDelimiter && trimmedLine.contains(delimiter) || fullLineDelimiter && trimmedLine.equals(delimiter);
   }

   private void executeStatement(String command) throws SQLException {
      try (Statement statement = connection.createStatement()) {
         statement.setEscapeProcessing(escapeProcessing);
         String sql = command;
         if (removeCRs) {
            sql = sql.replace("\r\n", "\n");
         }
         try {
            boolean hasResults = statement.execute(sql);
            while (!(!hasResults && statement.getUpdateCount() == -1)) {
               checkWarnings(statement);
               printResults(statement, hasResults);
               hasResults = statement.getMoreResults();
            }
         } catch (SQLWarning e) {
            throw e;
         } catch (SQLException e) {
            if (stopOnError) {
               throw e;
            } else {
               String message = "Error executing: " + command + ".  Cause: " + e;
               LOGGER.error(message);
            }
         }
      }
   }

   private void checkWarnings(Statement statement) throws SQLException {
      if (!throwWarning) {
         return;
      }
      // In Oracle, CREATE PROCEDURE, FUNCTION, etc. returns warning
      // instead of throwing exception if there is compilation error.
      SQLWarning warning = statement.getWarnings();
      if (warning != null) {
         throw warning;
      }
   }

   private void printResults(Statement statement, boolean hasResults) {
      if (!hasResults) {
         return;
      }
      try (ResultSet rs = statement.getResultSet()) {
         ResultSetMetaData md = rs.getMetaData();
         int cols = md.getColumnCount();
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < cols; i++) {
            String name = md.getColumnLabel(i + 1);
            sb.append(name + "\t");
         }
         sb.append(System.lineSeparator());
         while (rs.next()) {
            for (int i = 0; i < cols; i++) {
               String value = rs.getString(i + 1);
               sb.append(value + "\t");
            }
            sb.append(System.lineSeparator());
            LOGGER.debug(sb.toString());
         }
      } catch (SQLException e) {
         LOGGER.error("Error printing results: " + e.getMessage());
      }
   }
}
