// Generated from C:/Users/18527953/Desktop/Projects/WORK_PROJECT/dataspace-sdk/fourth-generation/model-extentions/computed-fields/computed-fields-generator/src/main/resources/grammars\ExpressionParser.g4 by ANTLR 4.9
package com.sbt.computed.expression.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExpressionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExpressionParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#fullId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFullId(ExpressionParser.FullIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#fullColumnName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFullColumnName(ExpressionParser.FullColumnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#collationName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollationName(ExpressionParser.CollationNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#uid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUid(ExpressionParser.UidContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#simpleId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleId(ExpressionParser.SimpleIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#dottedId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDottedId(ExpressionParser.DottedIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#decimalLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalLiteral(ExpressionParser.DecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#stringLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#booleanLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(ExpressionParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#nullNotnull}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullNotnull(ExpressionParser.NullNotnullContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(ExpressionParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#expressions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressions(ExpressionParser.ExpressionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(ExpressionParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#specificFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecificFunction(ExpressionParser.SpecificFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#caseFuncAlternative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseFuncAlternative(ExpressionParser.CaseFuncAlternativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#functionArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArgs(ExpressionParser.FunctionArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#functionArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArg(ExpressionParser.FunctionArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicate(ExpressionParser.PredicateContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionAtom(ExpressionParser.ExpressionAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#unaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOperator(ExpressionParser.UnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(ExpressionParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#logicalOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOperator(ExpressionParser.LogicalOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#mathOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperator(ExpressionParser.MathOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#functionNameBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionNameBase(ExpressionParser.FunctionNameBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#computedExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComputedExpression(ExpressionParser.ComputedExpressionContext ctx);
}