#!/bin/bash
# Semantic Analysis Demo Script
# Usage: ./demo-semantic-analysis.sh [demo-number]
# Or: ./demo-semantic-analysis.sh all

DEMO_CLASS="com.javdin.demo.SemanticAnalysisDemo"
DEMOS_DIR="test-resources/semantic-analysis"

show_menu() {
    echo "==============================================================="
    echo "  Javdin Semantic Analysis - Interactive Demo"
    echo "==============================================================="
    echo ""
    echo "SEMANTIC CHECKS:"
    echo "  1) Return outside function (ERROR)"
    echo "  2) Return outside function (FIXED)"
    echo "  3) Break outside loop (ERROR)"
    echo "  4) Break outside loop (FIXED)"
    echo "  5) Undeclared variable (ERROR)"
    echo "  6) Undeclared variable (FIXED)"
    echo "  7) Duplicate declaration (ERROR)"
    echo "  8) Duplicate declaration (FIXED)"
    echo ""
    echo "OPTIMIZATIONS:"
    echo "  9) Constant folding"
    echo " 10) Unused variable removal"
    echo " 11) Dead branch elimination"
    echo " 12) Unreachable code removal"
    echo " 13) Combined optimizations"
    echo ""
    echo "OTHER:"
    echo "  all) Run all demos"
    echo "  q) Quit"
    echo ""
}

run_demo() {
    local file=$1
    local optimize=$2
    
    echo ""
    echo "==============================================================="
    echo "Running: $file"
    echo "==============================================================="
    echo ""
    
    if [ "$optimize" = "yes" ]; then
        mvn exec:java -Dexec.mainClass="$DEMO_CLASS" \
            -Dexec.args="$file --optimize" -q
        
        # After optimization, offer to visualize AST
        if [ -f "ast-before-optimization.xml" ] && [ -f "ast-after-optimization.xml" ]; then
            echo ""
            read -p "Visualize AST before/after optimization? (y/n): " visualize
            if [ "$visualize" = "y" ] || [ "$visualize" = "Y" ]; then
                echo ""
                echo "Generating visualization for BEFORE optimization..."
                ./visualize-ast.sh ast-before-optimization.xml
                echo ""
                echo "Generated: ast-visualization.html (BEFORE)"
                echo ""
                read -p "Press Enter to generate AFTER visualization..."
                echo ""
                echo "Generating visualization for AFTER optimization..."
                ./visualize-ast.sh ast-after-optimization.xml
                echo ""
                echo "Generated: ast-visualization.html (AFTER)"
                echo ""
                echo "TIP: You can compare the HTML files or open them in a browser"
            fi
        fi
    else
        mvn exec:java -Dexec.mainClass="$DEMO_CLASS" \
            -Dexec.args="$file" -q
    fi
    
    echo ""
    read -p "Press Enter to continue..."
}

run_all() {
    echo "Running all demos..."
    
    # Semantic checks - errors
    run_demo "$DEMOS_DIR/semantic-check-1-return-outside-function-error.d" "no"
    run_demo "$DEMOS_DIR/semantic-check-2-break-outside-loop-error.d" "no"
    run_demo "$DEMOS_DIR/semantic-check-3-undeclared-variable-error.d" "no"
    run_demo "$DEMOS_DIR/semantic-check-4-duplicate-declaration-error.d" "no"
    
    # Semantic checks - fixed
    run_demo "$DEMOS_DIR/semantic-check-1-return-outside-function-fixed.d" "no"
    run_demo "$DEMOS_DIR/semantic-check-2-break-outside-loop-fixed.d" "no"
    run_demo "$DEMOS_DIR/semantic-check-3-undeclared-variable-fixed.d" "no"
    run_demo "$DEMOS_DIR/semantic-check-4-duplicate-declaration-fixed.d" "no"
    
    # Optimizations
    run_demo "$DEMOS_DIR/optimization-1-constant-folding.d" "yes"
    run_demo "$DEMOS_DIR/optimization-2-unused-variables.d" "yes"
    run_demo "$DEMOS_DIR/optimization-3-dead-branch-elimination.d" "yes"
    run_demo "$DEMOS_DIR/optimization-4-unreachable-code.d" "yes"
    run_demo "$DEMOS_DIR/optimization-5-combined.d" "yes"
}

# Check if argument provided
if [ $# -eq 1 ]; then
    case $1 in
        1) run_demo "$DEMOS_DIR/semantic-check-1-return-outside-function-error.d" "no" ;;
        2) run_demo "$DEMOS_DIR/semantic-check-1-return-outside-function-fixed.d" "no" ;;
        3) run_demo "$DEMOS_DIR/semantic-check-2-break-outside-loop-error.d" "no" ;;
        4) run_demo "$DEMOS_DIR/semantic-check-2-break-outside-loop-fixed.d" "no" ;;
        5) run_demo "$DEMOS_DIR/semantic-check-3-undeclared-variable-error.d" "no" ;;
        6) run_demo "$DEMOS_DIR/semantic-check-3-undeclared-variable-fixed.d" "no" ;;
        7) run_demo "$DEMOS_DIR/semantic-check-4-duplicate-declaration-error.d" "no" ;;
        8) run_demo "$DEMOS_DIR/semantic-check-4-duplicate-declaration-fixed.d" "no" ;;
        9) run_demo "$DEMOS_DIR/optimization-1-constant-folding.d" "yes" ;;
        10) run_demo "$DEMOS_DIR/optimization-2-unused-variables.d" "yes" ;;
        11) run_demo "$DEMOS_DIR/optimization-3-dead-branch-elimination.d" "yes" ;;
        12) run_demo "$DEMOS_DIR/optimization-4-unreachable-code.d" "yes" ;;
        13) run_demo "$DEMOS_DIR/optimization-5-combined.d" "yes" ;;
        all) run_all ;;
        *) echo "Invalid demo number. Use 1-13 or 'all'" ;;
    esac
    exit 0
fi

# Interactive mode
while true; do
    clear
    show_menu
    read -p "Select demo number (or 'q' to quit): " choice
    
    case $choice in
        1) run_demo "$DEMOS_DIR/semantic-check-1-return-outside-function-error.d" "no" ;;
        2) run_demo "$DEMOS_DIR/semantic-check-1-return-outside-function-fixed.d" "no" ;;
        3) run_demo "$DEMOS_DIR/semantic-check-2-break-outside-loop-error.d" "no" ;;
        4) run_demo "$DEMOS_DIR/semantic-check-2-break-outside-loop-fixed.d" "no" ;;
        5) run_demo "$DEMOS_DIR/semantic-check-3-undeclared-variable-error.d" "no" ;;
        6) run_demo "$DEMOS_DIR/semantic-check-3-undeclared-variable-fixed.d" "no" ;;
        7) run_demo "$DEMOS_DIR/semantic-check-4-duplicate-declaration-error.d" "no" ;;
        8) run_demo "$DEMOS_DIR/semantic-check-4-duplicate-declaration-fixed.d" "no" ;;
        9) run_demo "$DEMOS_DIR/optimization-1-constant-folding.d" "yes" ;;
        10) run_demo "$DEMOS_DIR/optimization-2-unused-variables.d" "yes" ;;
        11) run_demo "$DEMOS_DIR/optimization-3-dead-branch-elimination.d" "yes" ;;
        12) run_demo "$DEMOS_DIR/optimization-4-unreachable-code.d" "yes" ;;
        13) run_demo "$DEMOS_DIR/optimization-5-combined.d" "yes" ;;
        all) run_all ;;
        q|Q) echo "Goodbye!"; exit 0 ;;
        *) 
            echo "Invalid choice. Please select 1-13, 'all', or 'q'"
            read -p "Press Enter to continue..."
            ;;
    esac
done
