#!/bin/bash
# visualize-ast.sh - Quick AST visualization tool for Javdin

if [ $# -eq 0 ]; then
    echo "Usage: ./visualize-ast.sh <javdin-file.d>"
    echo ""
    echo "Example:"
    echo "  ./visualize-ast.sh presentation-example-1.d"
    echo ""
    echo "This will generate:"
    echo "  - ast.xml (intermediate XML)"
    echo "  - ast-visualization.html (open in browser)"
    exit 1
fi

JAVDIN_FILE="$1"

if [ ! -f "$JAVDIN_FILE" ]; then
    echo "Error: File '$JAVDIN_FILE' not found!"
    exit 1
fi

echo "Generating AST visualization for: $JAVDIN_FILE"
echo ""

mvn -q exec:java \
    -Dexec.mainClass="com.javdin.demo.AstVisualizationDemo" \
    -Dexec.args="$JAVDIN_FILE"

if [ $? -eq 0 ]; then
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
