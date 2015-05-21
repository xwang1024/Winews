<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<conf name="dataSource">
			<conf name="newspaper">
				<xsl:for-each select="newspaperList/newspaper">
					<conf name="{name}">
						<conf name="domain" value="{domain}" />
						<conf name="province" value="{province}" />
						<xsl:for-each select="url">
							<conf name="url" start="{@start}" end="{@end}">
								<conf name="rootFormat" value="{format/node}" />
								<conf name="nodePattern" value="{pattern/node}" />
								<conf name="contentPattern" value="{pattern/content}" />
							</conf>
							<conf name="date" start="{@start}" end="{@end}">
								<conf name="pattern" value="{pattern/date}" />
								<conf name="format" value="{format/date}" />
							</conf>
						</xsl:for-each>
						<xsl:for-each select="selector">
							<conf name="selector" start="{@start}" end="{@end}">
								<conf name="preTitle" value="{preTitle}" />
								<conf name="title" value="{title}" />
								<conf name="subTitle" value="{subTitle}" />
								<conf name="author" value="{author}" />
								<conf name="layout" value="{layout}" />
								<conf name="content" value="{content}" />
								<conf name="pictures" value="{picture}" />
							</conf>
						</xsl:for-each>
					</conf>
				</xsl:for-each>
			</conf>
		</conf>
	</xsl:template>
</xsl:stylesheet>