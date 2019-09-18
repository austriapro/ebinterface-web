package at.ebinterface.validation.validator;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBResult;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.exception.NamespaceUnknownException;
import at.ebinterface.validation.parser.CustomParser;
import at.ebinterface.validation.parser.EbiVersion;
import at.ebinterface.validation.rtr.VerificationServiceInvoker;
import at.ebinterface.validation.rtr.generated.VerifyDocumentRequest;
import at.ebinterface.validation.rtr.generated.VerifyDocumentResponse;
import at.ebinterface.validation.validator.jaxb.Result;

/**
 * This class validates a given ebInterface XML instance against a schematron file (already
 * transformed to .xsl)
 *
 * @author pl
 */
public class EbInterfaceValidator {

  /**
   * Validators for checking XML instances against the ebinterface schemas
   */
  private static Validator ebInterface3p0Validator;

  private static Validator ebInterface3p02Validator;

  private static Validator ebInterface4p0Validator;

  private static Validator ebInterface4p1Validator;

  private static Validator ebInterface4p2Validator;

  private static Validator ebInterface4p3Validator;

  private static Validator ebInterface5p0Validator;

  /**
   * Transformer factory
   */
  private static TransformerFactory tFactory;

  /**
   * Interim transformer
   */
  private static Transformer interimTransformer;

  /**
   * Transformer for generating the final report from schematron
   */
  private static Transformer reportTransformer;

  /**
   * ebInterface XSLT transformers
   */
  private static Transformer ebInterface3p0Transformer;
  private static Transformer ebInterface3p02Transformer;
  private static Transformer ebInterface4p0Transformer;
  private static Transformer ebInterface4p1Transformer;
  private static Transformer ebInterface4p2Transformer;
  private static Transformer ebInterface4p3Transformer;
  private static Transformer ebInterface5p0Transformer;

  /**
   * JAXBContext for generating the result
   */
  private static JAXBContext jaxb;

  private static final Logger LOG = LoggerFactory.getLogger(EbInterfaceValidator.class.getName());

  /**
   * Initialize the validator
   */
  static {
    final SchemaFactory factory = SchemaFactory
        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    //Set a custom schema resolver to avoid loading of Schemas from the Internet
    factory.setResourceResolver(new LocalSchemaResolver(factory.getResourceResolver()));

    // Load a WXS schema, represented by a Schema instance.
    final Source ebInterface3p0schemaFile = new StreamSource(
        EbInterfaceValidator.class
            .getResourceAsStream("/ebinterface/ebInterface3p0.xsd")
    );
    final Source ebInterface3p02schemaFile = new StreamSource(
        EbInterfaceValidator.class
            .getResourceAsStream("/ebinterface/ebInterface3p02.xsd")
    );
    final Source ebInterface4p0schemaFile = new StreamSource(
        EbInterfaceValidator.class
            .getResourceAsStream("/ebinterface/ebInterface4p0.xsd")
    );
    final Source ebInterface4p1schemaFile = new StreamSource(
        EbInterfaceValidator.class
            .getResourceAsStream("/ebinterface/ebInterface4p1.xsd")
    );
    final Source ebInterface4p2schemaFile = new StreamSource(
        EbInterfaceValidator.class
            .getResourceAsStream("/ebinterface/ebInterface4p2.xsd")
    );

    final Source ebInterface4p3schemaFile = new StreamSource(
        EbInterfaceValidator.class
            .getResourceAsStream("/ebinterface/ebInterface4p3.xsd")
    );

    final Source ebInterface5p0schemaFile = new StreamSource(
        EbInterfaceValidator.class
            .getResourceAsStream("/ebinterface/ebInterface5p0.xsd")
    );

    try {
      final Schema schema3p0 = factory.newSchema(ebInterface3p0schemaFile);
      final Schema schema3p02 = factory.newSchema(ebInterface3p02schemaFile);
      final Schema schema4p0 = factory.newSchema(ebInterface4p0schemaFile);
      final Schema schema4p1 = factory.newSchema(ebInterface4p1schemaFile);
      final Schema schema4p2 = factory.newSchema(ebInterface4p2schemaFile);
      final Schema schema4p3 = factory.newSchema(ebInterface4p3schemaFile);
      final Schema schema5p0 = factory.newSchema(ebInterface5p0schemaFile);

      // Create a Validator object, which can be used to validate
      // an instance document.
      ebInterface3p0Validator = schema3p0.newValidator();
      ebInterface3p02Validator = schema3p02.newValidator();
      ebInterface4p0Validator = schema4p0.newValidator();
      ebInterface4p1Validator = schema4p1.newValidator();
      ebInterface4p2Validator = schema4p2.newValidator();
      ebInterface4p3Validator = schema4p3.newValidator();
      ebInterface5p0Validator = schema5p0.newValidator();

    } catch (final Exception e) {
      throw new RuntimeException(e);
    }

    // Get a transformer factory
    tFactory = TransformerFactory.newInstance();

    /*
     * Initialize the XSLT Transformer for generating the interim XSLTs
     * based on the implementation
     */
    final String schematronImplUrl = EbInterfaceValidator.class
        .getResource(
            "/schematron-resources/iso-schematron-xslt2/iso_svrl_for_xslt2.xsl")
        .toString();
    final String reportUrl = EbInterfaceValidator.class.getResource(
        "/schematron-resources/custom/report.xsl").toString();
    try {
      // Schematron transformers

      // Initialize the interim transformer
      interimTransformer = tFactory.newTransformer(new StreamSource(
          schematronImplUrl));
      // Initialize the final transformer
      reportTransformer = tFactory.newTransformer(new StreamSource(
          reportUrl));

      // Transformers for ebInterface visualization
      String styleSheetFile = EbInterfaceValidator.class.getResource(
          "/stylesheets/ebInterface-3.0.xslt").toString();
      ebInterface3p0Transformer = tFactory
          .newTransformer(new StreamSource(styleSheetFile));

      styleSheetFile = EbInterfaceValidator.class.getResource(
          "/stylesheets/ebInterface-3.0.2.xslt").toString();
      ebInterface3p02Transformer = tFactory
          .newTransformer(new StreamSource(styleSheetFile));

      styleSheetFile = EbInterfaceValidator.class.getResource(
          "/stylesheets/ebInterface-4.0.xslt").toString();
      ebInterface4p0Transformer = tFactory
          .newTransformer(new StreamSource(styleSheetFile));

      styleSheetFile = EbInterfaceValidator.class.getResource(
          "/stylesheets/ebInterface-4.1.xslt").toString();
      ebInterface4p1Transformer = tFactory
          .newTransformer(new StreamSource(styleSheetFile));

      styleSheetFile = EbInterfaceValidator.class.getResource(
          "/stylesheets/ebInterface-4.2.xslt").toString();
      ebInterface4p2Transformer = tFactory
          .newTransformer(new StreamSource(styleSheetFile));

      styleSheetFile = EbInterfaceValidator.class.getResource(
          "/stylesheets/ebInterface-4.3.xslt").toString();
      ebInterface4p3Transformer = tFactory
          .newTransformer(new StreamSource(styleSheetFile));

      styleSheetFile = EbInterfaceValidator.class.getResource(
          "/stylesheets/ebInterface-5.0.xslt").toString();
      ebInterface5p0Transformer = tFactory
          .newTransformer(new StreamSource(styleSheetFile));

    } catch (final TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }

    // JAXB context
    try {
      jaxb = JAXBContext.newInstance(Result.class);
    } catch (final JAXBException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * Validate the XML instance input stream
   *
   * @param uploadedData The XML data
   * @return The validation result. Never <code>null</code>.
   */
  public ValidationResult validateXMLInstanceAgainstSchema(final byte[] uploadedData) {

    final ValidationResult result = new ValidationResult();

    // Step 1 - determine the correct ebInterface version
    EbiVersion version;
    try {
      version = CustomParser.INSTANCE
          .getEbInterfaceDetails(new InputSource(
              new NonBlockingByteArrayInputStream(uploadedData)));
      result.setDeterminedEbInterfaceVersion(version);
    } catch (final NamespaceUnknownException e1) {
      result.setSchemaValidationErrorMessage(e1.getMessage());
      return result;
    }

    // Step 2 - invoke the correct parser for the determined ebInterface
    // version
    try {

      final javax.xml.transform.sax.SAXSource
          saxSource =
          new javax.xml.transform.sax.SAXSource(new InputSource(
              new NonBlockingByteArrayInputStream(uploadedData)));

      final EEbInterfaceVersion v = version.getVersion ();
      switch (v) {
        case V30:
          ebInterface3p0Validator.validate(saxSource);
          break;
        case V302:
          ebInterface3p02Validator.validate(saxSource);
          break;
        case V40:
          ebInterface4p0Validator.validate(saxSource);
          break;
        case V41:
          ebInterface4p1Validator.validate(saxSource);
          break;
        case V42:
          ebInterface4p2Validator.validate(saxSource);
          break;
        case V43:
          ebInterface4p3Validator.validate(saxSource);
          break;
        case V50:
          ebInterface5p0Validator.validate(saxSource);
          break;
        default:
          throw new IllegalStateException ("Unsupported version " + v);
      }
    } catch (final SAXException e) {
      result.setSchemaValidationErrorMessage(e.getMessage());
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    // Step 3 - in case the document is signed, check the signature as well

    if (version.isSigned()) {

      //Build the request for the verification Web Service
      //Create a verification request
      final VerifyDocumentRequest request = new VerifyDocumentRequest();

      //Set the document
      request.setDocument(uploadedData);
      //No PDF report required
      request.setRequestPDFReport(false);
      //Expect German results
      request.setLanguage("de");

      VerifyDocumentResponse response = null;
      try {
        response = VerificationServiceInvoker.verifyDocument(request);
        result.setVerifyDocumentResponse(response);
      } catch (final SOAPFaultException sfe) {
        result.setSignatureValidationExceptionMessage(sfe.getMessage());
      } catch (final Exception e) {
        result.setSignatureValidationExceptionMessage(e.getMessage());
      }

    }

    return result;
  }

  /**
   * Apply the correct stylesheet and transform the input
   *
   * @return the string representation
   */
  public String transformInput(final byte[] uploadedData, final EEbInterfaceVersion version) {

    try {
      final NonBlockingStringWriter sw = new NonBlockingStringWriter();

      final SAXSource
          saxSource =
          new SAXSource(
              at.ebinterface.validation.validator.SAXParserFactory.newInstance().getXMLReader(),
              (new InputSource(new NonBlockingByteArrayInputStream(uploadedData))));

      LOG.info("Transforming {}", version);
      switch (version) {
        case V30:
          ebInterface3p0Transformer.transform(new StreamSource(
                                                  new NonBlockingByteArrayInputStream(uploadedData)),
                                              new StreamResult(sw));
          break;
        case V302:
          ebInterface3p02Transformer.transform(new StreamSource(
                                                   new NonBlockingByteArrayInputStream(uploadedData)),
                                               new StreamResult(sw));
          break;
        case V40:
          ebInterface4p0Transformer.transform(saxSource,
                                              new StreamResult(sw));
          break;
        case V41:
          ebInterface4p1Transformer.transform(new StreamSource(
                                                  new NonBlockingByteArrayInputStream(uploadedData)),
                                              new StreamResult(sw));
          break;
        case V42:
          ebInterface4p2Transformer.transform(new StreamSource(
                                                  new NonBlockingByteArrayInputStream(uploadedData)),
                                              new StreamResult(sw));
          break;
        case V43:
          ebInterface4p3Transformer.transform(new StreamSource(
                                                  new NonBlockingByteArrayInputStream(uploadedData)),
                                              new StreamResult(sw));
          break;
        case V50:
          ebInterface5p0Transformer.transform(new StreamSource(
                                                  new NonBlockingByteArrayInputStream(uploadedData)),
                                              new StreamResult(sw));
          break;
        default:
          throw new IllegalStateException ("Unsupported ebInterface version " + version);
      }
      return sw.getAsString();

    } catch (final Exception e) {
      return "XSLT Transformation konnte nicht ausgeführt werden. Fehler: "
             + e.getMessage();
    }

  }

  /**
   * Validate the given XML instance against the given schematron file
   * @param uploadedData XML to be validated
   * @param schematronFileReference Schematron file URL
   * @return The non-<code>null</code> validation result
   */
  public Result validateXMLInstanceAgainstSchematron(final byte[] uploadedData,
                                                     final String schematronFileReference) {

    try {
      final Transformer transformer = getTransformer(schematronFileReference);

      // create a new string writer to hold the output of the validation
      // transformation
      final NonBlockingStringWriter sw = new NonBlockingStringWriter();

      // apply the validating XSLT to the ebinterface document
      transformer.transform(new StreamSource(new NonBlockingByteArrayInputStream(
          uploadedData)), new StreamResult(sw));

      final JAXBResult jaxbResult = new JAXBResult(jaxb);

      // apply the final transformation
      reportTransformer.transform(
          new StreamSource(new NonBlockingStringReader(sw.getAsString())),
          jaxbResult);

      return (Result) jaxbResult.getResult();

    } catch (final TransformerException | JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get the right schematron transformer using the url path
   */
  private Transformer getTransformer(final String urlPath)
      throws TransformerException {

    final NonBlockingStringWriter sw = new NonBlockingStringWriter();

        /* Read the Schematron source */
    final String schematronDocumentUrl = this.getClass()
        .getResource(urlPath).toString();

    interimTransformer.transform(new StreamSource(schematronDocumentUrl),
                                 new StreamResult(sw));

    return tFactory.newTransformer(new StreamSource(new NonBlockingStringReader(sw
                                                                         .getAsString())));
  }

}
