package io.kotest.engine.tags

internal fun Parser.primary(): Expression {
   return if (peek()?.type == TokenType.OpenParen) {
      skip()
      val expr = expression()
      skip(TokenType.CloseParen)
      expr
   } else {
      val token = consume(TokenType.Identifier)
      return Expression.Identifier(token.lexeme)
   }
}

internal fun Parser.not(): Expression {
   return if (skipIf(TokenType.ExclamationMark)) {
      val ident = primary()
      Expression.Not(ident)
   } else primary()
}

internal fun Parser.and(): Expression {
   var left = not()
   while (skipIf(TokenType.Ampersand)) {
      val right = expression()
      left = Expression.And(left, right)
   }
   return left
}

internal fun Parser.or(): Expression {
   var left = and()
   while (skipIf(TokenType.Pipe)) {
      val right = expression()
      left = Expression.Or(left, right)
   }
   return left
}

internal fun Parser.expression(): Expression {
   return or()
}

sealed class Expression {
   data class Or(val left: Expression, val right: Expression) : Expression()
   data class And(val left: Expression, val right: Expression) : Expression()
   data class Not(val expr: Expression) : Expression()
   data class Identifier(val ident: String) : Expression()
}

fun Expression.asString(): String = when (this) {
   is Expression.Or -> left.asString() + " | " + right.asString()
   is Expression.And -> left.asString() + " & " + right.asString()
   is Expression.Not -> "!" + expr.asString()
   is Expression.Identifier -> this.ident
}
