package at.ebinterface.validation.validator;

import at.ebinterface.validation.parser.EbiVersion;
import at.ebinterface.validation.rtr.generated.VerifyDocumentResponse;
import at.ebinterface.validation.validator.jaxb.Result;

/**
 * DTO for the XML Schema validation result
 *
 * @author pl
 */
public final class ValidationResult {
  private String schemaValidationErrorMessage;
  private EbiVersion determinedEbInterfaceVersion;

  /**
   * Schematronvalidation result
   */
  private Result result;

  /**
   * Holds a potential signature validation exception, which is returned by the validation service
   * *
   */
  private String signatureValidationExceptionMessage;

  /**
   * Certificate specific results
   */
  private VerifyDocumentResponse verifyDocumentResponse;

  public ValidationResult (){}

  public String getSchemaValidationErrorMessage() {
    return schemaValidationErrorMessage;
  }

  public void setSchemaValidationErrorMessage(final String schemaValidationErrorMessage) {
    this.schemaValidationErrorMessage = schemaValidationErrorMessage;
  }

  public EbiVersion getDeterminedEbInterfaceVersion() {
    return determinedEbInterfaceVersion;
  }

  public void setDeterminedEbInterfaceVersion(
      final EbiVersion determinedEbInterfaceVersion) {
    this.determinedEbInterfaceVersion = determinedEbInterfaceVersion;
  }

  public Result getSchematronResult() {
    return result;
  }

  public void setSchematronResult(final Result result) {
    this.result = result;
  }

  public VerifyDocumentResponse getVerifyDocumentResponse() {
    return verifyDocumentResponse;
  }

  public void setVerifyDocumentResponse(final VerifyDocumentResponse verifyDocumentResponse) {
    this.verifyDocumentResponse = verifyDocumentResponse;
  }

  public String getSignatureValidationExceptionMessage() {
    return signatureValidationExceptionMessage;
  }

  public void setSignatureValidationExceptionMessage(final String signatureValidationExceptionMessage) {
    this.signatureValidationExceptionMessage = signatureValidationExceptionMessage;
  }
}
