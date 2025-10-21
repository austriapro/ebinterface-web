package at.ebinterface.validation.validator;

import java.io.IOException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.ebinterface.CEbInterface;
import com.helger.ebinterface.EEbInterfaceVersion;
import com.helger.ebinterface.visualization.VisualizationManager;
import com.helger.io.resource.ClassPathResource;
import com.helger.xml.sax.InputSourceFactory;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.transform.TransformResultFactory;
import com.helger.xml.transform.TransformSourceFactory;

import at.ebinterface.validation.exception.NamespaceUnknownException;
import at.ebinterface.validation.parser.CustomParser;
import at.ebinterface.validation.parser.EbiVersion;
import at.ebinterface.validation.rtr.VerificationServiceInvoker;
import at.ebinterface.validation.rtr.generated.VerifyDocumentRequest;
import at.ebinterface.validation.rtr.generated.VerifyDocumentResponse;
import jakarta.annotation.Nonnull;

/**
 * This class validates a given ebInterface XML instance against a schematron
 * file (already transformed to .xsl)
 *
 * @author pl
 */
public class EbInterfaceValidator
{
  private static final Logger LOGGER = LoggerFactory.getLogger (EbInterfaceValidator.class);

  /**
   * Validators for checking XML instances against the ebInterface schemas
   */
  private static final Validator VALIDATOR_30;
  private static final Validator VALIDATOR_302;
  private static final Validator VALIDATOR_40;
  private static final Validator VALIDATOR_41;
  private static final Validator VALIDATOR_42;
  private static final Validator VALIDATOR_43;
  private static final Validator VALIDATOR_50;
  private static final Validator VALIDATOR_60;
  private static final Validator VALIDATOR_61;

  @Nonnull
  private static Source [] _map (@Nonnull final List <ClassPathResource> xsds)
  {
    final int nLen = xsds.size ();
    final Source [] ret = new Source [nLen];
    for (int i = 0; i < nLen; ++i)
      ret[i] = TransformSourceFactory.create (xsds.get (i));
    return ret;
  }

  /**
   * Initialize the validator
   */
  static
  {
    LOGGER.info ("Start initializing ebInterface XML Schemas");

    final SchemaFactory factory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);

    try
    {
      final Schema schema3p0 = factory.newSchema (_map (CEbInterface.EBINTERFACE_30_XSDS));
      final Schema schema3p02 = factory.newSchema (_map (CEbInterface.EBINTERFACE_302_XSDS));
      final Schema schema4p0 = factory.newSchema (_map (CEbInterface.EBINTERFACE_40_XSDS));
      final Schema schema4p1 = factory.newSchema (_map (CEbInterface.EBINTERFACE_41_XSDS));
      final Schema schema4p2 = factory.newSchema (_map (CEbInterface.EBINTERFACE_42_XSDS));
      final Schema schema4p3 = factory.newSchema (_map (CEbInterface.EBINTERFACE_43_XSDS));
      final Schema schema5p0 = factory.newSchema (_map (CEbInterface.EBINTERFACE_50_XSDS));
      final Schema schema6p0 = factory.newSchema (_map (CEbInterface.EBINTERFACE_60_XSDS));
      final Schema schema6p1 = factory.newSchema (_map (CEbInterface.EBINTERFACE_61_XSDS));

      // Create a Validator object, which can be used to validate
      // an instance document.
      VALIDATOR_30 = schema3p0.newValidator ();
      VALIDATOR_302 = schema3p02.newValidator ();
      VALIDATOR_40 = schema4p0.newValidator ();
      VALIDATOR_41 = schema4p1.newValidator ();
      VALIDATOR_42 = schema4p2.newValidator ();
      VALIDATOR_43 = schema4p3.newValidator ();
      VALIDATOR_50 = schema5p0.newValidator ();
      VALIDATOR_60 = schema6p0.newValidator ();
      VALIDATOR_61 = schema6p1.newValidator ();
    }
    catch (final Exception e)
    {
      throw new RuntimeException (e);
    }

    LOGGER.info ("Finished initializing ebInterface stuff");
  }

  /**
   * Validate the XML instance input stream
   *
   * @param uploadedData
   *        The XML data
   * @return The validation result. Never <code>null</code>.
   */
  @Nonnull
  public ValidationResult validateXMLInstanceAgainstSchema (final byte [] uploadedData)
  {
    LOGGER.info ("Performing XML validation");
    final ValidationResult result = new ValidationResult ();

    // Step 0 - read XML
    final Document aDoc = DOMReader.readXMLDOM (uploadedData);
    if (aDoc == null)
    {
      result.setSchemaValidationErrorMessage ("Die hochgeladene Datei konnte nicht als XML interpretiert werden.");
      return result;
    }

    // Remember parsed document
    result.setParsedXMLDocument (aDoc);

    // Step 1 - determine the correct ebInterface version
    final EbiVersion version;
    try
    {
      version = CustomParser.INSTANCE.getEbInterfaceDetails (aDoc);
      result.setDeterminedEbInterfaceVersion (version);
    }
    catch (final NamespaceUnknownException ex)
    {
      result.setSchemaValidationErrorMessage (ex.getMessage ());
      return result;
    }

    LOGGER.info ("XML determined as " + version.getCaption ());

    // Step 2 - invoke the correct parser for the determined ebInterface
    // version
    try
    {
      final SAXSource saxSource = new SAXSource (InputSourceFactory.create (uploadedData));

      final EEbInterfaceVersion v = version.getVersion ();
      switch (v)
      {
        case V30:
          VALIDATOR_30.validate (saxSource);
          break;
        case V302:
          VALIDATOR_302.validate (saxSource);
          break;
        case V40:
          VALIDATOR_40.validate (saxSource);
          break;
        case V41:
          VALIDATOR_41.validate (saxSource);
          break;
        case V42:
          VALIDATOR_42.validate (saxSource);
          break;
        case V43:
          VALIDATOR_43.validate (saxSource);
          break;
        case V50:
          VALIDATOR_50.validate (saxSource);
          break;
        case V60:
          VALIDATOR_60.validate (saxSource);
          break;
        case V61:
          VALIDATOR_61.validate (saxSource);
          break;
        default:
          throw new IllegalStateException ("Unsupported version " + v);
      }
    }
    catch (final SAXException e)
    {
      result.setSchemaValidationErrorMessage (e.getMessage ());
    }
    catch (final IOException e)
    {
      throw new RuntimeException (e);
    }

    // Step 3 - in case the document is signed, check the signature as well
    if (version.isSigned ())
    {
      if (VerificationServiceInvoker.isActivated ())
      {
        LOGGER.info ("XML document is signed and the VerificationService is active");

        // Build the request for the verification Web Service
        // Create a verification request
        final VerifyDocumentRequest request = new VerifyDocumentRequest ();

        // Set the document
        request.setDocument (uploadedData);
        // No PDF report required
        request.setRequestPDFReport (Boolean.FALSE);
        // Expect German results
        request.setLanguage ("de");

        try
        {
          final VerifyDocumentResponse response = VerificationServiceInvoker.verifyDocument (request);
          result.setVerifyDocumentResponse (response);
        }
        catch (final Exception e)
        {
          LOGGER.warn ("Error in signature validation: " + e.getMessage ());
          result.setSignatureValidationExceptionMessage (e.getMessage ());
        }
      }
      else
      {
        LOGGER.info ("XML document is signed but the VerificationService is disabled");
      }
    }

    return result;
  }

  /**
   * Apply the correct stylesheet and transform the input
   *
   * @return the string representation
   */
  public String transformInput (final byte [] uploadedData, final EEbInterfaceVersion version)
  {
    LOGGER.info ("Creating HTML output for ebInterface " + version.getVersion ());

    try
    {
      final Document aDoc = DOMReader.readXMLDOM (uploadedData);
      if (aDoc == null)
        return "XSLT Transformation konnte nicht ausgeführt werden. Fehler: die hochgeladene Datei konnte nicht als XML interpretiert werden.";

      try (final NonBlockingStringWriter sw = new NonBlockingStringWriter ())
      {
        VisualizationManager.visualize (version, new DOMSource (aDoc), TransformResultFactory.create (sw));
        return sw.getAsString ();
      }
    }
    catch (final Exception e)
    {
      return "XSLT Transformation konnte nicht ausgeführt werden. Fehler: " + e.getMessage ();
    }
  }
}
