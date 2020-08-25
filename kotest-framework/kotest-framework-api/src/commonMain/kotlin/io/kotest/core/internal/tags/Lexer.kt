package io.kotest.core.internal.tags

class Lexer(private val input: String) {

   private var pointer = 0

   private fun next(): Char = input[pointer++]

   private fun eos(): Boolean = pointer == input.length

   private fun peek() = input[pointer]

   private fun readIdentifier(char: Char): Token {
      var lexeme = char.toString()
      while (!eos() && peek() != ' ' && peek() != ')' && peek() != '(') {
         lexeme += next()
      }
      return Token(
         lexeme,
         TokenType.Identifier
      )
   }

   fun lex(): List<Token> {
      val tokens = mutableListOf<Token>()
      while (!eos()) {
         val token = when (val char = next()) {
            '!' -> Token("!", TokenType.ExclamationMark)
            '&' -> Token("&", TokenType.Ampersand)
            ')' -> Token(")", TokenType.CloseParen)
            '(' -> Token("(", TokenType.OpenParen)
            '|' -> Token("|", TokenType.Pipe)
            ' ' -> null
            else -> readIdentifier(char)
         }
         if (token != null)
            tokens.add(token)
      }
      return tokens
   }
}
