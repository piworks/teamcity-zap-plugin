import io
import os
import sys
import lxml.html
from lxml import etree

def InsertXSLTSheetIntoXmlReport(xmlreportfile, xsltfile, xmlreportfileout):
    xslt_doc = etree.parse(xsltfile)
    xslt_transformer = etree.XSLT(xslt_doc)
    
    source_doc = etree.parse(xmlreportfile)
    output_doc = xslt_transformer(source_doc)
    
    output_doc.write("UglyZapReport.html", pretty_print=True)

def PrettyReport(xmlreportfileout):

    delete_list = ['&lt;/p&gt;', '&lt;p&gt;', '<div class="spacer"/>', '<a name="low"/>', '</a>']
    fin = open("UglyZapReport.html")
    fout = open(xmlreportfileout, "w+")
    for line in fin:
        for word in delete_list:
            if word == '<div class="spacer"/>' :
                line = line.replace(word, '</div><div class="spacer"/>')
            else :
                line = line.replace(word, '')
        fout.write(line)
    fin.close()
    fout.close()
    os.remove("UglyZapReport.html")

InsertXSLTSheetIntoXmlReport(sys.argv[1], sys.argv[2], sys.argv[3])
PrettyReport(sys.argv[3])