package at.ebinterface.web2.pdf;

import java.util.Locale;

import com.helger.annotation.misc.Translatable;
import com.helger.text.IMultilingualText;
import com.helger.text.display.IHasDisplayTextWithArgs;
import com.helger.text.resolve.DefaultTextResolver;
import com.helger.text.util.TextHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Generic PDF text
 *
 * @author Philip Helger
 */
@Translatable
public enum EPDFText implements IHasDisplayTextWithArgs
{
  DOCUMENT_KEYWORDS ("E-Rechnung {0} von\n\n{1}\n\nan\n\n{2}", "E-Invoice {0} from\n\n{1}\n\nto\n\n{2}"),
  DOCUMENT_SUBJECT ("E-Rechnung {0}", "E-Invoice {0}"),
  DOCUMENT_TITLE ("E-Rechnung", "e-Invoice"),
  VATIN ("UID", "VATIN"),
  DELIVERY_ID ("Lieferscheinnummer: {0}", "Delivery ID: {1}"),
  DELIVERED_AT ("Lieferung am: {0}", "Delivery on: {0}"),
  PERIOD_OF_SERVICE ("Leistungszeitraum: {0}-{1}", "Period of service: from {0} to {1}");

  private final IMultilingualText m_aTP;

  EPDFText (@Nonnull final String sDE, @Nonnull final String sEN)
  {
    m_aTP = TextHelper.create_DE_EN (sDE, sEN);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
  }
}
