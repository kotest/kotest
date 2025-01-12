package io.kotest.engine.tags

internal fun TagExpression.parse(): Expression? {
   val expr = this.expression
   return if (expr.isBlank()) null else Parser.from(expr).expression()
}

internal class Parser(private val tokens: List<Token>) {

   companion object {
      fun from(input: String) = Parser(Lexer(input).lex())
   }

   private var cursor = 0

   /**
    * Returns true if we have reached the end of the token stream
    */
   private fun isEof(): Boolean = cursor == tokens.size

   fun skip() {
      consume()
   }

   fun skip(type: TokenType) {
      consume(type)
   }

   /**
    * Consumes and returns the next [Token].
    */
   fun consume(): Token {
      val token = tokens[cursor]
      cursor++
      return token
   }

   /**
    * Consumes the next token, throwing an error if the token
    * is not of the given type.
    */
   fun consume(type: TokenType): Token {
      val next = consume()
      if (next.type != type) {
         error("Expected $type but was $next")
      }
      return next
   }

   /**
    * Returns the next [Token] without consuming it, or null if the next token is eof
    */
   fun peek(): Token? = if (isEof()) null else tokens[cursor]

   fun skipIf(type: TokenType): Boolean {
      return if (peek()?.type == type) {
         skip()
         true
      } else {
         false
      }
   }
}
