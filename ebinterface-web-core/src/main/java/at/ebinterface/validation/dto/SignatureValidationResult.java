package at.ebinterface.validation.dto;

import java.io.Serializable;
import java.math.BigInteger;

import at.ebinterface.validation.rtr.generated.SignatureInfoType;
import at.ebinterface.validation.rtr.generated.VerifyDocumentResponse;

/**
 * Used to represent the result of a signature validation based on the Web
 * Service response User: pl Date: 20.03.14 Time: 12:43
 */
public class SignatureValidationResult implements Serializable
{
  private boolean signatureValid;
  private boolean certificateValid;
  private boolean manifestValid;

  private String signatureText;
  private String certificateText;
  private String manifestText;

  /**
   * Construct a new signature validation result based on the results returned
   * by the RTR Web Service
   */
  public SignatureValidationResult (final VerifyDocumentResponse result)
  {
    SignatureInfoType signatureInfo = null;

    if (result != null &&
        result.getVerificationReport () != null &&
        result.getVerificationReport ().getSignatureInfo () != null &&
        result.getVerificationReport ().getSignatureInfo ().size () > 0)
      signatureInfo = result.getVerificationReport ().getSignatureInfo ().get (0);

    if (signatureInfo != null)
    {
      // Signature check details
      signatureValid = BigInteger.ZERO.equals (signatureInfo.getSignatureCheck ().getCode ());
      signatureText = signatureInfo.getSignatureCheck ().getInfo ();

      // Certificate check details
      certificateValid = BigInteger.ZERO.equals (signatureInfo.getCertificateCheck ().getCode ());
      certificateText = signatureInfo.getCertificateCheck ().getInfo ();

      // Manifest check
      manifestValid = BigInteger.ZERO.equals (signatureInfo.getManifestCheck ().getManifest ().getCode ());
      manifestText = signatureInfo.getManifestCheck ().getManifest ().getInfo ();
    }
  }

  public boolean isSignatureValid ()
  {
    return signatureValid;
  }

  public void setSignatureValid (final boolean signatureValid)
  {
    this.signatureValid = signatureValid;
  }

  public boolean isCertificateValid ()
  {
    return certificateValid;
  }

  public void setCertificateValid (final boolean certificateValid)
  {
    this.certificateValid = certificateValid;
  }

  public boolean isManifestValid ()
  {
    return manifestValid;
  }

  public void setManifestValid (final boolean manifestValid)
  {
    this.manifestValid = manifestValid;
  }

  public String getSignatureText ()
  {
    return signatureText;
  }

  public void setSignatureText (final String signatureText)
  {
    this.signatureText = signatureText;
  }

  public String getCertificateText ()
  {
    return certificateText;
  }

  public void setCertificateText (final String certificateText)
  {
    this.certificateText = certificateText;
  }

  public String getManifestText ()
  {
    return manifestText;
  }

  public void setManifestText (final String manifestText)
  {
    this.manifestText = manifestText;
  }
}
