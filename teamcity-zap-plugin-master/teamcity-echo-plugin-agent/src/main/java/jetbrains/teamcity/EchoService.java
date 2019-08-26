package jetbrains.teamcity;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.TCStreamUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EchoService extends BuildServiceAdapter {
  private final Set<File> myFilesToDelete = new HashSet<File>();

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

    final String url = getRunnerParameters().get(EchoRunnerConstants.MESSAGE_KEY);
    String zapPathOriginal = getRunnerParameters().get(EchoRunnerConstants.ZAP_PATH);
    String zapPath = zapPathOriginal.replace(" ", "` ");
    zapPath = zapPath.replace("(", "`(");
    zapPath = zapPath.replace(")", "`)");
    String failOpt = getRunnerParameters().get(EchoRunnerConstants.FAIL_OPT);

    //"C:\\Users\\furkan.yangil\\AppData\\Local\\Programs\\Python\\Python37\\python.exe xsl_to_html.py ZapReportXML.xml " + zapPath + "xml\\report.html.xsl ZapReport.html"

    String create_XML = "New-Item -Path 'ZapReportXML.xml' -ItemType File -force;";
    String create_report  = "New-Item -Path 'ZapReport.html' -ItemType File -force;";
    String run_zap = " java -Xmx512m -jar \""+ zapPath + "zap-2.8.0.jar\" -cmd -quickurl \""+ url +"\" -quickout \"ZapReportXML.xml\" -quickprogress -config api.key=12345;";
    String xml_to_html = "$XSLFileName = 'report.html.xsl' ; $XSLFileInput = '" + zapPathOriginal + "xml\\' + $XSLFileName ; $XMLFileName = 'ZapReportXML.xml' ; $XMLInputFile = $XMLFileName ; $OutPutFileName = 'ZapReport.html' ; $XMLOutputFile = $OutPutFileName ; $XSLInputElement = New-Object System.Xml.Xsl.XslCompiledTransform; ; $XSLInputElement.Load($XSLFileInput) ; $XSLInputElement.Transform($XMLInputFile, $XMLOutputFile);";
    String pretty_html = "((Get-Content -path ZapReport.html -Raw) -replace '&lt;p&gt;','' -replace '&lt;/p&gt;','') | Set-Content -Path ZapReport.html;";
    String error = checkRisk(failOpt);

    return new SimpleProgramCommandLine(getRunnerContext(), "powershell.exe", Collections.singletonList( create_XML + create_report + run_zap + xml_to_html + pretty_html + error));
  }

  String getCustomScript(String scriptContent) throws RunBuildException {
    try {
      final File scriptFile = File.createTempFile("custom_script", ".sh", getAgentTempDirectory());
      FileUtil.writeFileAndReportErrors(scriptFile, scriptContent);
      myFilesToDelete.add(scriptFile);
      return scriptFile.getAbsolutePath();
    } catch (IOException e) {
      RunBuildException exception = new RunBuildException("Failed to create temporary custom script file in directory '" + getAgentTempDirectory() + "': " + e
          .toString(), e);
      exception.setLogStacktrace(false);
      throw exception;
    }
  }

  private void setExecutableAttribute(@NotNull final String script) throws RunBuildException {
    try {
      TCStreamUtil.setFileMode(new File(script), "a+x");
    } catch (Throwable t) {
      throw new RunBuildException("Failed to set executable attribute for custom script '" + script + "'", t);
    }
  }

  @Override
  public void afterProcessFinished() throws RunBuildException {
    super.afterProcessFinished();
    for (File file : myFilesToDelete) {
      FileUtil.delete(file);
    }
    myFilesToDelete.clear();
  }

  public String checkRisk(String risk) {

    String riskResult = "";

    switch(risk){
      case "Low":
        riskResult += "if((Get-Content ZapReport.html | Where-Object { $_.Contains('a name=\"\"low\"\"') } ).Count) {echo \"Found` a` low` risk` threat!\"; exit 1};";
      case "Medium":
        riskResult += "if((Get-Content ZapReport.html | Where-Object { $_.Contains('a name=\"\"medium\"\"') } ).Count) {echo \"Found` a` medium` risk` threat!\"; exit 1};";
      case "High":
        riskResult += "if((Get-Content ZapReport.html | Where-Object { $_.Contains('a name=\"\"high\"\"') } ).Count) {echo \"Found` a` high` risk` threat!\"; exit 1};";
        break;
    }
    return riskResult;
  }
}
