package test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.wicket.util.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.Rule;
import at.ebinterface.validation.validator.Rules;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.validator.jaxb.Result;

/**
 * Test class for testing the schematron validator
 *
 * @author pl
 */
public class EbInterfaceValidatorTest {


    private static final Logger LOG = LoggerFactory.getLogger(EbInterfaceValidatorTest.class.getName());


    static {


        //Set the manual keystore, otherwise the RTR certificate is not trusted
        try {
            final URL url = EbInterfaceValidatorTest.class.getResource("/keystore.jks");
            LOG.debug("Setting key store reference to {}", url.getPath());
            System.setProperty("javax.net.ssl.trustStore", url.getPath());
            System.setProperty("javax.net.ssl.trustStorePassword", "");

        } catch (final Exception e1) {
            throw new RuntimeException("Error while reading SSL Keystore. Unable to proceed.", e1);
        }


    }

    /**
     * Test the schema validator
     *
     * @throws IOException on error
     */
    @Test
    public void test4p3SchemaValidator() throws IOException {

        // Valid schema
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/4p3/ebInterface_4p3_sample.xml");
        byte[] uploadedData = null;

        assertNotNull(input);
        // Step 1 - validate against the schema
        uploadedData = IOUtils.toByteArray(input);
        EbInterfaceValidator validator = new EbInterfaceValidator();
        ValidationResult result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));


        // Invalid schema
        input = this.getClass().getResourceAsStream("/ebinterface/4p3/ebInterface_4p3_sample_invalid.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));


    }


    /**
     * Test the schema validator
     *
     * @throws IOException on error
     */
    @Test
    public void test4p2SchemaValidator() throws IOException {

        // Valid schema
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/4p2/ebInterface_4p2_sample.xml");
        byte[] uploadedData = null;

        assertNotNull(input);
        // Step 1 - validate against the schema
        uploadedData = IOUtils.toByteArray(input);
        EbInterfaceValidator validator = new EbInterfaceValidator();
        ValidationResult result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));


        // Invalid schema
        input = this.getClass().getResourceAsStream("/ebinterface/4p2/ebInterface_4p2_sample_invalid.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));


    }

    /**
     * Test the schema validator
     *
     * @throws IOException on error
     */
    @Test
    public void test4p1SchemaValidator() throws IOException {

        // Valid schema
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/4p1/ebInterface_4p1_sample.xml");
        byte[] uploadedData = null;

        assertNotNull(input);
        // Step 1 - validate against the schema
        uploadedData = IOUtils.toByteArray(input);
        EbInterfaceValidator validator = new EbInterfaceValidator();
        ValidationResult result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));


        // Invalid schema
        input = this.getClass().getResourceAsStream("/ebinterface/4p1/ebInterface_4p1_sample_invalid.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));


    }


    /**
     * Test the schema validator
     *
     * @throws IOException on error
     */
    @Test
    public void test4p0SchemaValidator() throws IOException {

        // Valid schema
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/4p0/testinstance-valid-schema.xml");
        byte[] uploadedData = null;

        assertNotNull(input);
        // Step 1 - validate against the schema
        uploadedData = IOUtils.toByteArray(input);
        EbInterfaceValidator validator = new EbInterfaceValidator();
        ValidationResult result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/4p0/ebinterface4-test1.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        // Invalid schema
        input = this.getClass().getResourceAsStream("/ebinterface/4p0/testinstance-invalid-schema.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        //Invalid schema with non-qualified attributes
        input = this.getClass().getResourceAsStream("/ebinterface/4p0/ebinterface4-test1-noprefix.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));



    }



    @Test
    public void test4p0SchemaValidatorWithSignedSamples() throws IOException {

        InputStream input;
        byte[]  uploadedData;
        EbInterfaceValidator validator;
        ValidationResult result;


        //Test a sample with is entirely incorrect (signature as ROOT element, which is not allowed)
        input = this.getClass().getResourceAsStream("/ebinterface/4p0/ebinterface4_signed_and_invalid.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        //Must be ebinterface 4p0
        assertEquals(EEbInterfaceVersion.V40, result.getDeterminedEbInterfaceVersion().getVersion ());
        //Must be signed
        assertTrue(result.getDeterminedEbInterfaceVersion().isSigned());
        //Signature is invalid - thus no response
        assertNull(result.getVerifyDocumentResponse());
        //Signature validation exception message must be present
        assertFalse(StringHelper.hasNoText(result.getSignatureValidationExceptionMessage()));

        //Test a correctly signed sample
        //on the Web interface)

        input = this.getClass().getResourceAsStream("/ebinterface/4p0/ebinterface4_signed_and_valid.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);

        //Must be ebinterface 4p0
        assertEquals(EEbInterfaceVersion.V40, result.getDeterminedEbInterfaceVersion().getVersion ());

        //Must be signed
        assertTrue(result.getDeterminedEbInterfaceVersion().isSigned());

        //Signature must be valid - i.e. there must be an answer from the signature service
        //Enable this line in order to check if the signature validation correct
        //It is currently disabled since we do not push the RTR Web Service credentials to GitHub
        //and, thus, our CI environment does not have them either
        //assertNotNull(result.getVerifyDocumentResponse());



    }


    /**
     * Test the schema validator
     *
     * @throws IOException on error
     */
    @Test
    public void test3p02SchemaValidator() throws IOException {

        // Valid schema
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/3p02/InvoiceExample1.xml");

        assertNotNull(input);

        // Step 1 - validate against the schema
        EbInterfaceValidator validator = new EbInterfaceValidator();
        byte[] uploadedData = IOUtils.toByteArray(input);
        ValidationResult result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p02/InvoiceExample2.xml");
        validator = new EbInterfaceValidator();
        uploadedData = IOUtils.toByteArray(input);
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p02/valid_and_signed.xml");
        validator = new EbInterfaceValidator();
        uploadedData = IOUtils.toByteArray(input);
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        // Invalid schema
        input = this.getClass().getResourceAsStream("/ebinterface/3p02/InvoiceExample-invalid.xml");
        validator = new EbInterfaceValidator();
        uploadedData = IOUtils.toByteArray(input);
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        System.out.println(result);

    }


    /**
     * Test the schema validator
     *
     * @throws IOException on error
     */
    @Test
    public void test3p0SchemaValidator() throws IOException {

        // Valid schema
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/3p0/InvoiceExample1.xml");

        assertNotNull(input);

        // Step 1 - validate against the schema
        byte[] uploadedData = IOUtils.toByteArray(input);
        EbInterfaceValidator validator = new EbInterfaceValidator();
        ValidationResult result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p0/valid1.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p0/valid2.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p0/valid3.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p0/valid4.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p0/valid5.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        input = this.getClass().getResourceAsStream("/ebinterface/3p0/valid6.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertTrue(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        // Invalid schema
        input = this.getClass().getResourceAsStream("/ebinterface/3p0/InvoiceExample-invalid.xml");
        uploadedData = IOUtils.toByteArray(input);
        validator = new EbInterfaceValidator();
        result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));

        System.out.println(result);

    }

    /**
     * Test the SVNR numbers
     *
     * @throws IOException on error
     */
    @Test
    @Ignore ("Old ebInterface 4.0")
    public void testSchematronValidatorSVNR() throws IOException {

        // CASE 1
        // Test a file with no SVNR - no rule shall be fired
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/4p0/testinstance-no-svnr.xml");
        byte[] uploadedData = IOUtils.toByteArray(input);
        final Rule rule = Rules.getRule("Sozialversicherung (ebInterface 4p0)", EEbInterfaceVersion.V40);
        assertNotNull(rule);
        final String schematronFile = rule.getFileReference();
        assertNotNull(input);

        final EbInterfaceValidator validator = new EbInterfaceValidator();
        Result result = validator.validateXMLInstanceAgainstSchematron(uploadedData, schematronFile);
        List<Result.Error> errors = result.getErrors();

        assertTrue(errors.size() == 0);

        // CASE 2
        // Test a file with two valid SVNR
        input = this.getClass().getResourceAsStream("/ebinterface/4p0/testinstance-valid-svnr.xml");
        uploadedData = IOUtils.toByteArray(input);
        assertNotNull(input);

        result = validator.validateXMLInstanceAgainstSchematron(uploadedData, schematronFile);
        errors = result.getErrors();

        assertTrue(errors.size() == 0);

        // CASE 3
        // Test a file with one invalid SVNR
        input = this.getClass().getResourceAsStream("/ebinterface/4p0/testinstance-invalid-svnr.xml");
        uploadedData = IOUtils.toByteArray(input);
        assertNotNull(input);

        result = validator.validateXMLInstanceAgainstSchematron(uploadedData, schematronFile);
        errors = result.getErrors();

        assertTrue(errors.size() > 0);

    }

    /**
     * Test the schematron validator
     *
     * @throws IOException on error
     */
    @Test
    @Ignore ("Old ebInterface 4.0")
    public void testSchematronValidatorSVBillersContractPartnerNumber() throws IOException {

        // CASE 1
        // Test a file where the biller contract partner number is not present
        InputStream input = this.getClass()
                .getResourceAsStream("/ebinterface/4p0/testinstance-no-billerscontractpartnernumberpresent.xml");
        byte[] uploadedData = IOUtils.toByteArray(input);
        final Rule rule = Rules.getRule("Sozialversicherung (ebInterface 4p0)", EEbInterfaceVersion.V40);
        assertNotNull(rule);
        final String schematronFile = rule.getFileReference();
        assertNotNull(input);

        // No rule must fire in this case and no error must be produced
        final EbInterfaceValidator validator = new EbInterfaceValidator();
        Result result = validator.validateXMLInstanceAgainstSchematron(uploadedData, schematronFile);
        List<Result.Error> errors = result.getErrors();

        assertTrue(errors.size() == 0);

        // CASE 2
        // Test a file with a valid biller contract partner number
        input = this.getClass()
                .getResourceAsStream("/ebinterface/4p0/testinstance-valid-billerscontractpartnernumber.xml");
        uploadedData = IOUtils.toByteArray(input);
        assertNotNull(input);

        // Rule must fire but no error must be produced
        result = validator.validateXMLInstanceAgainstSchematron(uploadedData, schematronFile);
        errors = result.getErrors();

        assertTrue(errors.size() == 0);

        // CASE 3
        // Test a file with a invalid biller contract partner number
        input = this.getClass()
                .getResourceAsStream("/ebinterface/4p0/testinstance-invalid-billerscontractpartnernumber.xml");
        uploadedData = IOUtils.toByteArray(input);
        assertNotNull(input);

        // Rule must fire and error must be produced
        result = validator.validateXMLInstanceAgainstSchematron(uploadedData, schematronFile);
        errors = result.getErrors();
        assertTrue(errors.size() > 0);
        printErrors(errors);

    }



    @Test
    public void testXXEExploit() throws IOException {

        final InputStream input = this.getClass().getResourceAsStream("/ebinterface/4p0/xxe-exploit.xml");
        Assert.assertNotNull(input);
        final byte [] uploadedData = IOUtils.toByteArray(input);
        final EbInterfaceValidator validator = new EbInterfaceValidator();
        final ValidationResult result = validator.validateXMLInstanceAgainstSchema(uploadedData);
        assertFalse(StringHelper.hasNoText(result.getSchemaValidationErrorMessage()));
    }

    /**
     * Print the errors
     *
     * @param errors
     */
    private void printErrors(final List<Result.Error> errors) {
        for (final Result.Error error : errors) {
            out("Violating element: " + error.getViolatingElement() + " Error message: " + error.getErrorMessage());
        }
    }

    private void out(final String s) {
        System.out.println(s);
    }

}
