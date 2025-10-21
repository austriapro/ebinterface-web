package test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.wicket.util.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.rtr.VerificationServiceInvoker;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;

/**
 * Test class for testing the schematron validator
 *
 * @author pl
 */
public class EbInterfaceValidatorTest
{

  private static final Logger LOG = LoggerFactory.getLogger (EbInterfaceValidatorTest.class.getName ());

  static
  {

    // Set the manual keystore, otherwise the RTR certificate is not trusted
    try
    {
      final URL url = EbInterfaceValidatorTest.class.getResource ("/keystore.jks");
      LOG.debug ("Setting key store reference to {}", url.getPath ());
      System.setProperty ("javax.net.ssl.trustStore", url.getPath ());
      System.setProperty ("javax.net.ssl.trustStorePassword", "");

    }
    catch (final Exception e1)
    {
      throw new RuntimeException ("Error while reading SSL Keystore. Unable to proceed.", e1);
    }

  }

  /**
   * Test the schema validator
   *
   * @throws IOException
   *         on error
   */
  @Test
  public void test4p3SchemaValidator () throws IOException
  {

    // Valid schema
    InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/4p3/ebInterface_4p3_sample.xml");
    byte [] uploadedData = null;

    assertNotNull (input);
    // Step 1 - validate against the schema
    uploadedData = IOUtils.toByteArray (input);
    EbInterfaceValidator validator = new EbInterfaceValidator ();
    ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    // Invalid schema
    input = this.getClass ().getResourceAsStream ("/ebinterface/4p3/ebInterface_4p3_sample_invalid.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

  }

  /**
   * Test the schema validator
   *
   * @throws IOException
   *         on error
   */
  @Test
  public void test4p2SchemaValidator () throws IOException
  {

    // Valid schema
    InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/4p2/ebInterface_4p2_sample.xml");
    byte [] uploadedData = null;

    assertNotNull (input);
    // Step 1 - validate against the schema
    uploadedData = IOUtils.toByteArray (input);
    EbInterfaceValidator validator = new EbInterfaceValidator ();
    ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    // Invalid schema
    input = this.getClass ().getResourceAsStream ("/ebinterface/4p2/ebInterface_4p2_sample_invalid.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

  }

  /**
   * Test the schema validator
   *
   * @throws IOException
   *         on error
   */
  @Test
  public void test4p1SchemaValidator () throws IOException
  {

    // Valid schema
    InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/4p1/ebInterface_4p1_sample.xml");
    byte [] uploadedData = null;

    assertNotNull (input);
    // Step 1 - validate against the schema
    uploadedData = IOUtils.toByteArray (input);
    EbInterfaceValidator validator = new EbInterfaceValidator ();
    ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    // Invalid schema
    input = this.getClass ().getResourceAsStream ("/ebinterface/4p1/ebInterface_4p1_sample_invalid.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

  }

  /**
   * Test the schema validator
   *
   * @throws IOException
   *         on error
   */
  @Test
  public void test4p0SchemaValidator () throws IOException
  {

    // Valid schema
    InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/4p0/testinstance-valid-schema.xml");
    byte [] uploadedData = null;

    assertNotNull (input);
    // Step 1 - validate against the schema
    uploadedData = IOUtils.toByteArray (input);
    EbInterfaceValidator validator = new EbInterfaceValidator ();
    ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/4p0/ebinterface4-test1.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    // Invalid schema
    input = this.getClass ().getResourceAsStream ("/ebinterface/4p0/testinstance-invalid-schema.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    // Invalid schema with non-qualified attributes
    input = this.getClass ().getResourceAsStream ("/ebinterface/4p0/ebinterface4-test1-noprefix.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

  }

  @Test
  public void test4p0SchemaValidatorWithSignedSamples () throws IOException
  {
    // Test a sample with is entirely incorrect (signature as ROOT element,
    // which is not allowed)
    InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/4p0/ebinterface4_signed_and_invalid.xml");
    byte [] uploadedData = IOUtils.toByteArray (input);
    EbInterfaceValidator validator = new EbInterfaceValidator ();
    ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    // Must be ebinterface 4p0
    assertEquals (EEbInterfaceVersion.V40, result.getDeterminedEbInterfaceVersion ().getVersion ());
    // Must be signed
    assertTrue (result.getDeterminedEbInterfaceVersion ().isSigned ());
    // Signature is invalid - thus no response
    assertNull (result.getVerifyDocumentResponse ());
    if (VerificationServiceInvoker.isActivated ())
    {
      // Signature validation exception message must be present
      assertTrue (StringHelper.isNotEmpty (result.getSignatureValidationExceptionMessage ()));
    }

    // Test a correctly signed sample
    // on the Web interface)

    input = this.getClass ().getResourceAsStream ("/ebinterface/4p0/ebinterface4_signed_and_valid.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);

    // Must be ebinterface 4p0
    assertEquals (EEbInterfaceVersion.V40, result.getDeterminedEbInterfaceVersion ().getVersion ());

    // Must be signed
    assertTrue (result.getDeterminedEbInterfaceVersion ().isSigned ());

    // Signature must be valid - i.e. there must be an answer from the signature
    // service
    // Enable this line in order to check if the signature validation correct
    // It is currently disabled since we do not push the RTR Web Service
    // credentials to GitHub
    // and, thus, our CI environment does not have them either
    // assertNotNull(result.getVerifyDocumentResponse());

  }

  /**
   * Test the schema validator
   *
   * @throws IOException
   *         on error
   */
  @Test
  public void test3p02SchemaValidator () throws IOException
  {

    // Valid schema
    InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/3p02/InvoiceExample1.xml");

    assertNotNull (input);

    // Step 1 - validate against the schema
    EbInterfaceValidator validator = new EbInterfaceValidator ();
    byte [] uploadedData = IOUtils.toByteArray (input);
    ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p02/InvoiceExample2.xml");
    validator = new EbInterfaceValidator ();
    uploadedData = IOUtils.toByteArray (input);
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p02/valid_and_signed.xml");
    validator = new EbInterfaceValidator ();
    uploadedData = IOUtils.toByteArray (input);
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    // Invalid schema
    input = this.getClass ().getResourceAsStream ("/ebinterface/3p02/InvoiceExample-invalid.xml");
    validator = new EbInterfaceValidator ();
    uploadedData = IOUtils.toByteArray (input);
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    System.out.println (result);

  }

  /**
   * Test the schema validator
   *
   * @throws IOException
   *         on error
   */
  @Test
  public void test3p0SchemaValidator () throws IOException
  {

    // Valid schema
    InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/InvoiceExample1.xml");

    assertNotNull (input);

    // Step 1 - validate against the schema
    byte [] uploadedData = IOUtils.toByteArray (input);
    EbInterfaceValidator validator = new EbInterfaceValidator ();
    ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/valid1.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/valid2.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/valid3.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/valid4.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/valid5.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/valid6.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertTrue (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    // Invalid schema
    input = this.getClass ().getResourceAsStream ("/ebinterface/3p0/InvoiceExample-invalid.xml");
    uploadedData = IOUtils.toByteArray (input);
    validator = new EbInterfaceValidator ();
    result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));

    System.out.println (result);

  }

  @Test
  public void testXXEExploit () throws IOException
  {

    final InputStream input = this.getClass ().getResourceAsStream ("/ebinterface/4p0/xxe-exploit.xml");
    Assert.assertNotNull (input);
    final byte [] uploadedData = IOUtils.toByteArray (input);
    final EbInterfaceValidator validator = new EbInterfaceValidator ();
    final ValidationResult result = validator.validateXMLInstanceAgainstSchema (uploadedData);
    assertFalse (StringHelper.isEmpty (result.getSchemaValidationErrorMessage ()));
  }
}
