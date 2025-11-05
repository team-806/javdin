<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
		
<xsl:output method="html" version="5.0" encoding="UTF-8" indent="yes"/>

<!--
  This stylesheet transforms the simplified AST XML into an HTML visualization.
  Creates a tree structure with CSS styling.
-->

<xsl:template match="/">
  <html>
    <head>
      <title>Javdin AST Visualization</title>
      <style>
        body {
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          background: #1e1e1e;
          color: #d4d4d4;
          padding: 20px;
          margin: 0;
        }
        
        h1 {
          color: #b0b0b0;
          border-bottom: 2px solid #505050;
          padding-bottom: 10px;
        }
        
        .info {
          background: #252526;
          padding: 15px;
          border-radius: 5px;
          margin-bottom: 20px;
          border-left: 4px solid #505050;
        }
        
        .tree {
          margin-top: 20px;
          font-size: 14px;
        }
        
        .node {
          margin: 3px 0;
          padding: 4px 8px;
          border-radius: 3px;
          display: inline-block;
        }
        
        .node-nonterminal {
          background: #2d2d30;
          color: #d4d4d4;
          font-weight: bold;
          border: 1px solid #505050;
        }
        
        .node-terminal {
          background: #1e1e1e;
          color: #b0b0b0;
          border: 1px solid #3a3a3a;
        }
        
        .node-keyword {
          background: #2d2d30;
          color: #d4d4d4;
          font-weight: bold;
        }
        
        .node-identifier {
          background: #252526;
          color: #9cdcfe;
        }
        
        .node-literal {
          background: #252526;
          color: #ce9178;
        }
        
        .children {
          margin-left: 30px;
          border-left: 2px solid #3e3e42;
          padding-left: 10px;
          margin-top: 5px;
        }
        
        .attribute {
          color: #808080;
          font-size: 11px;
          margin-left: 10px;
        }
        
        .value {
          color: #b0b0b0;
          font-style: italic;
        }
        
        .line-info {
          color: #606060;
          font-size: 10px;
          margin-left: 8px;
        }
        
        .node:hover {
          box-shadow: 0 0 5px rgba(128, 128, 128, 0.3);
          cursor: pointer;
        }
        
        .collapsible {
          cursor: pointer;
          user-select: none;
        }
        
        .collapsible::before {
          content: '▼ ';
          display: inline-block;
          margin-right: 6px;
          transition: transform 0.2s;
        }
        
        .collapsed::before {
          transform: rotate(-90deg);
        }
        
        .collapsed + .children {
          display: none;
        }
      </style>
      <script>
        <![CDATA[
        function toggleCollapse(element) {
          element.classList.toggle('collapsed');
        }
        ]]>
      </script>
    </head>
    <body>
      <h1>Javdin AST Visualization</h1>
      
      <div class="info">
        <strong>Abstract Syntax Tree</strong><br/>
        Generated from parser output<br/>
        Click on nodes to collapse/expand
      </div>
      
      <div class="tree">
        <xsl:apply-templates/>
      </div>
    </body>
  </html>
</xsl:template>

<!-- Match document root -->
<xsl:template match="document">
  <xsl:apply-templates select="*"/>
</xsl:template>

<!-- Match nonterminals (non-leaf nodes) -->
<xsl:template match="*[*]">
  <div>
    <span class="node node-nonterminal collapsible" onclick="toggleCollapse(this)">
      <xsl:value-of select="local-name()"/>
      <xsl:if test="@variant">
        <span class="attribute">variant=<span class="value"><xsl:value-of select="@variant"/></span></span>
      </xsl:if>
      <xsl:if test="@line">
        <span class="line-info">L<xsl:value-of select="@line"/>:<xsl:value-of select="@column"/></span>
      </xsl:if>
    </span>
    
    <div class="children">
      <xsl:apply-templates select="*"/>
    </div>
  </div>
</xsl:template>

<!-- Match terminals (leaf nodes) -->
<xsl:template match="*[not(*)]">
  <div>
    <xsl:variable name="nodeName" select="local-name()"/>
    <xsl:variable name="nodeClass">
      <xsl:choose>
        <xsl:when test="$nodeName = 'IDENTIFIER'">node-identifier</xsl:when>
        <xsl:when test="$nodeName = 'INTEGER' or $nodeName = 'REAL' or $nodeName = 'STRING' or $nodeName = 'BOOL'">node-literal</xsl:when>
        <xsl:when test="contains($nodeName, 'TYPE') or $nodeName = 'VAR' or $nodeName = 'IF' or $nodeName = 'WHILE' or $nodeName = 'FUNC' or $nodeName = 'RETURN'">node-keyword</xsl:when>
        <xsl:otherwise>node-terminal</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <span class="node {$nodeClass}">
      <xsl:value-of select="local-name()"/>
      <xsl:if test="text() and text() != ''">
        <span class="value"> = "<xsl:value-of select="text()"/>"</span>
      </xsl:if>
      <xsl:if test="@line">
        <span class="line-info">L<xsl:value-of select="@line"/>:<xsl:value-of select="@column"/></span>
      </xsl:if>
    </span>
  </div>
</xsl:template>

</xsl:stylesheet>
