package at.ebinterface.validation.rtr;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ebinterface.validation.rtr.generated.VerificationFault;
import at.ebinterface.validation.rtr.generated.VerificationService;
import at.ebinterface.validation.rtr.generated.VerificationServicePortType;
import at.ebinterface.validation.rtr.generated.VerifyDocumentRequest;
import at.ebinterface.validation.rtr.generated.VerifyDocumentResponse;
import jakarta.xml.ws.BindingProvider;

/**
 * Used to invoke the validation service - adds the HTTP basic parameters to the
 * service invocation Abstraction class is needed, since the generated classes
 * may be overwritten User: pl Date: 18.03.14 Time: 21:40
 */
public class VerificationServiceInvoker
{

  private static final Logger LOG = LoggerFactory.getLogger (VerificationServiceInvoker.class.getName ());

  private static String USERNAME;
  private static String PASSWORD;

  private static boolean activated = true;

  static
  {
    // Get username and password for the RTR Web Service, which requires HTTP
    // Basic Authentication
    // That may be done more nicely using Spring, but we abstain to use Spring
    // dependencies in this project...
    final Properties prop = new Properties ();
    try (final InputStream input = Thread.currentThread ()
                                         .getContextClassLoader ()
                                         .getResourceAsStream ("rtr.properties"))
    {
      prop.load (input);

      // Set the properties
      USERNAME = prop.getProperty ("rtr.username");
      PASSWORD = prop.getProperty ("rtr.password");

      // Set a password authenticator in order to allow communication through
      // the basic authentication
      Authenticator.setDefault (new Authenticator ()
      {
        @Override
        protected PasswordAuthentication getPasswordAuthentication ()
        {
          return new PasswordAuthentication (USERNAME, PASSWORD.toCharArray ());
        }
      });
    }
    catch (final Exception ex)
    {
      LOG.error ("Unable to read username/password for HTTP basic authentication of RTR service. Validation service Won't be able to perform signature validations.");
      activated = false;
    }

    // Enable some more verbose logging
    if (false)
    {
      System.setProperty ("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
      System.setProperty ("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
      System.setProperty ("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
      System.setProperty ("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
    }
  }

  /**
   * Used to invoke the RTR document validation service. Please note that you
   * must pass a valid username/password in order to access the validation
   * service. Credentials may be requested from RTR
   * <p/>
   * Credentials are retrieved from a rtr.properties file, which must contain
   * the following value
   * <p/>
   * rtr.username=xyz rtr.password=xyz
   *
   * @param request
   *        Web Service request
   */
  public static VerifyDocumentResponse verifyDocument (final VerifyDocumentRequest request) throws VerificationFault
  {
    final VerificationService verificationService = new VerificationService ();
    final VerificationServicePortType portType = verificationService.getVerificationServicePortSOAP ();

    // Add the basic authentication credentials
    final BindingProvider bp = (BindingProvider) portType;
    bp.getRequestContext ().put (BindingProvider.USERNAME_PROPERTY, USERNAME);
    bp.getRequestContext ().put (BindingProvider.PASSWORD_PROPERTY, PASSWORD);

    return portType.verifyDocument (request);

  }

  public static boolean isActivated ()
  {
    return activated;
  }
}
