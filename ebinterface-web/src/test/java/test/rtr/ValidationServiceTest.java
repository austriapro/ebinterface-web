package test.rtr;


import static org.junit.Assert.fail;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.stream.StreamHelper;

import at.ebinterface.validation.dto.SignatureValidationResult;
import at.ebinterface.validation.rtr.VerificationServiceInvoker;
import at.ebinterface.validation.rtr.generated.VerificationFault;
import at.ebinterface.validation.rtr.generated.VerifyDocumentRequest;
import at.ebinterface.validation.rtr.generated.VerifyDocumentResponse;

public class ValidationServiceTest {


    private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceTest.class.getName());


    static {
        //Set the manual keystore, otherwise the RTR certificate is not trusted
        try {
            final URL url = ValidationServiceTest.class.getResource("/keystore.jks");
            System.setProperty("javax.net.ssl.trustStore", url.getPath());
            System.setProperty("javax.net.ssl.trustStorePassword", "");

        } catch (final Exception e1) {
            throw new RuntimeException("Error while reading SSL Keystore. Unable to proceed.", e1);
        }

    }

    @Test
    public void testValidationService() throws Exception {

        //Get an invoice instance
        String invoice = StreamHelper.getAllBytesAsString (getClass().getResourceAsStream("/ebinterface/3p02/valid_and_signed.xml"), StandardCharsets.UTF_8);

        //Create a verification request
        VerifyDocumentRequest request = new VerifyDocumentRequest();

        //Set the document
        request.setDocument(invoice.getBytes());
        //No PDF report required
        request.setRequestPDFReport(Boolean.FALSE);
        //Expect German results
        request.setLanguage("de");

        //Get a response
        try {

            //We can only test this, if the verification service is available
            if (!VerificationServiceInvoker.isActivated()) {
                LOG.warn("Unable to test verification service, since it is not activated.");
                return;
            }


            final VerifyDocumentResponse response = VerificationServiceInvoker.verifyDocument(request);

            final SignatureValidationResult result = new SignatureValidationResult(response);
            Assert.assertTrue(result.isCertificateValid());
            Assert.assertTrue(result.isSignatureValid());
            Assert.assertTrue(result.isManifestValid());


        } catch (final VerificationFault verificationFault) {
            LOG.error("Fehler bei der Verarbeitung. Error code {}, Error message {}", verificationFault.getFaultInfo().getErrorCode(), verificationFault.getFaultInfo().getInfo());
        } catch (final Exception e) {
            LOG.error("Unable to get validation result. ", e);
            fail();
        }


        //Get an invalid invoice
        invoice = StreamHelper.getAllBytesAsString(this.getClass().getResourceAsStream("/ebinterface/3p02/mesonic1-corrupted.xml"), StandardCharsets.UTF_8);
        request = new VerifyDocumentRequest();
        //Set the document
        request.setDocument(invoice.getBytes());
        //No PDF report required
        request.setRequestPDFReport(Boolean.FALSE);
        //Expect German results
        request.setLanguage("de");

        //Get a response
        try {
            VerificationServiceInvoker.verifyDocument(request);
            fail();

        } catch (final VerificationFault verificationFault) {
            LOG.error("Fehler bei der Verarbeitung. Error code {}, Error message {}", verificationFault.getFaultInfo().getErrorCode(), verificationFault.getFaultInfo().getInfo());
        } catch (final Exception e) {
            LOG.error("Unable to get validation result. ", e);
        }


    }


}