package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet sheet) {
        variableTypes.push(new HashMap<>());
        for (ASTNode child : sheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
        variableTypes.pop();
    }

    private void checkVariableAssignment(VariableAssignment assignment) {
        ExpressionType type = getExpressionType(assignment.expression);

        if (type == ExpressionType.UNDEFINED) {
            assignment.setError("Variabele '" + assignment.name.name + "' heeft een onbekend type.");
            return;
        }

        variableTypes.peek().put(assignment.name.name, type);
    }
    private void checkStylerule(Stylerule rule) {
        variableTypes.push(new HashMap<>());

        for (ASTNode child : rule.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }else if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
        variableTypes.pop();
    }

    private void checkDeclaration(Declaration declaration) {
        String propertyName = declaration.property.name;
        ExpressionType type = getExpressionType(declaration.expression);

        if (propertyName.equals("width") || propertyName.equals("height")) {
            if (type != ExpressionType.PIXEL && type != ExpressionType.PERCENTAGE) {
                declaration.setError("Property '" + propertyName + "' vereist PIXEL of PERCENTAGE, maar kreeg " + type);
            }
        } else if (propertyName.equals("color") || propertyName.equals("background-color")) {
            if (type != ExpressionType.COLOR) {
                declaration.setError("Property '" + propertyName + "' expects a color value.");
            }
        }else{
            declaration.setError("Property '" + propertyName + "' is niet toegestaan in ICSS.");
        }
    }

    private ExpressionType getExpressionType(Expression expr) {
        if (expr instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expr instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expr instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expr instanceof ScalarLiteral) return ExpressionType.SCALAR;
        if (expr instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expr instanceof VariableReference) return getTypeOfVariable(((VariableReference) expr).name);
        if (expr instanceof Operation) return evaluateOperation((Operation) expr);
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType getTypeOfVariable(String varName) {
        for (HashMap<String, ExpressionType> scope : variableTypes) {
            if (scope.containsKey(varName)) return scope.get(varName);
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType evaluateOperation(Operation op) {
        ExpressionType leftType = getExpressionType(op.lhs);
        ExpressionType rightType = getExpressionType(op.rhs);
        // Check op onbekende types
        if (leftType == ExpressionType.UNDEFINED || rightType == ExpressionType.UNDEFINED) {
            op.setError("Onbekend type in operatie.");
            return ExpressionType.UNDEFINED;
        }

        // Kleuren zijn nooit toegestaan
        if (leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) {
            op.setError("Kleuren kunnen niet in operaties gebruikt worden.");
            return ExpressionType.UNDEFINED;
        }

        if (op instanceof AddOperation || op instanceof SubtractOperation) {
            // Beide operands moeten hetzelfde type zijn
            if (leftType != rightType) {
                op.setError("Type mismatch in operatie: " + leftType + " vs " + rightType);
                return ExpressionType.UNDEFINED;
            }
            return leftType;
        }

        if (op instanceof MultiplyOperation) {
            // Verplicht 1 scalar
            if (leftType == ExpressionType.SCALAR) return rightType;
            if (rightType == ExpressionType.SCALAR) return leftType;

            op.setError("Vermenigvuldiging vereist minimaal één SCALAR operand.");
            return ExpressionType.UNDEFINED;
        }

        return ExpressionType.UNDEFINED;
    }
}
