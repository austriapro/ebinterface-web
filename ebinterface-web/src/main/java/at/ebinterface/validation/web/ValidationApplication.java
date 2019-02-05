package at.ebinterface.validation.web;

import com.google.common.base.Strings;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.web.pages.LabsPage;
import at.ebinterface.validation.web.pages.ServicePage;
import at.ebinterface.validation.web.pages.StartPage;


/**
 * The application class for the ebInterface validation application
 *
 * @author pl
 */
public class ValidationApplication extends WebApplication {


  /**
   * Defines wether the application shall be started in develop or deployment mode
   */
  private static RuntimeConfigurationType CONFIGURATION_TYPE = RuntimeConfigurationType.DEPLOYMENT;

  private static final Logger LOG = LoggerFactory.getLogger(ValidationApplication.class.getName());

  static {
    //Set the manual keystore, otherwise the RTR certificate is not trusted
    try {
      URL url = ValidationApplication.class.getResource("/keystore.jks");
      LOG.info("Setting key store reference to {}", url.getPath());
      System.setProperty("javax.net.ssl.trustStore", url.getPath());
      System.setProperty("javax.net.ssl.trustStorePassword", "");

    } catch (Exception e1) {
      throw new RuntimeException("Error while reading SSL Keystore. Unable to proceed.", e1);
    }

  }

  @Override
  protected void init() {
    super.init();

    try {
      LOG.info("Compiling JasperReport template for ebInterface");
      JasperDesign
          jrDesign =
          JRXmlLoader.load(
              this.getClass().getClassLoader().getResourceAsStream("reports/ebInterface.jrxml"));
      JasperReport jrReport = JasperCompileManager.compileReport(jrDesign);
      setMetaData(Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE, jrReport);
      LOG.info("JasperReport template for ebInterface is now stored in application context.");
    } catch (Exception ex) {
      LOG.error("Could not load ebInterface PDF template!");
    }

    try {
      LOG.info("Compiling JasperReport template for ZUGFeRD");
      JasperDesign
          jrDesign =
          JRXmlLoader
              .load(this.getClass().getClassLoader().getResourceAsStream("reports/ZUGFeRD.jrxml"));
      JasperReport jrReport = JasperCompileManager.compileReport(jrDesign);
      setMetaData(Constants.METADATAKEY_ZUGFERD_JRTEMPLATE, jrReport);
      LOG.info("JasperReport template for ZUGFeRD is now stored in application context.");
    } catch (Exception ex) {
      LOG.error("Could not load ZUGFeRD PDF template!");
    }

    LOG.info("Initializing XML schema validator for ebInterface");
    EbInterfaceValidator validator = new EbInterfaceValidator();
    setMetaData(Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR, validator);
    LOG.info("XML schema validator for ebInterface is now stored in application context.");

    LOG.info("Initializing XML schema validator for ZUGFeRD");
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = null;
    try {
      schema = factory.newSchema(new StreamSource(new File(
          ValidationApplication.class
              .getResource("/zugferd1p0/ZUGFeRD1p0.xsd").toURI())));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    setMetaData(Constants.METADATAKEY_ZUGFERD_XMLSCHEMA, schema);
    LOG.info("XML schema validator for ZUGFeRD is now stored in application context.");

    LOG.info("Initializing schematron transformer for ZUGFeRD");
    try {
      Source xsl = new StreamSource(new File(
          ValidationApplication.class
              .getResource("/zugferd1p0/ZUGFeRD_1p0-compiled.xsl").toURI()));
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Templates templates = transFactory.newTemplates(xsl);
      setMetaData(Constants.METADATAKEY_ZUGFERD_SCHEMATRONTEMPLATE, templates);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    LOG.info("schematron transformer for ZUGFeRD is now stored in application context.");

    getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
    getRequestCycleSettings().setResponseRequestEncoding("UTF-8");

    mountPage("/service", ServicePage.class);
    mountPage("/labs", LabsPage.class);

  }

  /**
   * Returns the configuration type (develop or deployment)
   */
  @Override
  public RuntimeConfigurationType getConfigurationType() {

    return CONFIGURATION_TYPE;

  }


  /**
   * Returns the home page
   */
  @Override
  public Class<? extends Page> getHomePage() {

    // https://gitlab.ecosio.com/misc/austriapro/issues/11
    Class<? extends Page> homePage = StartPage.class;

    try {

      String appPath = System.getenv("APPLICATION_PATH");

      if (Strings.isNullOrEmpty(appPath)) {
        LOG.debug("APPLICATION_PATH not set, returning default homepage");
        return homePage;
      }

      switch (appPath) {
        case "services":
          LOG.debug("Homepage is ServicePage");
          homePage = ServicePage.class;
          break;
        case "labs":
          LOG.debug("Homepage is LabsPage");
          homePage = LabsPage.class;
          break;
        default:
          LOG.info("Could not determine type of landing page, using startpage");
          homePage = StartPage.class;

      }

    } catch (Exception e) {
      LOG.warn(e.getMessage(), e);
    }

    return homePage;
  }

}
