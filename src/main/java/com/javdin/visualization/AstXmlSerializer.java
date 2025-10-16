package com.javdin.visualization;

import com.javdin.ast.*;

/**
 * Serializes the Javdin AST to XML format for visualization.
 * 
 * This visitor traverses the AST and generates an XML representation
 * that can be transformed with XSLT into HTML visualizations.
 */
public class AstXmlSerializer implements AstVisitor<String> {
    
    private int indent = 0;
    
    private String getIndent() {
        return "  ".repeat(indent);
    }
    
    private String element(String name, String attributes, String content) {
        if (content == null || content.trim().isEmpty()) {
            return getIndent() + "<" + name + attributes + "/>\n";
        }
        return getIndent() + "<" + name + attributes + ">\n" + 
               content + 
               getIndent() + "</" + name + ">\n";
    }
    
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
    
    @Override
    public String visitProgram(ProgramNode node) {
        indent++;
        StringBuilder sb = new StringBuilder();
        for (StatementNode stmt : node.getStatements()) {
            sb.append(stmt.accept(this));
        }
        indent--;
        return element("program", "", sb.toString());
    }
    
    @Override
    public String visitDeclaration(DeclarationNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        for (DeclarationNode.VariableDefinition var : node.getVariables()) {
            content.append(getIndent()).append("<variable name=\"").append(escapeXml(var.getName())).append("\">\n");
            if (var.getInitialValue() != null) {
                indent++;
                content.append(var.getInitialValue().accept(this));
                indent--;
            }
            content.append(getIndent()).append("</variable>\n");
        }
        indent--;
        return element("declaration", " type=\"var\"", content.toString());
    }
    
    @Override
    public String visitAssignment(AssignmentNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(node.getTarget().accept(this));
        content.append(node.getValue().accept(this));
        indent--;
        return element("assignment", "", content.toString());
    }
    
    @Override
    public String visitExpressionStatement(ExpressionStatementNode node) {
        indent++;
        String content = node.getExpression().accept(this);
        indent--;
        return element("expression_statement", "", content);
    }
    
    @Override
    public String visitIf(IfNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(element("condition", "", node.getCondition().accept(this)));
        content.append(element("then_block", "", node.getThenStatement().accept(this)));
        if (node.getElseStatement() != null) {
            content.append(element("else_block", "", node.getElseStatement().accept(this)));
        }
        indent--;
        return element("if_statement", "", content.toString());
    }
    
    @Override
    public String visitWhile(WhileNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(element("condition", "", node.getCondition().accept(this)));
        content.append(element("body", "", node.getBody().accept(this)));
        indent--;
        return element("while_statement", "", content.toString());
    }
    
    @Override
    public String visitFor(ForNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        
        if (node.isInfiniteLoop()) {
            // Infinite loop: loop ... end
            content.append(getIndent()).append("<loop_type>infinite</loop_type>\n");
        } else if (node.isRangeLoop()) {
            // Range loop: for [var in] start..end loop ... end
            if (node.getVariable() != null) {
                content.append(getIndent()).append("<variable>").append(escapeXml(node.getVariable())).append("</variable>\n");
            }
            content.append(element("range_start", "", node.getIterable().accept(this)));
            content.append(element("range_end", "", node.getRangeEnd().accept(this)));
        } else {
            // Iterable loop: for [var in] iterable loop ... end
            if (node.getVariable() != null) {
                content.append(getIndent()).append("<variable>").append(escapeXml(node.getVariable())).append("</variable>\n");
            }
            content.append(element("iterable", "", node.getIterable().accept(this)));
        }
        
        content.append(element("body", "", node.getBody().accept(this)));
        indent--;
        return element("for_statement", "", content.toString());
    }
    
    @Override
    public String visitReturn(ReturnNode node) {
        indent++;
        String content = node.getValue() != null ? node.getValue().accept(this) : "";
        indent--;
        return element("return_statement", "", content);
    }
    
    @Override
    public String visitBreak(BreakNode node) {
        return element("break_statement", "", "");
    }
    
    @Override
    public String visitContinue(ContinueNode node) {
        return element("continue_statement", "", "");
    }
    
    @Override
    public String visitPrint(PrintNode node) {
        indent++;
        StringBuilder sb = new StringBuilder();
        for (ExpressionNode expr : node.getExpressions()) {
            sb.append(expr.accept(this));
        }
        indent--;
        return element("print_statement", "", sb.toString());
    }
    
    @Override
    public String visitBlock(BlockNode node) {
        indent++;
        StringBuilder sb = new StringBuilder();
        for (StatementNode stmt : node.getStatements()) {
            sb.append(stmt.accept(this));
        }
        indent--;
        return element("block", "", sb.toString());
    }
    
    @Override
    public String visitBinaryOp(BinaryOpNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(getIndent()).append("<operator>").append(escapeXml(node.getOperator())).append("</operator>\n");
        content.append(element("left", "", node.getLeft().accept(this)));
        content.append(element("right", "", node.getRight().accept(this)));
        indent--;
        return element("binary_operation", "", content.toString());
    }
    
    @Override
    public String visitUnaryOp(UnaryOpNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(getIndent()).append("<operator>").append(escapeXml(node.getOperator())).append("</operator>\n");
        content.append(element("operand", "", node.getOperand().accept(this)));
        indent--;
        return element("unary_operation", "", content.toString());
    }
    
    @Override
    public String visitFunctionCall(FunctionCallNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(element("function", "", node.getFunction().accept(this)));
        if (!node.getArguments().isEmpty()) {
            indent++;
            StringBuilder args = new StringBuilder();
            for (ExpressionNode arg : node.getArguments()) {
                args.append(arg.accept(this));
            }
            indent--;
            content.append(element("arguments", "", args.toString()));
        }
        indent--;
        return element("function_call", "", content.toString());
    }
    
    @Override
    public String visitFunctionLiteral(FunctionLiteralNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        
        if (!node.getParameters().isEmpty()) {
            content.append(getIndent()).append("<parameters>");
            content.append(escapeXml(String.join(", ", node.getParameters())));
            content.append("</parameters>\n");
        }
        
        // Handle both statement body and expression body
        if (node.isExpressionBody()) {
            content.append(element("body", " type=\"expression\"", node.getExpressionBody().accept(this)));
        } else {
            indent++;
            StringBuilder bodyContent = new StringBuilder();
            for (StatementNode stmt : node.getStatementBody()) {
                bodyContent.append(stmt.accept(this));
            }
            indent--;
            content.append(element("body", " type=\"statements\"", bodyContent.toString()));
        }
        
        indent--;
        return element("function_literal", "", content.toString());
    }
    
    @Override
    public String visitArrayLiteral(ArrayLiteralNode node) {
        indent++;
        StringBuilder sb = new StringBuilder();
        for (ExpressionNode elem : node.getElements()) {
            sb.append(elem.accept(this));
        }
        indent--;
        return element("array_literal", "", sb.toString());
    }
    
    @Override
    public String visitTupleLiteral(TupleLiteralNode node) {
        indent++;
        StringBuilder sb = new StringBuilder();
        for (TupleLiteralNode.TupleElement elem : node.getElements()) {
            sb.append(getIndent()).append("<element name=\"").append(escapeXml(elem.getName())).append("\">\n");
            indent++;
            sb.append(elem.getValue().accept(this));
            indent--;
            sb.append(getIndent()).append("</element>\n");
        }
        indent--;
        return element("tuple_literal", "", sb.toString());
    }
    
    @Override
    public String visitArrayAccess(ArrayAccessNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(element("array", "", node.getArray().accept(this)));
        content.append(element("index", "", node.getIndex().accept(this)));
        indent--;
        return element("array_access", "", content.toString());
    }
    
    @Override
    public String visitTupleMemberAccess(TupleMemberAccessNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(element("tuple", "", node.getTuple().accept(this)));
        content.append(getIndent()).append("<member>").append(escapeXml(node.getMemberName())).append("</member>\n");
        indent--;
        return element("tuple_access", "", content.toString());
    }
    
    @Override
    public String visitReference(ReferenceNode node) {
        return element("identifier", "", getIndent() + escapeXml(node.getName()) + "\n");
    }
    
    @Override
    public String visitLiteral(LiteralNode node) {
        Object value = node.getValue();
        String type;
        if (value instanceof Integer || value instanceof Long) {
            type = "integer";
        } else if (value instanceof Double || value instanceof Float) {
            type = "real";
        } else if (value instanceof String) {
            type = "string";
        } else if (value instanceof Boolean) {
            type = "boolean";
        } else if (value == null) {
            type = "none";
        } else {
            type = "unknown";
        }
        
        String valueStr = value == null ? "none" : value.toString();
        return element("literal", " type=\"" + type + "\"", 
                      getIndent() + escapeXml(valueStr) + "\n");
    }
    
    @Override
    public String visitTypeCheck(TypeCheckNode node) {
        indent++;
        StringBuilder content = new StringBuilder();
        content.append(element("expression", "", node.getExpression().accept(this)));
        content.append(getIndent()).append("<type>").append(escapeXml(node.getTypeIndicator())).append("</type>\n");
        indent--;
        return element("type_check", "", content.toString());
    }
    
    /**
     * Serialize an AST to XML
     */
    public String serialize(ProgramNode ast) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<document>\n");
        xml.append(ast.accept(this));
        xml.append("</document>\n");
        return xml.toString();
    }
}
