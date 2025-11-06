#!/bin/bash
# visualize-ast.sh - Quick AST visualization tool for Javdin

if [ $# -eq 0 ]; then
    echo "Usage: ./visualize-ast.sh <javdin-file.d | ast-file.xml>"
    echo ""
    echo "Examples:"
    echo "  ./visualize-ast.sh presentation-example-1.d       # Parse .d file and visualize"
    echo "  ./visualize-ast.sh ast-before-optimization.xml    # Visualize existing AST XML"
    echo ""
    echo "This will generate:"
    echo "  - ast.xml (if input is .d file)"
    echo "  - ast-visualization.html (open in browser)"
    exit 1
fi

INPUT_FILE="$1"

if [ ! -f "$INPUT_FILE" ]; then
    echo "Error: File '$INPUT_FILE' not found!"
    exit 1
fi

# Check if input is XML or .d file
if [[ "$INPUT_FILE" == *.xml ]]; then
    # Input is already XML, just transform to HTML
    echo "Generating HTML visualization from AST XML: $INPUT_FILE"
    echo ""
    
    # Use XSLT to transform XML to HTML directly
    mvn -q exec:java \
        -Dexec.mainClass="com.javdin.demo.AstXmlToHtmlConverter" \
        -Dexec.args="$INPUT_FILE"
    
    EXIT_CODE=$?
else
    # Input is .d source file, parse and generate AST
    echo "Generating AST visualization for: $INPUT_FILE"
    echo ""
    
    mvn -q exec:java \
        -Dexec.mainClass="com.javdin.demo.AstVisualizationDemo" \
        -Dexec.args="$INPUT_FILE"
    
    EXIT_CODE=$?
fi

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo "[OK] Done! Visualization saved to: ast-visualization.html"
    
    # Try to open in browser (works on most Linux systems)
    if command -v xdg-open &> /dev/null; then
        xdg-open ast-visualization.html 2>/dev/null &
    fi
else
    echo ""
    echo "[ERROR] Error generating visualization"
    exit 1
fi
