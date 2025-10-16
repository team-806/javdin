# AST Visualization Tool

## Overview

The AST Visualization Tool generates beautiful, interactive HTML visualizations of Javdin Abstract Syntax Trees. This is useful for presentations, debugging, and understanding program structure.

## Quick Start

### Generate Visualization

```bash
mvn exec:java -Dexec.mainClass="com.javdin.demo.AstVisualizationDemo" \
              -Dexec.args="<your-javdin-file.d>"
```

Example:
```bash
mvn exec:java -Dexec.mainClass="com.javdin.demo.AstVisualizationDemo" \
              -Dexec.args="presentation-example-1.d"
```

### Output Files

The tool generates two files:

1. **ast.xml** - AST in XML format (intermediate representation)
2. **ast-visualization.html** - Interactive HTML tree (open in browser)

The HTML file will automatically open in your default browser. If not, simply double-click `ast-visualization.html` or open it manually.

## Features

### Visual Design
- **Dark Theme**: Professional dark color scheme (#1e1e1e background)
- **Minimal Color Palette**: Clean 2-3 color scheme for maximum compatibility
  - Gray tones for structure nodes
  - Light blue for identifiers
  - Light orange for literal values

### Interactive Elements
- **Collapsible Nodes**: Click any node to expand/collapse its children
- **Hover Effects**: Visual feedback when hovering over nodes
- **Tree Structure**: Clear parent-child relationships with connecting lines

## Architecture

### How It Works

1. **Parse**: Javdin source → Lexer → Parser → AST (ProgramNode)
2. **Serialize**: AST → Custom Visitor (AstXmlSerializer) → XML
3. **Transform**: XML → XSLT (ast-to-html.xsl) → HTML
4. **Display**: HTML rendered in browser

### Components

```
src/main/java/com/javdin/
├── visualization/
│   └── AstXmlSerializer.java       # Visitor-based XML serialization
└── demo/
    └── AstVisualizationDemo.java   # CLI tool

src/main/resources/xslt/
└── ast-to-html.xsl                 # XML → HTML transformation
```

### Technical Details

- **Visitor Pattern**: `AstXmlSerializer` implements `AstVisitor<String>`
- **XML Generation**: Custom XML with proper indentation and escaping
- **XSLT 1.0**: Standard Java XSLT transformer (javax.xml.transform)
- **No External Dependencies**: Uses only Java built-ins

## Usage Examples

### Factorial Function
```d
var factorial := func(n) is
    if n <= 1 then
        return 1
    else
        return n * factorial(n - 1)
    end
end

var result := factorial(5)
print result
```

Generated tree shows:
- `program` → `declaration` → `function_literal`
  - `parameters`: `n`
  - `body` → `if_statement`
    - `condition`: `n <= 1`
    - `then_block`: `return 1`
    - `else_block`: `return n * factorial(n-1)`

### For Loop Example
```d
var sum := 0
for i in [1, 2, 3, 4, 5] loop
    sum := sum + i
end
```

Tree structure:
- `program`
  - `declaration`: `sum := 0`
  - `for_statement`
    - `variable`: `i`
    - `iterable`: `[1, 2, 3, 4, 5]`
    - `body`: `sum := sum + i`

## Customization

### Modify Styling

Edit colors and fonts in `src/main/resources/xslt/ast-to-html.xsl`:

```xslt
<style>
  body {
    background-color: #1e1e1e;  /* Background */
    color: #d4d4d4;             /* Text color */
  }
  
  .node-identifier { color: #9cdcfe; }  /* Identifiers */
  .node-literal { color: #ce9178; }     /* Literals */
</style>
```

### Add New Node Types

If you add new AST node types:

1. Add visitor method to `AstVisitor` interface
2. Implement serialization in `AstXmlSerializer`
3. Optionally add styling in XSLT

## Troubleshooting

### "Could not find ast-to-html.xsl"
Run `mvn clean compile` to copy resources to target directory.

### Parsing Errors
Check your Javdin syntax. The tool displays detailed error messages.

### Browser Doesn't Open
Manually open `ast-visualization.html` in any web browser.

## Design Decisions

### Why Custom Serialization vs. CUP's XML?

**Initial Approach**: Tried CUP's `-xmlActions` flag for automatic XML generation.

**Problem Discovered**: CUP's `-xmlActions` **replaces** custom AST building. You can't have both:
- `-xmlActions` → Auto-generates `XMLElement` instead of custom nodes
- Our parser → Already builds custom AST (`ProgramNode`, `IfNode`, etc.)

**Solution**: Custom serialization using visitor pattern
- ✅ Works with existing AST
- ✅ Complete control over XML structure
- ✅ Simpler integration
- ✅ No grammar changes needed

### XML Structure

**Chosen Design**: Semantic, AST-focused XML
```xml
<if_statement>
  <condition>
    <binary_operation>
      <operator>&lt;=</operator>
      <left><identifier>n</identifier></left>
      <right><literal type="integer">1</literal></right>
    </binary_operation>
  </condition>
  <then_block>...</then_block>
  <else_block>...</else_block>
</if_statement>
```

**Alternative (Parse Tree)**: Would include grammar details (terminals, productions)
- ❌ Too verbose for presentations
- ❌ Includes implementation details
- ✅ Our approach focuses on semantic meaning

## Integration with Build Process

### Run as Maven Task

Add to `.vscode/tasks.json`:
```json
{
    "label": "🎨 Visualize AST",
    "type": "shell",
    "command": "mvn",
    "args": [
        "exec:java",
        "-Dexec.mainClass=com.javdin.demo.AstVisualizationDemo",
        "-Dexec.args=${input:sourceFile}"
    ],
    "problemMatcher": []
}
```

### Automated Testing

Use in tests to verify AST structure:
```java
@Test
public void testAstStructure() {
    Parser parser = new Parser(new Lexer(source));
    ProgramNode ast = parser.parse();
    
    AstXmlSerializer serializer = new AstXmlSerializer();
    String xml = serializer.serialize(ast);
    
    // Assert XML contains expected nodes
    assertTrue(xml.contains("<if_statement>"));
    assertTrue(xml.contains("<function_literal>"));
}
```

## Performance

- **Small programs** (<100 nodes): < 100ms
- **Medium programs** (100-1000 nodes): < 500ms
- **Large programs** (1000+ nodes): < 2 seconds

XSLT transformation is fast - most time is parsing.

## Future Enhancements

Potential improvements:

1. **Graph Visualization**: Generate Graphviz DOT format
2. **Diff Mode**: Compare ASTs of two programs
3. **Search/Filter**: Highlight specific node types
4. **Export**: SVG, PNG, PDF formats
5. **Annotations**: Add comments/notes to nodes
6. **Source Mapping**: Click node → jump to source line

## Credits

- **Parser**: CUP (Constructor of Useful Parsers)
- **XSLT**: Java built-in transformer
- **Design**: Custom dark theme inspired by VS Code

## License

Same license as Javdin project.
