<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:exsl="http://exslt.org/common"
		extension-element-prefixes="exsl">
		
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<!-- 
  This stylesheet transforms CUP's raw parse tree XML into a simplified AST.
  It flattens degenerate trees, removes unnecessary nesting, and optimizes expression depth.
-->

<!-- Blacklist: nonterminals that should NOT be collapsed (preserve their structure) -->
<xsl:variable name="blacklist">
  <symbol>program</symbol>
  <symbol>statement</symbol>
  <symbol>declaration</symbol>
  <symbol>if_statement</symbol>
  <symbol>while_statement</symbol>
  <symbol>for_statement</symbol>
</xsl:variable>

<!-- Root template -->
<xsl:template match="/">
  <xsl:variable name="flatten">
    <xsl:apply-templates mode="flatten"/>
  </xsl:variable>
  <xsl:copy-of select="$flatten"/>
</xsl:template>

<!-- Skip blacklist nodes in output -->
<xsl:template match="blacklist" mode="flatten"/>

<!-- Process document root -->
<xsl:template match="document" mode="flatten">
  <document>
    <xsl:apply-templates mode="flatten" select="parsetree"/>
  </document>
</xsl:template>

<!-- Process nonterminals -->
<xsl:template match="nonterminal" mode="flatten">
  <xsl:variable name="id" select="@id"/>
  
  <xsl:choose>
    <!-- Flatten expression chains (3 children: expr op expr) -->
    <xsl:when test="count(*) = 3 and contains($id, 'expr') and *[contains(@id, 'expr')]">
      <xsl:apply-templates mode="flatten"/>
    </xsl:when>
    
    <!-- Collapse degenerate trees (parent and child have same name) -->
    <xsl:when test="../@id = @id and count($blacklist/symbol[text() = $id]) = 0">
      <xsl:apply-templates mode="flatten"/>
    </xsl:when>
    
    <!-- Collapse unary productions (single child, not in blacklist) -->
    <xsl:when test="count(*) = 3 and count($blacklist/symbol[text() = $id]) = 0">
      <xsl:apply-templates mode="flatten"/>
    </xsl:when>
    
    <!-- Otherwise, keep the node -->
    <xsl:otherwise>
      <xsl:element name="{@id}">
        <xsl:attribute name="variant">
          <xsl:value-of select="@variant"/>
        </xsl:attribute>
        <xsl:if test="@line">
          <xsl:attribute name="line">
            <xsl:value-of select="@line"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@column">
          <xsl:attribute name="column">
            <xsl:value-of select="@column"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:apply-templates mode="flatten"/>
      </xsl:element>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Process terminals -->
<xsl:template match="terminal" mode="flatten">
  <xsl:element name="{@id}">
    <xsl:if test="@line">
      <xsl:attribute name="line">
        <xsl:value-of select="@line"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@column">
      <xsl:attribute name="column">
        <xsl:value-of select="@column"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:value-of select="text()"/>
  </xsl:element>
</xsl:template>

<!-- Copy text nodes -->
<xsl:template match="text()" mode="flatten">
  <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
