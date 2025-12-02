#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/scripts/test-suite-common.sh"

ensure_jar || exit 1

run_all() {
    reset_counters
    mapfile -t files < <(find "$REPO_ROOT/test-resources" -type f -name '*.d' | sort)
    if [ ${#files[@]} -eq 0 ]; then
        echo "No .d files under test-resources/"
        return
    fi

    echo "======================================"
    echo "All Test Resources"
    echo "======================================"
    echo ""
    for file in "${files[@]}"; do
        run_test_file "$file"
    done
    print_summary "All Test Resources"
}

run_dir_suite() {
    local dir="$1"
    local label="$2"
    reset_counters
    run_suite "$dir" "$label"
    print_summary "$label"
}

print_menu() {
    cat <<'EOF'
==============================
 Javdin Test Suite Menu
==============================
1) Run arrays suite
2) Run tuples suite
3) Run functions suite
4) Run loops suite
5) Run language-d-specific suite
6) Run semantic-analysis suite
7) Run ALL test-resources
q) Quit
EOF
}

while true; do
    print_menu
    read -rp "Select option: " choice
    case "$choice" in
        1)
            run_dir_suite "$REPO_ROOT/test-resources/arrays" "Array Suite"
            ;;
        2)
            run_dir_suite "$REPO_ROOT/test-resources/tuples" "Tuple Suite"
            ;;
        3)
            run_dir_suite "$REPO_ROOT/test-resources/functions" "Function Suite"
            ;;
        4)
            run_dir_suite "$REPO_ROOT/test-resources/loops" "Loop Suite"
            ;;
        5)
            run_dir_suite "$REPO_ROOT/test-resources/language-d-specific" "Language-D Specific Suite"
            ;;
        6)
            run_dir_suite "$REPO_ROOT/test-resources/semantic-analysis" "Semantic Analysis Suite"
            ;;
        7)
            run_all
            ;;
        q|Q)
            exit 0
            ;;
        *)
            echo "Unknown option: $choice"
            ;;
    esac
    echo ""
    read -rp "Press Enter to return to the menu..." _dummy
    clear
done
