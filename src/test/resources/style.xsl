<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                version="1.0"
>
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
    <xsl:template match="/">
        Name
        <xsl:for-each select="//test">
            <xsl:value-of
                    select="concat(@name,'&#xA;')"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>