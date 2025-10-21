package at.ebinterface.web2.pdf;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Currency;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.CGlobal;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.numeric.BigHelper;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.datetime.format.PDTToString;
import com.helger.masterdata.currency.ECurrency;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Abstract base class for layouting
 *
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractStyledElement
{
  protected static final boolean GROSS_SUMS_2_DIGITS = false;

  private static final int UNIT_AMOUNT_FRACTION = PDFHelper.AMOUNT2_FRACTION;
  private static final int TOTAL_AMOUNT_FRACTION = PDFHelper.AMOUNT4_FRACTION;
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractStyledElement.class);

  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  @GuardedBy ("RW_LOCK")
  private static final ICommonsMap <Locale, Currency> CURRENCY_FORMAT_PATTERN_CACHE = new CommonsHashMap <> ();

  private final Locale m_aContentLocale;
  private DecimalFormat m_aNFPercentage;
  private NumberFormat m_aNFInt;
  private NumberFormat m_aNFDecimal;
  private DecimalFormat m_aNFUnitAmount;
  private DecimalFormat m_aNFTotalAmount;

  @Nonnull
  private static Currency _getCurrency (@Nonnull final Locale aContentLocale)
  {
    RW_LOCK.readLock ().lock ();
    try
    {
      final Currency aCurrency = CURRENCY_FORMAT_PATTERN_CACHE.get (aContentLocale);
      if (aCurrency != null)
        return aCurrency;
    }
    finally
    {
      RW_LOCK.readLock ().unlock ();
    }

    return RW_LOCK.writeLockedGet ( () -> {
      // Try again in write lock
      Currency aCurrency = CURRENCY_FORMAT_PATTERN_CACHE.get (aContentLocale);
      if (aCurrency == null)
      {
        // Resolve currency
        try
        {
          aCurrency = Currency.getInstance (aContentLocale);
        }
        catch (final Exception ex)
        {}
        if (aCurrency == null)
        {
          // Can e.g. happen, when the passed locale does not have a country
          // set!
          LOGGER.error ("Failed to get currency of locale " + aContentLocale + " - falling back to EURO");

          // Use Euro in this case as a reasonable default
          aCurrency = ECurrency.EUR.getAsCurrency ();
        }
        CURRENCY_FORMAT_PATTERN_CACHE.put (aContentLocale, aCurrency);
      }
      return aCurrency;
    });
  }

  public AbstractStyledElement (@Nonnull final Locale aContentLocale)
  {
    m_aContentLocale = ValueEnforcer.notNull (aContentLocale, "ContentLocale");
  }

  @Nonnull
  public final Locale getLocale ()
  {
    return m_aContentLocale;
  }

  @Nonnull
  private DecimalFormat _createAmountValueDecimalFormat (@Nonnegative final int nMaxFractionDigits)
  {
    final Currency aCurrency = _getCurrency (m_aContentLocale);

    final DecimalFormat aCurrencyDF = (DecimalFormat) NumberFormat.getCurrencyInstance (m_aContentLocale);

    // Ensure that e.g. PLN Zloty are displayed with 2 fraction digits -
    // otherwise the would have a minimum of 0 digits.
    aCurrencyDF.setMinimumFractionDigits (aCurrency.getDefaultFractionDigits ());
    aCurrencyDF.setMaximumFractionDigits (nMaxFractionDigits);

    // Extract value pattern from currency pattern (without currency symbol)
    final String sValuePattern = StringReplace.replaceAll (aCurrencyDF.toPattern (), "\u00A4", "").trim ();

    // Use the decimal symbols from the currency format
    final DecimalFormat ret = new DecimalFormat (sValuePattern, aCurrencyDF.getDecimalFormatSymbols ());
    ret.setRoundingMode (PDFHelper.ROUNDING_MODE);
    return ret;
  }

  /**
   * Convert the passed BigDecimal with 4 decimals to a String. As all
   * calculations are done with a maximum of 4 decimal places, rounding should
   * never take place, but because the devil never sleeps, the rounding is still
   * contained in here.
   *
   * @param aValue
   *        The value to be printed. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public final String getFormattedUnitAmount (@Nonnull final BigDecimal aValue)
  {
    // Create lazily because it is time consuming
    if (m_aNFUnitAmount == null)
      m_aNFUnitAmount = _createAmountValueDecimalFormat (UNIT_AMOUNT_FRACTION);

    String ret = m_aNFUnitAmount.format (aValue);

    // Skip trailing 0s to avoid that a tilde is added to "3.1000"
    if (BigHelper.getWithoutTrailingZeroes (aValue).scale () > UNIT_AMOUNT_FRACTION)
      ret = "~" + ret;
    return ret;
  }

  /**
   * Convert the passed BigDecimal with 2 decimals to a String. In case the
   * value is rounded, a leading "~" is added.
   *
   * @param aValue
   *        The value to be printed. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public final String getFormattedTotalAmount (@Nonnull final BigDecimal aValue)
  {
    // Create lazily because it is time consuming
    if (m_aNFTotalAmount == null)
      m_aNFTotalAmount = _createAmountValueDecimalFormat (TOTAL_AMOUNT_FRACTION);

    String ret = m_aNFTotalAmount.format (aValue);

    // As this method is only used for the cover page, no rounding sign is
    // needed
    if (false)
    {
      // Skip trailing 0s to avoid that a tilde is added to "3.1000"
      if (BigHelper.getWithoutTrailingZeroes (aValue).scale () > TOTAL_AMOUNT_FRACTION)
        ret = "~" + ret;
    }

    return ret;
  }

  @Nonnull
  public final String getFormattedDate (@Nullable final LocalDate aDate)
  {
    return PDTToString.getAsString (aDate, m_aContentLocale);
  }

  @Nonnull
  public final String getFormattedTime (@Nullable final LocalTime aTime)
  {
    return PDTToString.getAsString (aTime, m_aContentLocale);
  }

  /**
   * Get the formatted percentage value
   *
   * @param aValue
   *        The value to be formatted (e.g. 50 for 50%)
   * @return The formatted value including the '%' sign!
   */
  @Nonnull
  public final String getFormattedPercentage (@Nonnull final BigDecimal aValue)
  {
    // Init lazily
    if (m_aNFPercentage == null)
    {
      m_aNFPercentage = (DecimalFormat) NumberFormat.getPercentInstance (m_aContentLocale);
      m_aNFPercentage.setMaximumFractionDigits (PDFHelper.PERCENTAGE_FRACTION);
      m_aNFPercentage.setRoundingMode (PDFHelper.ROUNDING_MODE);
    }

    // The percentage formatter automatically multiplies by 100!
    final String ret = m_aNFPercentage.format (aValue.divide (CGlobal.BIGDEC_100));
    return ret;
  }

  @Nonnull
  public final String getFormattedLong (final long n)
  {
    // Init lazily
    if (m_aNFInt == null)
      m_aNFInt = NumberFormat.getIntegerInstance (m_aContentLocale);

    return m_aNFInt.format (n);
  }

  @Nullable
  public final String getFormattedLong (@Nullable final Long aValue)
  {
    return aValue == null ? null : getFormattedLong (aValue.longValue ());
  }

  @Nonnull
  public final String getFormattedDecimal (@Nonnull final BigDecimal aValue)
  {
    // Init lazily
    if (m_aNFDecimal == null)
    {
      m_aNFDecimal = NumberFormat.getInstance (m_aContentLocale);
      m_aNFDecimal.setMinimumFractionDigits (0);
      // In case default maximum fractions would be lower than this (e.g. 3 for
      // German)
      m_aNFDecimal.setMaximumFractionDigits (PDFHelper.AMOUNT4_FRACTION);
      m_aNFDecimal.setRoundingMode (PDFHelper.ROUNDING_MODE);
    }

    return m_aNFDecimal.format (BigHelper.getWithoutTrailingZeroes (aValue));
  }
}
