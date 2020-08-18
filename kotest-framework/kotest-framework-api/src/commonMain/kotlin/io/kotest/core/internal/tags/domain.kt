package io.kotest.core.internal.tags

data class Token(val lexeme: String, val type: TokenType)

enum class TokenType {
   ExclamationMark, Pipe, Ampersand, Identifier, OpenParen, CloseParen
}
