package at.ebinterface.web2.pdf;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * Class for all PDF generation exceptions.
 *
 * @author Philip Helger
 */
public final class PDFGenerationException extends Exception
{
  private final EPDFGenerationError m_eErrorCode;

  private static String _buildMessage (@Nonnull final EPDFGenerationError eErrorCode, final String sMessage)
  {
    ValueEnforcer.notNull (eErrorCode, "ErrorCode");

    return "[error " + eErrorCode + ": " + sMessage + "]";
  }

  public PDFGenerationException (@Nonnull final EPDFGenerationError eErrorCode, final String sMessage)
  {
    super (_buildMessage (eErrorCode, sMessage));
    m_eErrorCode = eErrorCode;
  }

  public PDFGenerationException (@Nonnull final EPDFGenerationError eErrorCode, final Throwable aCause)
  {
    super (_buildMessage (eErrorCode, "Nested exception"), aCause);
    m_eErrorCode = eErrorCode;
  }

  @Nonnull
  public EPDFGenerationError getErrorCode ()
  {
    return m_eErrorCode;
  }
}
