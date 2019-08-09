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

    final String zapLocation = getRunnerParameters().get(EchoRunnerConstants.MESSAGE_KEY);

    final String scriptContent = "powershell.exe docker run -i owasp/zap2docker-stable zap-cli -v quick-scan -l Informational -s all --ajax-spider --self-contained --start-options '-config api.disablekey=true' https://www.piworks.net";
    // C:\Program Files\OWASP\Zed Attack Proxy\
    //final String script = getCustomScript(scriptContent);

    //setExecutableAttribute(script);
    return new SimpleProgramCommandLine(getRunnerContext(), "powershell.exe", Collections.singletonList(" ./zap.bat -cmd -quickurl \"http://www.blankwebsite.com/\" -quickout \"ZapReportXML.xml\" -quickprogress -config api.key=12345; C:\\Users\\furkan.yangil\\AppData\\Local\\Programs\\Python\\Python37\\python.exe xsl_to_html.py ZapReportXML.xml report.html.xsl ZapReport.html"));
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
  /*
  public void createReport() throws Exception {

    File stylesheet = new File( "report.html.xsl");
    File xmlfile  = new File("ZapReportXML.xml");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(xmlfile);
    StreamSource stylesource = new StreamSource(stylesheet);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer(stylesource);
    DOMSource source = new DOMSource(document);
    //The Html output is in ZapReport.html
    StreamResult result = new StreamResult("ZapReport.html");
    transformer.transform(source,result);
  }
  */
}
