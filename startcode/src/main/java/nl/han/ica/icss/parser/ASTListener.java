package nl.han.ica.icss.parser;

import java.beans.Statement;
import java.util.Properties;
import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
		ast.setRoot(stylesheet);
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override
	public void enterId_selector(ICSSParser.Id_selectorContext ctx) {
		IdSelector selector = new IdSelector(ctx.getText());
		currentContainer.push(selector);
	}

	@Override
	public void exitId_selector(ICSSParser.Id_selectorContext ctx) {
		IdSelector selector = (IdSelector)currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterTag_selector(ICSSParser.Tag_selectorContext ctx) {
		TagSelector selector = new TagSelector(ctx.getText());
		currentContainer.push(selector);
	}

	@Override
	public void exitTag_selector(ICSSParser.Tag_selectorContext ctx) {
		TagSelector selector = (TagSelector)currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterClass_selector(ICSSParser.Class_selectorContext ctx) {
		ClassSelector selector = new ClassSelector(ctx.getText());
		currentContainer.push(selector);
	}

	@Override
	public void exitClass_selector(ICSSParser.Class_selectorContext ctx) {
		ClassSelector selector = (ClassSelector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration)currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterProperty(ICSSParser.PropertyContext ctx) {
		PropertyName property = new PropertyName(ctx.getText());
		currentContainer.push(property);
	}

	@Override
	public void exitProperty(ICSSParser.PropertyContext ctx) {
		PropertyName property = (PropertyName)currentContainer.pop();
		currentContainer.peek().addChild(property);
	}

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifc = new IfClause();
		currentContainer.push(ifc);
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifc = (IfClause)currentContainer.pop();
		currentContainer.peek().addChild(ifc);
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause elsec = new ElseClause();
		currentContainer.push(elsec);
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause elsec = (ElseClause)currentContainer.pop();
		currentContainer.peek().addChild(elsec);
	}

	@Override
	public void enterMulExpr(ICSSParser.MulExprContext ctx) {
		MultiplyOperation mul = new MultiplyOperation();
		currentContainer.push(mul);
	}

	@Override
	public void exitMulExpr(ICSSParser.MulExprContext ctx) {
		MultiplyOperation mul = (MultiplyOperation) currentContainer.pop();
		currentContainer.peek().addChild(mul);
	}

	@Override
	public void enterAddExpr(ICSSParser.AddExprContext ctx) {
		AddOperation add = new AddOperation();
		currentContainer.push(add);
	}

	@Override
	public void exitAddExpr(ICSSParser.AddExprContext ctx) {
		AddOperation add = (AddOperation) currentContainer.pop();
		currentContainer.peek().addChild(add);
	}

	@Override
	public void enterSubExpr(ICSSParser.SubExprContext ctx) {
		SubtractOperation sub = new SubtractOperation();
		currentContainer.push(sub);
	}

	@Override
	public void exitSubExpr(ICSSParser.SubExprContext ctx) {
		SubtractOperation sub = (SubtractOperation) currentContainer.pop();
		currentContainer.peek().addChild(sub);
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		PixelLiteral px = new PixelLiteral(ctx.getText());
		currentContainer.push(px);
	}

	@Override
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		PixelLiteral px = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(px);
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		PercentageLiteral perc = new PercentageLiteral(ctx.getText());
		currentContainer.push(perc);
	}

	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		PercentageLiteral perc = (PercentageLiteral) currentContainer.pop();
		currentContainer.peek().addChild(perc);
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ScalarLiteral scalar = new ScalarLiteral(ctx.getText());
		currentContainer.push(scalar);
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ScalarLiteral scalar = (ScalarLiteral) currentContainer.pop();
		currentContainer.peek().addChild(scalar);
	}

	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ColorLiteral color = new ColorLiteral(ctx.getText());
		currentContainer.push(color);
	}

	@Override
	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ColorLiteral color = (ColorLiteral) currentContainer.pop();
		currentContainer.peek().addChild(color);
	}

	@Override
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		BoolLiteral bool = new BoolLiteral(ctx.getText());
		currentContainer.push(bool);
	}

	@Override
	public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		BoolLiteral bool = (BoolLiteral) currentContainer.pop();
		currentContainer.peek().addChild(bool);
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment var = new VariableAssignment();
		currentContainer.push(var);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment var = (VariableAssignment) currentContainer.pop();
		currentContainer.peek().addChild(var);
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		VariableReference var = new VariableReference(ctx.getText());
		currentContainer.push(var);
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		VariableReference var = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(var);
	}
}