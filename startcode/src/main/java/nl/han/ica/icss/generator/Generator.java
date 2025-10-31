package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class Generator {

    public String generate(AST ast) {
        return generateStylesheet(ast.root);
    }

    private String generateStylesheet(Stylesheet stylesheet) {
        StringBuilder css = new StringBuilder();

        for (ASTNode child : stylesheet.getChildren()) {
            if (child instanceof Stylerule) {
                css.append(generateStylerule((Stylerule) child)).append("\n\n");
            }
        }

        return css.toString().trim();
    }

    private String generateStylerule(Stylerule stylerule) {
        StringBuilder css = new StringBuilder();

        // selectors
        for (int i = 0; i < stylerule.selectors.size(); i++) {
            css.append(stylerule.selectors.get(i).toString());
            if (i < stylerule.selectors.size() - 1) {
                css.append(", ");
            }
        }
        css.append(" {\n");

        // declarations
        for (ASTNode child : stylerule.body) {
            if (child instanceof Declaration) {
                css.append(generateDeclaration((Declaration) child));
            }
        }

        css.append("}\n");
        return css.toString();
    }

    private String generateDeclaration(Declaration declaration) {
        return "  " + declaration.property.name + ": " + generateExpression(declaration.expression) + ";\n";
    }

    private String generateExpression(Expression expression) {
        if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value + "px";
        } else if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value + "%";
        } else if (expression instanceof ScalarLiteral) {
            return String.valueOf(((ScalarLiteral) expression).value);
        } else if (expression instanceof ColorLiteral) {
            return ((ColorLiteral) expression).value;
        }
        return "";
    }
}
