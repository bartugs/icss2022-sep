package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues.push(new HashMap<>());
        applyStylesheet(ast.root);
        variableValues.pop();
    }

    private void applyStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
    }

    private void applyVariableAssignment(VariableAssignment node) {
        Literal value = evalExpression(node.expression);
        node.expression = value;
        variableValues.peek().put(node.name.name, value);
    }

    private void applyStylerule(Stylerule node) {
        variableValues.push(new HashMap<>());
        ArrayList<ASTNode> transformedBody = new ArrayList<>();

        for (ASTNode child : node.body) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
                transformedBody.add(child);
            } else if (child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child);
                transformedBody.add(child);
            } else if (child instanceof IfClause) {
                applyIfClause((IfClause) child, transformedBody);
            }
        }
        //if clauses verwijderen en correcte nodes in body zetten
        node.body.clear();
        node.body.addAll(transformedBody);
        variableValues.pop();
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evalExpression(node.expression);
    }

    private void applyIfClause(IfClause node, ArrayList<ASTNode> transformedBody) {
        Literal cond = evalExpression(node.conditionalExpression);
        if (!(cond instanceof BoolLiteral))
            throw new IllegalStateException("If condition must be boolean");

        boolean condValue = ((BoolLiteral) cond).value;

        variableValues.push(new HashMap<>());

        if (condValue) {
            for (ASTNode child : node.body) {
                if (child instanceof IfClause) {
                    applyIfClause((IfClause) child, transformedBody);
                } else if (child instanceof Declaration) {
                    applyDeclaration((Declaration) child);
                    transformedBody.add(child);
                } else if (child instanceof VariableAssignment) {
                    applyVariableAssignment((VariableAssignment) child);
                    transformedBody.add(child);
                }
            }
        } else if (node.elseClause != null) {
            for (ASTNode child : node.elseClause.body) {
                if (child instanceof IfClause) {
                    applyIfClause((IfClause) child, transformedBody);
                } else if (child instanceof Declaration) {
                    applyDeclaration((Declaration) child);
                    transformedBody.add(child);
                } else if (child instanceof VariableAssignment) {
                    applyVariableAssignment((VariableAssignment) child);
                    transformedBody.add(child);
                }
            }
        }

        variableValues.pop();
    }

    private Literal evalExpression(Expression node) {
        if (node instanceof Literal) {
            return (Literal) node;
        } else if (node instanceof VariableReference) {
            return lookupVariable(((VariableReference) node).name);
        } else if (node instanceof Operation) {
            return evalOperation((Operation) node);
        }
        throw new IllegalArgumentException("Unknown expression type: " + node.getClass().getSimpleName());
    }

    private Literal lookupVariable(String name) {
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        throw new IllegalStateException("Variable " + name + " not found.");
    }

    private Literal evalOperation(Operation node) {
        Literal left = evalExpression(node.lhs);
        Literal right = evalExpression(node.rhs);

        if (node instanceof AddOperation) return evalAdd(left, right);
        if (node instanceof SubtractOperation) return evalSubtract(left, right);
        if (node instanceof MultiplyOperation) return evalMultiply(left, right);

        throw new IllegalArgumentException("Unknown operation type: " + node.getClass().getSimpleName());
    }

    private Literal evalAdd(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral)
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        if (left instanceof PercentageLiteral && right instanceof PercentageLiteral)
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        if (left instanceof ScalarLiteral && right instanceof ScalarLiteral)
            return new ScalarLiteral(((ScalarLiteral) left).value + ((ScalarLiteral) right).value);
        throw new IllegalArgumentException("Invalid operands for addition");
    }

    private Literal evalSubtract(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral)
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        if (left instanceof PercentageLiteral && right instanceof PercentageLiteral)
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        if (left instanceof ScalarLiteral && right instanceof ScalarLiteral)
            return new ScalarLiteral(((ScalarLiteral) left).value - ((ScalarLiteral) right).value);
        throw new IllegalArgumentException("Invalid operands for subtraction");
    }

    private Literal evalMultiply(Literal left, Literal right) {
        if (left instanceof ScalarLiteral) {
            int val = ((ScalarLiteral) left).value;
            if (right instanceof PixelLiteral) return new PixelLiteral(val * ((PixelLiteral) right).value);
            if (right instanceof PercentageLiteral)
                return new PercentageLiteral(val * ((PercentageLiteral) right).value);
            if (right instanceof ScalarLiteral) return new ScalarLiteral(val * ((ScalarLiteral) right).value);
        }
        if (right instanceof ScalarLiteral) {
            int val = ((ScalarLiteral) right).value;
            if (left instanceof PixelLiteral) return new PixelLiteral(((PixelLiteral) left).value * val);
            if (left instanceof PercentageLiteral) return new PercentageLiteral(((PercentageLiteral) left).value * val);
        }
        throw new IllegalArgumentException("Invalid operands for multiplication");
    }
}