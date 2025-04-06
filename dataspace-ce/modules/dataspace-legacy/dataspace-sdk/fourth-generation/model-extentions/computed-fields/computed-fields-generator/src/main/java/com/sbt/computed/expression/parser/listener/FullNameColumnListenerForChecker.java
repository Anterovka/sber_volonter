package com.sbt.computed.expression.parser.listener;

import com.sbt.computed.expression.exception.ComputedArgumentParserExpressionException;
import com.sbt.computed.expression.exception.InvalidParserExpressionException;
import com.sbt.computed.expression.exception.PropertyFromOtherClassInParserExpressionException;
import com.sbt.computed.expression.exception.PropertyNotFromClassParserExpressionException;
import com.sbt.computed.expression.parser.CheckExpression;
import com.sbt.computed.expression.parser.ExpressionParser;
import com.sbt.computed.expression.parser.ExpressionParserListener;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class FullNameColumnListenerForChecker implements IFullNameColumnListener {

    private final XmlModelClassProperty property;

    public FullNameColumnListenerForChecker(XmlModelClassProperty property) {
        this.property = property;
    }


    /******************************************************************************************************************/
    @Override
    public void enterFullColumnName(ExpressionParser.FullColumnNameContext ctx) {

        Interval of = Interval.of(0, ctx.getStart().getInputStream().size());

        String expression = ctx.getStart().getInputStream().getText(of);

        final String[] tempArray = new String[2];

        if (ctx.children.size() == 1) {
            tempArray[1] = getPropertyName(ctx.children.get(0));
        } else if (ctx.children.size() == 2) {

            tempArray[0] = getPropertyName(ctx.children.get(0));
            tempArray[1] = getPropertyName(ctx.children.get(1)).replaceFirst("\\.", "");

            if (this.property.getModelClass().getName().compareToIgnoreCase(tempArray[0]) == 0
            ) {
                this.property.getModelClass().getPropertiesAsList()
                        .stream()
                        .filter(p -> p.getName().compareToIgnoreCase(tempArray[1]) == 0)
                        .findFirst()
                        .ifPresentOrElse(cc -> {
                        }, () -> {
                            throw new PropertyFromOtherClassInParserExpressionException(expression, tempArray[0], tempArray[1]);
                        });
            } else {

                Stream<XmlModelClass> embeddedClasses = this.property.getModelClass().getModel().getClassesAsList().stream().filter(c -> c.isEmbeddable());
                Optional<XmlModelClassProperty> oEmbProp = this.property.getModelClass().getPropertiesAsList().stream().filter(p -> p.getName().compareToIgnoreCase(tempArray[0]) == 0).findFirst();
                if (oEmbProp.isEmpty()) {
                    throw new PropertyFromOtherClassInParserExpressionException(expression, tempArray[0], tempArray[1]);
                }
                embeddedClasses
                        .filter(p -> p.getName().compareToIgnoreCase(oEmbProp.get().getType()) == 0)
                        .findFirst()
                        .ifPresentOrElse(c -> {

                            c.getPropertiesAsList()
                                    .stream()
                                    .filter(pp -> pp.getName().compareToIgnoreCase(tempArray[1]) == 0)
                                    .findFirst().ifPresentOrElse(cc -> {
                                    }, () -> {
                                        throw new PropertyFromOtherClassInParserExpressionException(expression, tempArray[0], tempArray[1]);

                                    });
                        }, () -> {
                            throw new PropertyFromOtherClassInParserExpressionException(expression, tempArray[0], tempArray[1]);
                        });
                return;

            }


        } else {
            throw new InvalidParserExpressionException(expression);
        }


        Optional<XmlModelClassProperty> propertyOptional
                = this.property.getModelClass().getPropertiesAsList().stream()
                .filter(p -> p.getName().compareToIgnoreCase(tempArray[1]) == 0)
                .findFirst();


        if (!propertyOptional.isPresent()) {
            throw new PropertyNotFromClassParserExpressionException(expression, tempArray[1]);
        } else if (propertyOptional.get().getComputedExpression() != null) {
            throw new ComputedArgumentParserExpressionException(expression, tempArray[1]);
        }


    }


    private String getPropertyName(ParseTree pTrees) {
        if (pTrees.getChild(0) instanceof TerminalNodeImpl) {
            return pTrees.getChild(0).getText();
        } else {
            return getPropertyName(pTrees.getChild(0));
        }
    }


    @Override
    public void exitFullColumnName(ExpressionParser.FullColumnNameContext ctx) {
    }

    /******************************************************************************************************************/


    @Override
    public void enterFullId(ExpressionParser.FullIdContext ctx) {

    }

    @Override
    public void exitFullId(ExpressionParser.FullIdContext ctx) {

    }


    @Override
    public void enterCollationName(ExpressionParser.CollationNameContext ctx) {

    }

    @Override
    public void exitCollationName(ExpressionParser.CollationNameContext ctx) {

    }


    @Override
    public void enterUid(ExpressionParser.UidContext ctx) {

    }

    @Override
    public void exitUid(ExpressionParser.UidContext ctx) {

    }

    @Override
    public void enterSimpleId(ExpressionParser.SimpleIdContext ctx) {

    }

    @Override
    public void exitSimpleId(ExpressionParser.SimpleIdContext ctx) {

    }

    @Override
    public void enterDottedId(ExpressionParser.DottedIdContext ctx) {

    }

    @Override
    public void exitDottedId(ExpressionParser.DottedIdContext ctx) {

    }

    @Override
    public void enterDecimalLiteral(ExpressionParser.DecimalLiteralContext ctx) {

    }

    @Override
    public void exitDecimalLiteral(ExpressionParser.DecimalLiteralContext ctx) {

    }

    @Override
    public void enterStringLiteral(ExpressionParser.StringLiteralContext ctx) {

    }

    @Override
    public void exitStringLiteral(ExpressionParser.StringLiteralContext ctx) {

    }

    @Override
    public void enterBooleanLiteral(ExpressionParser.BooleanLiteralContext ctx) {

    }

    @Override
    public void exitBooleanLiteral(ExpressionParser.BooleanLiteralContext ctx) {

    }

    @Override
    public void enterNullNotnull(ExpressionParser.NullNotnullContext ctx) {

    }

    @Override
    public void exitNullNotnull(ExpressionParser.NullNotnullContext ctx) {

    }

    @Override
    public void enterConstant(ExpressionParser.ConstantContext ctx) {

    }

    @Override
    public void exitConstant(ExpressionParser.ConstantContext ctx) {

    }


    @Override
    public void enterExpressions(ExpressionParser.ExpressionsContext ctx) {

    }

    @Override
    public void exitExpressions(ExpressionParser.ExpressionsContext ctx) {

    }

    @Override
    public void enterFunctionCall(ExpressionParser.FunctionCallContext ctx) {

    }

    @Override
    public void exitFunctionCall(ExpressionParser.FunctionCallContext ctx) {

    }

    @Override
    public void enterSpecificFunction(ExpressionParser.SpecificFunctionContext ctx) {

    }

    @Override
    public void exitSpecificFunction(ExpressionParser.SpecificFunctionContext ctx) {

    }

    @Override
    public void enterCaseFuncAlternative(ExpressionParser.CaseFuncAlternativeContext ctx) {

    }

    @Override
    public void exitCaseFuncAlternative(ExpressionParser.CaseFuncAlternativeContext ctx) {

    }


    @Override
    public void enterFunctionArgs(ExpressionParser.FunctionArgsContext ctx) {

    }

    @Override
    public void exitFunctionArgs(ExpressionParser.FunctionArgsContext ctx) {

    }

    @Override
    public void enterFunctionArg(ExpressionParser.FunctionArgContext ctx) {

    }

    @Override
    public void exitFunctionArg(ExpressionParser.FunctionArgContext ctx) {

    }

    @Override
    public void enterExpression(ExpressionParser.ExpressionContext ctx) {

    }

    @Override
    public void exitExpression(ExpressionParser.ExpressionContext ctx) {

    }

    @Override
    public void enterPredicate(ExpressionParser.PredicateContext ctx) {

    }

    @Override
    public void exitPredicate(ExpressionParser.PredicateContext ctx) {

    }

    @Override
    public void enterExpressionAtom(ExpressionParser.ExpressionAtomContext ctx) {

    }

    @Override
    public void exitExpressionAtom(ExpressionParser.ExpressionAtomContext ctx) {

    }

    @Override
    public void enterUnaryOperator(ExpressionParser.UnaryOperatorContext ctx) {

    }

    @Override
    public void exitUnaryOperator(ExpressionParser.UnaryOperatorContext ctx) {

    }

    @Override
    public void enterComparisonOperator(ExpressionParser.ComparisonOperatorContext ctx) {

    }

    @Override
    public void exitComparisonOperator(ExpressionParser.ComparisonOperatorContext ctx) {

    }

    @Override
    public void enterLogicalOperator(ExpressionParser.LogicalOperatorContext ctx) {

    }

    @Override
    public void exitLogicalOperator(ExpressionParser.LogicalOperatorContext ctx) {

    }


    @Override
    public void enterMathOperator(ExpressionParser.MathOperatorContext ctx) {

    }

    @Override
    public void exitMathOperator(ExpressionParser.MathOperatorContext ctx) {

    }

    @Override
    public void enterFunctionNameBase(ExpressionParser.FunctionNameBaseContext ctx) {

    }

    @Override
    public void exitFunctionNameBase(ExpressionParser.FunctionNameBaseContext ctx) {

    }

    @Override
    public void enterComputedExpression(ExpressionParser.ComputedExpressionContext ctx) {

    }

    @Override
    public void exitComputedExpression(ExpressionParser.ComputedExpressionContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode node) {

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }
}
