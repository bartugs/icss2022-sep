package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        //variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet node) {
        applyStylerule((Stylerule)node.getChildren().get(0));
    }

    private void applyStylerule(Stylerule node) {
        for(ASTNode child : node.getChildren()) {
            if(child instanceof Declaration) {
                applyDeclaration((Declaration)child);
            }
        }
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evalExpression(node.expression);
    }
//pixelliteral uiteindelijk literal
    private PixelLiteral evalExpression(Expression expression) {
        if(expression instanceof PixelLiteral) {
            return (PixelLiteral)expression;
        }else {
            return evalAddOperation((AddOperation)expression);
        }
    }

    private PixelLiteral evalAddOperation(AddOperation expression) {
        PixelLiteral left = evalExpression(expression.lhs);
        PixelLiteral right = evalExpression(expression.rhs);
        return new PixelLiteral(left.value+ right.value);
    }
}
