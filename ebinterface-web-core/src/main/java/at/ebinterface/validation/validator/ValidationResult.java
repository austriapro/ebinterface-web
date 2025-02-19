package at.ebinterface.validation.validator;

import javax.annotation.Nullable;

import org.w3c.dom.Document;

import at.ebinterface.validation.parser.EbiVersion;
import at.ebinterface.validation.rtr.generated.VerifyDocumentResponse;

/**
 * DTO for the XML Schema validation result
 *
 * @author pl
 */
public final class ValidationResult
{
  private String schemaValidationErrorMessage;
  private Document doc;
  private EbiVersion determinedEbInterfaceVersion;

  /**
   * Holds a potential signature validation exception, which is returned by the
   * validation service *
   */
  private String signatureValidationExceptionMessage;

  /**
   * Certificate specific results
   */
  private VerifyDocumentResponse verifyDocumentResponse;

  public ValidationResult ()
  {}

  public String getSchemaValidationErrorMessage ()
  {
    return schemaValidationErrorMessage;
  }

  public void setSchemaValidationErrorMessage (final String schemaValidationErrorMessage)
  {
    this.schemaValidationErrorMessage = schemaValidationErrorMessage;
  }

  @Nullable
  public Document getParsedXMLDocument ()
  {
    return doc;
  }

  public void setParsedXMLDocument (@Nullable final Document doc)
  {
    this.doc = doc;
  }

  public EbiVersion getDeterminedEbInterfaceVersion ()
  {
    return determinedEbInterfaceVersion;
  }

  public void setDeterminedEbInterfaceVersion (final EbiVersion determinedEbInterfaceVersion)
  {
    this.determinedEbInterfaceVersion = determinedEbInterfaceVersion;
  }

  public VerifyDocumentResponse getVerifyDocumentResponse ()
  {
    return verifyDocumentResponse;
  }

  public void setVerifyDocumentResponse (final VerifyDocumentResponse verifyDocumentResponse)
  {
    this.verifyDocumentResponse = verifyDocumentResponse;
  }

  public String getSignatureValidationExceptionMessage ()
  {
    return signatureValidationExceptionMessage;
  }

  public void setSignatureValidationExceptionMessage (final String signatureValidationExceptionMessage)
  {
    this.signatureValidationExceptionMessage = signatureValidationExceptionMessage;
  }
}
