# CUP XML Capabilities - Research Checklist

**Goal**: Determine if CUP has built-in XML output capabilities and how to use them

---

## Research Tasks

### ✅ Task 1: Examine Generated CupParser Class

**File**: `target/generated-sources/cup/com/javdin/parser/generated/CupParser.java`

**Look for**:
- [ ] Methods containing "xml" or "XML"
- [ ] Methods containing "dump" or "debug"
- [ ] Methods for parse tree output
- [ ] Configuration options for debugging

**Commands to run**:
```bash
# Search for XML-related methods
grep -i "xml" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java

# Search for dump methods
grep -i "dump" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java

# Search for debug methods
grep -i "debug" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java

# List all public methods
grep "public.*(" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java
```

**Expected findings**:
- Methods like `xmlDump()`, `debug_parse()`, `setDebug()`, etc.
- Or: No XML support found → use custom implementation

---

### ✅ Task 2: Check CUP Runtime API

**File**: Java CUP runtime JAR (in Maven dependencies)

**Look for**:
- [ ] `java_cup.runtime.lr_parser` class methods
- [ ] Documentation comments
- [ ] Configuration fields

**Commands to run**:
```bash
# Find the CUP runtime JAR
find ~/.m2/repository -name "*java-cup-runtime*.jar"

# Extract and examine
jar tf /path/to/java-cup-runtime-11b-20160615.jar

# Decompile lr_parser class
javap -classpath ~/.m2/repository/com/github/vbmacher/java-cup-runtime/11b-20160615/java-cup-runtime-11b-20160615.jar \
  java_cup.runtime.lr_parser
```

**Look for these methods**:
- `parse()`
- `debug_parse()`
- `xmlDump()`
- `setDebug(boolean)`
- `getParseTree()`

---

### ✅ Task 3: Review CUP Grammar Options

**File**: `src/main/resources/parser.cup`

**Check for**:
- [ ] Parser code directives
- [ ] Debug options
- [ ] Custom parser actions

**CUP directives to research**:
```cup
/* Possible CUP options: */
parser code {:
    // Can we add XML dump methods here?
:};

action code {:
    // Can we capture parse tree here?
:};

init with {:
    // Can we enable debugging here?
:};
```

**Try adding**:
```cup
parser code {:
    public boolean getDebug() { return debug; }
    public void setDebug(boolean d) { debug = d; }
    
    // Can we access parse tree?
    // Can we add custom XML output?
:};
```

---

### ✅ Task 4: Check CUP Official Documentation

**Sources**:
- https://www2.cs.tum.edu/projects/cup/
- https://www2.cs.tum.edu/projects/cup/manual.html
- https://www2.cs.tum.edu/projects/cup/docs.php

**Search for**:
- [ ] "XML" - any mention of XML output
- [ ] "parse tree" - how to access parse tree
- [ ] "debugging" - debug output options
- [ ] "AST" - abstract syntax tree features
- [ ] Example grammars with XML output

**Download examples**:
```bash
# If CUP provides example grammars
wget https://www2.cs.tum.edu/projects/cup/examples.zip
unzip examples.zip
grep -r "xml" examples/
```

---

### ✅ Task 5: Test CUP Debug Mode

**Create test**: `src/test/java/com/javdin/parser/CupDebugTest.java`

```java
package com.javdin.parser;

import com.javdin.lexer.Lexer;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CupDebugTest {
    
    @Test
    public void testDebugOutput() throws Exception {
        String input = "var x := 42";
        
        // Capture console output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        
        try {
            Lexer lexer = new Lexer(input);
            LexerAdapter scanner = new LexerAdapter(lexer);
            CupParser parser = new CupParser(scanner);
            
            // Try enabling debug mode
            // parser.setDebug(true);  // Does this exist?
            
            parser.parse();
            
            System.out.flush();
            System.setOut(old);
            
            String output = baos.toString();
            System.out.println("Debug output:");
            System.out.println(output);
            
        } catch (Exception e) {
            System.setOut(old);
            throw e;
        }
    }
}
```

**Run**:
```bash
mvn test -Dtest=CupDebugTest
```

---

### ✅ Task 6: Examine CUP Source Code (if needed)

**Repository**: https://github.com/vbmacher/cup-maven-plugin

**Clone and explore**:
```bash
git clone https://github.com/vbmacher/cup-maven-plugin
cd cup-maven-plugin
find . -name "*.java" | xargs grep -l "xml\|XML"
find . -name "*.java" | xargs grep -l "parse.*tree"
```

---

## Decision Matrix

After research, choose implementation path:

### Path A: CUP Has XML Support ✅
**Indicators**:
- Found `xmlDump()` or similar method
- Found `debug_parse()` with XML output
- Documentation mentions XML/parse tree access

**Next steps**:
1. Implement `parseToXml()` using CUP's built-in feature
2. Create XSLT transformations
3. Build visualization tool

**Estimated time**: 8-12 hours

---

### Path B: CUP Lacks XML Support (Custom Implementation) 🔨
**Indicators**:
- No XML methods found
- No parse tree access API
- Documentation silent on XML

**Next steps**:
1. Implement custom `AstXmlSerializer` using visitor pattern
2. Create XSLT transformations
3. Build visualization tool

**Estimated time**: 10-14 hours

---

### Path C: Minimal (Emergency Fallback) ⚡
**When**: Very limited time or both paths blocked

**Implementation**:
1. Add `toString()` methods to AST nodes
2. Format as indented tree structure
3. Wrap in `<pre>` tags for HTML
4. No XML or XSLT needed

**Estimated time**: 2-4 hours

---

## Research Findings Template

Use this template to document findings:

```markdown
## CUP XML Research Results

**Date**: October 16, 2025
**Researcher**: [Your Name]

### Methods Found in CupParser:
- [ ] `xmlDump()` - YES/NO - Description: ___
- [ ] `debug_parse()` - YES/NO - Description: ___
- [ ] `setDebug()` - YES/NO - Description: ___
- [ ] Other: ___

### Methods Found in lr_parser:
- [ ] Listed methods: ___

### Documentation Findings:
- [ ] XML output mentioned: YES/NO
- [ ] Parse tree access: YES/NO
- [ ] Relevant sections: ___

### Decision:
- [ ] Path A: Use CUP built-in XML
- [ ] Path B: Custom implementation
- [ ] Path C: Minimal fallback

### Reasoning:
[Explain decision]

### Next Steps:
1. ___
2. ___
3. ___
```

---

## Quick Test Script

Save as `test-cup-xml.sh`:

```bash
#!/bin/bash

echo "=== CUP XML Capabilities Research ==="
echo ""

echo "1. Checking generated CupParser..."
if grep -q "xml" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java; then
    echo "   ✅ Found 'xml' in CupParser"
    grep -n "xml" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java
else
    echo "   ❌ No 'xml' found in CupParser"
fi
echo ""

echo "2. Checking for debug methods..."
if grep -q "debug" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java; then
    echo "   ✅ Found 'debug' in CupParser"
    grep -n "debug" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java
else
    echo "   ❌ No 'debug' found in CupParser"
fi
echo ""

echo "3. Listing public methods..."
grep "public.*(" target/generated-sources/cup/com/javdin/parser/generated/CupParser.java | head -20
echo ""

echo "4. Checking CUP runtime JAR..."
CUP_JAR=$(find ~/.m2/repository -name "java-cup-runtime*.jar" 2>/dev/null | head -1)
if [ -n "$CUP_JAR" ]; then
    echo "   ✅ Found: $CUP_JAR"
    echo "   Methods in lr_parser:"
    javap -classpath "$CUP_JAR" java_cup.runtime.lr_parser | grep "public"
else
    echo "   ❌ CUP runtime JAR not found"
fi

echo ""
echo "=== Research Complete ==="
```

**Run**:
```bash
chmod +x test-cup-xml.sh
./test-cup-xml.sh
```

---

## Additional Resources

### CUP Versions to Check:
- Current: 11b-20160615
- Alternative: Try newer versions if XML support is missing
- Alternative: Look for CUP2 (if exists)

### Alternative Tools:
If CUP completely lacks tree output:
- **ANTLR**: Has excellent tree visualization
- **JavaCC**: Has tree building support
- **Hand-coded**: Just use our existing AST

### Similar Projects:
Search GitHub for:
- "CUP XML"
- "CUP visualization"
- "CUP parse tree"
- "java-cup AST"

---

**Start Research**: Check Task 1 first, then proceed based on findings.

**Time Box**: Spend max 2 hours on research before deciding on implementation path.
