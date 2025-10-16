# Understanding and Running the Javdin Parser

This document explains how the parser works, why you see certain errors, and how to properly test it.

---

## Issue 1: IDE Shows "Unresolved Import" Errors

### The Problem

Your IDE (likely VS Code or Eclipse) shows errors like:
```
Cannot resolve import com.javdin.parser.generated.CupParser
Cannot resolve import com.javdin.parser.generated.Symbols
```

### Why This Happens

**This is EXPECTED behavior!** Here's why:

1. **Generated Code Location**: CUP generates `CupParser.java` and `Symbols.java` during the `mvn compile` phase
2. **Generated Location**: `target/generated-sources/cup/com/javdin/parser/generated/`
3. **IDE Issue**: Your IDE hasn't indexed the generated sources yet

### The Solution

**Option 1: Compile First (Recommended)**
```bash
mvn clean compile
```
This generates the files. Then refresh your IDE's project structure.

**Option 2: In VS Code**
- Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
- Type "Java: Clean Java Language Server Workspace"
- Restart VS Code

**Option 3: In Eclipse**
- Right-click project → Maven → Update Project
- Check "Force Update of Snapshots/Releases"

### Verification

After compilation, check:
```bash
ls -la target/generated-sources/cup/com/javdin/parser/generated/
```

You should see:
```
CupParser.java
Symbols.java
```

**Important**: These files are **generated** during build, not committed to git. They're in `.gitignore`.

---

## Issue 2: NoClassDefFoundError When Running Parser Directly

### The Problem

```bash
java -cp target/classes com.javdin.parser.Parser presentation-example-1.d
# Error: Unable to initialize main class com.javdin.parser.Parser
# Caused by: java.lang.NoClassDefFoundError: java_cup/runtime/Scanner
```

### Why This Happens

**This is ALSO expected!** Here's the issue:

1. **Missing Dependency**: `Parser` class uses `java_cup.runtime.Scanner` (from CUP library)
2. **Classpath Issue**: `-cp target/classes` only includes YOUR compiled code, not dependencies
3. **Dependencies Location**: CUP runtime JAR is in Maven's local repository (`~/.m2/repository/`)

### Why Parser Class Has No main() Method

The `Parser` class is a **library class**, not an executable. It's designed to be used by `Main.java`.

**Parser.java** is like this:
```java
public class Parser {
    public Parser(Lexer lexer) { ... }
    public ProgramNode parse() throws ParseException { ... }
    // NO main() method!
}
```

It's meant to be called FROM another class, not run directly.

### The Solution: Use Main.java

The correct entry point is `Main.java`, which HAS a main() method:

```bash
# DON'T DO THIS (won't work):
java -cp target/classes com.javdin.parser.Parser file.d

# DO THIS instead (but still needs dependencies):
java -cp target/classes com.javdin.main.Main file.d
```

But this STILL won't work because of missing dependencies!

---

## Issue 3: Running the JAR File Incorrectly

### The Problem

```bash
java target/javdin-1.0.0.jar presentation-example-1.d
# Error: Could not find or load main class target.javdin-1.0.0.jar
```

### Why This Happens

**Wrong syntax!** You're missing the `-jar` flag.

### The Solution

```bash
# WRONG:
java target/javdin-1.0.0.jar presentation-example-1.d

# CORRECT:
java -jar target/javdin-1.0.0.jar presentation-example-1.d
```

The `-jar` flag tells Java to run a JAR file as an executable.

---

## How to Properly Test the Parser

Here are **ALL the working methods** to test your parser:

### Method 1: Using the Shaded JAR (Easiest! ✅ Recommended)

```bash
# Step 1: Build the JAR with all dependencies
mvn clean package

# Step 2: Run with -jar flag
java -jar target/javdin-1.0.0.jar presentation-example-1.d
```

**Why this works**:
- `mvn package` creates a **fat JAR** (includes all dependencies)
- Maven Shade Plugin bundles CUP runtime into the JAR
- No classpath issues!

### Method 2: Using Maven Exec Plugin

```bash
# Run directly with Maven (automatically handles dependencies)
mvn exec:java -Dexec.mainClass="com.javdin.main.Main" -Dexec.args="presentation-example-1.d"
```

**Why this works**:
- Maven manages the classpath
- All dependencies automatically included

### Method 3: Manual Classpath (Advanced)

If you want to run from `target/classes`, you need to include ALL dependencies:

```bash
# Step 1: Get all dependencies in one line
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt

# Step 2: Run with full classpath
java -cp "target/classes:$(cat cp.txt)" com.javdin.main.Main presentation-example-1.d
```

**Why this works**:
- `dependency:build-classpath` lists all JAR files
- `-cp` includes both your code AND dependencies

### Method 4: Use Tests (For Development)

Create a simple test to parse your file:

```java
@Test
void testMyProgram() throws Exception {
    String source = Files.readString(Path.of("presentation-example-1.d"));
    Lexer lexer = new Lexer(source);
    Parser parser = new Parser(lexer);
    ProgramNode ast = parser.parse();
    
    assertThat(ast).isNotNull();
    assertThat(ast.getStatements()).isNotEmpty();
}
```

Run with:
```bash
mvn test -Dtest=MyTest
```

---

## Quick Reference Commands

### Build Commands
```bash
# Clean and compile (generates parser)
mvn clean compile

# Run all tests
mvn test

# Build executable JAR
mvn clean package
```

### Run Commands
```bash
# Run the interpreter (CORRECT METHOD)
java -jar target/javdin-1.0.0.jar presentation-example-1.d

# Parse and show just syntax (create a demo class for this - see below)
mvn exec:java -Dexec.mainClass="com.javdin.demo.ParserDemo" -Dexec.args="presentation-example-1.d"
```

---

## Creating a Simple Parser Demo

Since you want to test JUST the parser (not the full interpreter), let's create a demo class:

**File**: `src/main/java/com/javdin/demo/ParserDemo.java`

```java
package com.javdin.demo;

import com.javdin.lexer.Lexer;
import com.javdin.parser.Parser;
import com.javdin.ast.ProgramNode;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParserDemo {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java ParserDemo <source-file>");
            System.exit(1);
        }
        
        // Read source
        String source = Files.readString(Path.of(args[0]));
        
        System.out.println("SOURCE CODE:");
        System.out.println("=".repeat(60));
        System.out.println(source);
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Parse
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        ProgramNode ast = parser.parse();
        
        System.out.println("✓ PARSING SUCCESSFUL!");
        System.out.println("AST Root: " + ast.getClass().getSimpleName());
        System.out.println("Number of statements: " + ast.getStatements().size());
        System.out.println();
        
        System.out.println("Statement types:");
        for (int i = 0; i < ast.getStatements().size(); i++) {
            var stmt = ast.getStatements().get(i);
            System.out.println("  " + (i+1) + ". " + stmt.getClass().getSimpleName());
        }
    }
}
```

**Run it**:
```bash
# Compile
mvn compile

# Run with Maven exec
mvn exec:java -Dexec.mainClass="com.javdin.demo.ParserDemo" -Dexec.args="presentation-example-1.d"

# Or rebuild JAR and run
mvn package
java -cp target/javdin-1.0.0.jar com.javdin.demo.ParserDemo presentation-example-1.d
```

---

## Understanding the Build Process

### What Happens During `mvn compile`

1. **CUP Plugin** (`generate` phase):
   ```
   Input:  src/main/resources/parser.cup
   Output: target/generated-sources/cup/com/javdin/parser/generated/
           ├── CupParser.java
           └── Symbols.java
   ```

2. **Build Helper Plugin**:
   - Adds `target/generated-sources/cup` to source path
   - IDE/compiler can now find generated files

3. **Java Compiler**:
   - Compiles everything in `src/main/java` + generated sources
   - Output: `target/classes/`

### What Happens During `mvn package`

1. Everything from `mvn compile`
2. **Maven JAR Plugin**: Creates basic JAR
   - Output: `target/original-javdin-1.0.0.jar` (small, no dependencies)
3. **Maven Shade Plugin**: Creates fat JAR
   - Includes all dependencies (CUP runtime, etc.)
   - Sets main class in MANIFEST.MF
   - Output: `target/javdin-1.0.0.jar` (large, ~4MB)

---

## Summary: Your Questions Answered

### Q1: Why "unresolved import" in IDE?

**A**: Generated code doesn't exist until `mvn compile`. IDE doesn't know about it yet.

**Solution**: 
```bash
mvn clean compile
# Then refresh IDE
```

### Q2: Is `NoClassDefFoundError` expected?

**A**: YES! When running with incomplete classpath.

**Why**: Missing CUP runtime dependency in classpath.

**Solution**: Use `java -jar target/javdin-1.0.0.jar` instead.

### Q3: How to run the JAR correctly?

**Wrong**:
```bash
java target/javdin-1.0.0.jar file.d  # Missing -jar!
```

**Correct**:
```bash
java -jar target/javdin-1.0.0.jar file.d
```

### Q4: How to test parser with my own programs?

**Best method**:
```bash
# 1. Build JAR
mvn clean package

# 2. Run
java -jar target/javdin-1.0.0.jar your-program.d
```

**Alternative** (just parsing, no interpretation):
1. Create `ParserDemo.java` (see above)
2. Run with Maven exec plugin

---

## Troubleshooting Checklist

If something doesn't work:

- [ ] Did you run `mvn clean compile` first?
- [ ] Are you using `java -jar` (not just `java`)?
- [ ] Is the JAR file path correct? (`target/javdin-1.0.0.jar`)
- [ ] Does the input file exist? (`presentation-example-1.d`)
- [ ] Is the JAR file the shaded one? (Should be ~4MB, not 90KB)

---

**Need more help?** Check:
- `mvn dependency:tree` - See all dependencies
- `jar tf target/javdin-1.0.0.jar` - List JAR contents
- `mvn -X compile` - Verbose compilation output
