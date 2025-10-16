# AST Visualization - Quick Reference Card

## 🎯 One-Line Command
```bash
./visualize-ast.sh presentation-example-1.d
```

## 📋 What You Get
- `ast.xml` - XML representation
- `ast-visualization.html` - Interactive tree (auto-opens)

## 🎨 Visual Features
- Dark theme (#1e1e1e)
- Color-coded nodes (blue/green/orange/purple)
- Click to expand/collapse
- Hover effects

## 🔧 Alternative Commands

### Maven (longer but explicit)
```bash
mvn exec:java -Dexec.mainClass="com.javdin.demo.AstVisualizationDemo" \
              -Dexec.args="your-file.d"
```

### Build First (if needed)
```bash
mvn clean compile
```

## 📂 Output Location
Current directory: `./ast-visualization.html`

## 🐛 Quick Fixes

### Problem: Browser doesn't open
→ Manually open `ast-visualization.html`

### Problem: "Could not find xsl"
→ Run `mvn clean compile`

### Problem: Parsing error
→ Check Javdin syntax

## 📚 Documentation
- Full guide: `AST-VISUALIZATION-README.md`
- Summary: `AST-VISUALIZATION-SUMMARY.md`

## ✅ Verified Examples
- ✓ presentation-example-1.d (factorial)
- ✓ presentation-example-2.d (arrays & loops)
- ✓ All 193 tests passing

## 🎓 For Presentations
1. Pre-generate visualizations
2. Open HTML in browser
3. Show tree structure
4. Click nodes to demo interactivity
5. Explain AST concepts using colors

## 🚀 Implementation
- **Parser**: CUP-generated
- **Serializer**: Custom visitor (AstXmlSerializer)
- **Transform**: XSLT (ast-to-html.xsl)
- **Dependencies**: Java built-ins only

## 💡 Pro Tips
- Use dark theme presentations (matches viz theme)
- Zoom browser for better visibility (Ctrl/Cmd +)
- Click root node first for dramatic reveal
- Explain color coding before diving in

---

**Ready to use! Good luck with your presentation! 🎉**
