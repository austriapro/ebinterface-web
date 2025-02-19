package at.ebinterface.web2.pdf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.ValueEnforcer;
import com.helger.ebinterface.EbInterface40Marshaller;
import com.helger.ebinterface.EbInterface41Marshaller;
import com.helger.ebinterface.EbInterface42Marshaller;
import com.helger.ebinterface.EbInterface43Marshaller;
import com.helger.ebinterface.EbInterface50Marshaller;
import com.helger.ebinterface.EbInterface60Marshaller;
import com.helger.ebinterface.EbInterface61Marshaller;
import com.helger.ebinterface.v40.Ebi40InvoiceType;
import com.helger.ebinterface.v41.Ebi41InvoiceType;
import com.helger.ebinterface.v42.Ebi42InvoiceType;
import com.helger.ebinterface.v43.Ebi43InvoiceType;
import com.helger.ebinterface.v50.Ebi50InvoiceType;
import com.helger.ebinterface.v60.Ebi60InvoiceType;
import com.helger.ebinterface.v61.Ebi61InvoiceType;

import at.austriapro.ebinterface.ubl.to.EbInterface40ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface41ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface42ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface43ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface50ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface60ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface61ToInvoiceConverter;
import at.ebinterface.validation.parser.EbiVersion;
import at.ebinterface.web2.app.CApp;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

public final class PDFHelper
{
  /** Maximum fraction digits for percentage values */
  public static final int PERCENTAGE_FRACTION = 2;
  public static final int AMOUNT2_FRACTION = 2;
  public static final int AMOUNT4_FRACTION = 4;
  public static final int AMOUNT_MAX_FRACTION = 12;

  /** For Austria HALF_UP is correct */
  public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  private static final Logger LOGGER = LoggerFactory.getLogger (PDFHelper.class);

  private PDFHelper ()
  {}

  /**
   * @param aBD
   *        Source number. May not be <code>null</code>.
   * @return The number rounded to the number of decimals for percentage values
   *         (currently 2)
   */
  @Nonnull
  public static BigDecimal alignPerc (@Nonnull final BigDecimal aBD)
  {
    ValueEnforcer.notNull (aBD, "BigDecimal");
    return aBD.setScale (PERCENTAGE_FRACTION, ROUNDING_MODE);
  }

  /**
   * @param aBD
   *        Source number. May not be <code>null</code>.
   * @return The number rounded to the number of decimals for total amounts
   *         (currently 2)
   */
  @Nonnull
  public static BigDecimal alignAmount2 (@Nonnull final BigDecimal aBD)
  {
    ValueEnforcer.notNull (aBD, "BigDecimal");
    return aBD.setScale (AMOUNT2_FRACTION, ROUNDING_MODE);
  }

  /**
   * @param aBD
   *        Source number. May not be <code>null</code>.
   * @return The number rounded to the number of decimals for sub amounts
   *         (currently 4)
   */
  @Nonnull
  public static BigDecimal alignAmount4 (@Nonnull final BigDecimal aBD)
  {
    ValueEnforcer.notNull (aBD, "BigDecimal");
    return aBD.setScale (AMOUNT4_FRACTION, ROUNDING_MODE);
  }

  @Nullable
  public static InvoiceType createIntermediateFormat (@Nonnull final Document parsedXMLDocument,
                                                      @Nonnull final EbiVersion eDeterminedVersion)
  {
    final Locale aDisplayLocale = CApp.LOCALE_DE_AT;
    final Locale aContentLocale = CApp.LOCALE_DE_AT;
    switch (eDeterminedVersion.getVersion ())
    {
      case V40:
      {
        final Ebi40InvoiceType aEbiDoc = new EbInterface40Marshaller ().read (parsedXMLDocument);
        return new EbInterface40ToInvoiceConverter (aDisplayLocale, aContentLocale).convertInvoice (aEbiDoc);
      }
      case V41:
      {
        final Ebi41InvoiceType aEbiDoc = new EbInterface41Marshaller ().read (parsedXMLDocument);
        return new EbInterface41ToInvoiceConverter (aDisplayLocale, aContentLocale).convertInvoice (aEbiDoc);
      }
      case V42:
      {
        final Ebi42InvoiceType aEbiDoc = new EbInterface42Marshaller ().read (parsedXMLDocument);
        return new EbInterface42ToInvoiceConverter (aDisplayLocale, aContentLocale).convertInvoice (aEbiDoc);
      }
      case V43:
      {
        final Ebi43InvoiceType aEbiDoc = new EbInterface43Marshaller ().read (parsedXMLDocument);
        return new EbInterface43ToInvoiceConverter (aDisplayLocale, aContentLocale).convertInvoice (aEbiDoc);
      }
      case V50:
      {
        final Ebi50InvoiceType aEbiDoc = new EbInterface50Marshaller ().read (parsedXMLDocument);
        return new EbInterface50ToInvoiceConverter (aDisplayLocale, aContentLocale).convertInvoice (aEbiDoc);
      }
      case V60:
      {
        final Ebi60InvoiceType aEbiDoc = new EbInterface60Marshaller ().read (parsedXMLDocument);
        return new EbInterface60ToInvoiceConverter (aDisplayLocale, aContentLocale).convertInvoice (aEbiDoc);
      }
      case V61:
      {
        final Ebi61InvoiceType aEbiDoc = new EbInterface61Marshaller ().read (parsedXMLDocument);
        return new EbInterface61ToInvoiceConverter (aDisplayLocale, aContentLocale).convertInvoice (aEbiDoc);
      }
      default:
        LOGGER.error ("Unsupported ebInterface version: " + eDeterminedVersion.getVersion ());
        return null;
    }
  }

  @Nullable
  public static byte [] createPDF (@Nonnull final Document parsedXMLDocument,
                                   @Nonnull final EbiVersion eDeterminedVersion)
  {
    final InvoiceType aIntermediate = createIntermediateFormat (parsedXMLDocument, eDeterminedVersion);
    if (aIntermediate == null)
    {
      LOGGER.error ("Failed to create intermediate invoice for version " + eDeterminedVersion);
      return null;
    }
    // TODO
    return null;
  }

}
