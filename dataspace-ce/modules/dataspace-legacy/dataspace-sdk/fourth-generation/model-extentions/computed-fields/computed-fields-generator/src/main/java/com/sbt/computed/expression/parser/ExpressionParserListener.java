// Generated from C:/Users/18527953/Desktop/Projects/WORK_PROJECT/dataspace-sdk/fourth-generation/model-extentions/computed-fields/computed-fields-generator/src/main/resources/grammars\ExpressionParser.g4 by ANTLR 4.9
package com.sbt.computed.expression.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionParser}.
 */
public interface ExpressionParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#fullId}.
	 * @param ctx the parse tree
	 */
	void enterFullId(ExpressionParser.FullIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#fullId}.
	 * @param ctx the parse tree
	 */
	void exitFullId(ExpressionParser.FullIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void enterFullColumnName(ExpressionParser.FullColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void exitFullColumnName(ExpressionParser.FullColumnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#collationName}.
	 * @param ctx the parse tree
	 */
	void enterCollationName(ExpressionParser.CollationNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#collationName}.
	 * @param ctx the parse tree
	 */
	void exitCollationName(ExpressionParser.CollationNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#uid}.
	 * @param ctx the parse tree
	 */
	void enterUid(ExpressionParser.UidContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#uid}.
	 * @param ctx the parse tree
	 */
	void exitUid(ExpressionParser.UidContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#simpleId}.
	 * @param ctx the parse tree
	 */
	void enterSimpleId(ExpressionParser.SimpleIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#simpleId}.
	 * @param ctx the parse tree
	 */
	void exitSimpleId(ExpressionParser.SimpleIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#dottedId}.
	 * @param ctx the parse tree
	 */
	void enterDottedId(ExpressionParser.DottedIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#dottedId}.
	 * @param ctx the parse tree
	 */
	void exitDottedId(ExpressionParser.DottedIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDecimalLiteral(ExpressionParser.DecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDecimalLiteral(ExpressionParser.DecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(ExpressionParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(ExpressionParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#nullNotnull}.
	 * @param ctx the parse tree
	 */
	void enterNullNotnull(ExpressionParser.NullNotnullContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#nullNotnull}.
	 * @param ctx the parse tree
	 */
	void exitNullNotnull(ExpressionParser.NullNotnullContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(ExpressionParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(ExpressionParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#expressions}.
	 * @param ctx the parse tree
	 */
	void enterExpressions(ExpressionParser.ExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#expressions}.
	 * @param ctx the parse tree
	 */
	void exitExpressions(ExpressionParser.ExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(ExpressionParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(ExpressionParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#specificFunction}.
	 * @param ctx the parse tree
	 */
	void enterSpecificFunction(ExpressionParser.SpecificFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#specificFunction}.
	 * @param ctx the parse tree
	 */
	void exitSpecificFunction(ExpressionParser.SpecificFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#caseFuncAlternative}.
	 * @param ctx the parse tree
	 */
	void enterCaseFuncAlternative(ExpressionParser.CaseFuncAlternativeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#caseFuncAlternative}.
	 * @param ctx the parse tree
	 */
	void exitCaseFuncAlternative(ExpressionParser.CaseFuncAlternativeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#functionArgs}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArgs(ExpressionParser.FunctionArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#functionArgs}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArgs(ExpressionParser.FunctionArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArg(ExpressionParser.FunctionArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArg(ExpressionParser.FunctionArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(ExpressionParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(ExpressionParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterExpressionAtom(ExpressionParser.ExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitExpressionAtom(ExpressionParser.ExpressionAtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(ExpressionParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(ExpressionParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(ExpressionParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(ExpressionParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOperator(ExpressionParser.LogicalOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOperator(ExpressionParser.LogicalOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void enterMathOperator(ExpressionParser.MathOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void exitMathOperator(ExpressionParser.MathOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#functionNameBase}.
	 * @param ctx the parse tree
	 */
	void enterFunctionNameBase(ExpressionParser.FunctionNameBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#functionNameBase}.
	 * @param ctx the parse tree
	 */
	void exitFunctionNameBase(ExpressionParser.FunctionNameBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#computedExpression}.
	 * @param ctx the parse tree
	 */
	void enterComputedExpression(ExpressionParser.ComputedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#computedExpression}.
	 * @param ctx the parse tree
	 */
	void exitComputedExpression(ExpressionParser.ComputedExpressionContext ctx);
}