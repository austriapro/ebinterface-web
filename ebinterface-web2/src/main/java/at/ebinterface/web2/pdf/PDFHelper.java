package at.ebinterface.web2.pdf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.datetime.format.PDTToString;
import com.helger.ebinterface.EbInterface40Marshaller;
import com.helger.ebinterface.EbInterface41Marshaller;
import com.helger.ebinterface.EbInterface42Marshaller;
import com.helger.ebinterface.EbInterface43Marshaller;
import com.helger.ebinterface.EbInterface50Marshaller;
import com.helger.ebinterface.EbInterface60Marshaller;
import com.helger.ebinterface.EbInterface61Marshaller;
import com.helger.ebinterface.codelist.EFurtherIdentification;
import com.helger.ebinterface.v40.Ebi40InvoiceType;
import com.helger.ebinterface.v41.Ebi41InvoiceType;
import com.helger.ebinterface.v42.Ebi42InvoiceType;
import com.helger.ebinterface.v43.Ebi43InvoiceType;
import com.helger.ebinterface.v50.Ebi50InvoiceType;
import com.helger.ebinterface.v60.Ebi60InvoiceType;
import com.helger.ebinterface.v61.Ebi61InvoiceType;
import com.helger.text.locale.country.CountryCache;

import at.austriapro.ebinterface.ubl.AbstractEbInterfaceUBLConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface40ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface41ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface42ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface43ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface50ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface60ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface61ToInvoiceConverter;
import at.ebinterface.validation.parser.EbiVersion;
import at.ebinterface.web2.app.CApp;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PeriodType;
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

  /**
   * Remove duplicate whitespaces but ensure to keep all newlines!
   *
   * @param s
   *        Source string. Should be trimmed already
   * @return Unified string.
   */
  @Nonnull
  public static String unifySpaces (@Nonnull final String s)
  {
    String sUnified = s;
    // Replace \r and \t with space
    sUnified = StringReplace.replaceAll (sUnified, '\r', ' ');
    sUnified = StringReplace.replaceAll (sUnified, '\t', ' ');
    // Merge all duplicate spaces
    sUnified = StringReplace.replaceAllRepeatedly (sUnified, "  ", " ");
    // Avoid blanks at line end
    return StringReplace.replaceAll (sUnified, " \n", "\n");
  }

  /**
   * Get the content of the passed delivery object as a string. It combines the
   * delivery date/period and the delivery address.
   *
   * @param aDelivery
   *        The delivery to be converted. May be <code>null</code>.
   * @param sSep
   *        The separator to be used, to combine the different elements into one
   *        big string. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The display locale to use for text resolving. May not be
   *        <code>null</code>.
   * @return <code>null</code> only if the passed delivery is <code>null</code>.
   */
  @Nullable
  public static String getDeliveryAsString (@Nullable final DeliveryType aDelivery,
                                            @Nonnull final String sSep,
                                            @Nonnull final Locale aDisplayLocale)
  {
    if (aDelivery == null)
      return null;

    final ICommonsList <String> aElements = new CommonsArrayList <> ();

    if (StringHelper.isNotEmpty (aDelivery.getIDValue ()))
      aElements.add (EPDFText.DELIVERY_ID.getDisplayTextWithArgs (aDisplayLocale, aDelivery.getID ()));

    if (aDelivery.getActualDeliveryDateValue () != null)
    {
      // Add date
      aElements.add (EPDFText.DELIVERED_AT.getDisplayTextWithArgs (aDisplayLocale,
                                                                   PDTToString.getAsString (aDelivery.getActualDeliveryDateValue (),
                                                                                            aDisplayLocale)));
    }
    else
      if (aDelivery.getRequestedDeliveryPeriod () != null)
      {
        // Add period
        final PeriodType aPeriod = aDelivery.getRequestedDeliveryPeriod ();
        aElements.add (EPDFText.PERIOD_OF_SERVICE.getDisplayTextWithArgs (aDisplayLocale,
                                                                          PDTToString.getAsString (aPeriod.getStartDateValue (),
                                                                                                   aDisplayLocale),
                                                                          PDTToString.getAsString (aPeriod.getEndDateValue (),
                                                                                                   aDisplayLocale)));
      }

    // Add address elements
    final AddressType aAddress = aDelivery.getDeliveryAddress ();
    if (aAddress != null)
    {
      // Salutation + name
      final String sAddressString = createAddressString (aAddress, aDisplayLocale);
      aElements.addAll (StringHelper.getExploded ('\n', sAddressString));
    }

    final String sElements = aElements.isEmpty () ? null
                                                  : StringImplode.imploder ()
                                                                 .source (aElements)
                                                                 .separator (sSep)
                                                                 .build ();
    final String sDescription = aDelivery.hasDeliveryTermsEntries () &&
                                aDelivery.getDeliveryTermsAtIndex (0).hasSpecialTermsEntries ()
                                                                                                ? StringHelper.trim (aDelivery.getDeliveryTermsAtIndex (0)
                                                                                                                              .getSpecialTermsAtIndex (0)
                                                                                                                              .getValue ())
                                                                                                : null;
    final String ret = StringImplode.imploder ()
                                    .source (sElements, sDescription)
                                    .separator ('\n')
                                    .filterNonEmpty ()
                                    .build ();
    // Avoid returning ""
    return StringHelper.isNotEmpty (ret) ? ret : null;
  }

  @Nonnull
  public static String createContactDetails (@Nonnull final Locale aDisplayLocale, @Nonnull final ContactType aContact)
  {
    final StringBuilder aContactDetails = new StringBuilder ();

    // Contact
    final String sContact = StringHelper.trim (aContact.getNameValue ());
    if (StringHelper.isNotEmpty (sContact))
      aContactDetails.append (unifySpaces (sContact));

    // Email address
    final String sEmail = StringHelper.trim (aContact.getElectronicMailValue ());
    if (StringHelper.isNotEmpty (sEmail))
    {
      // Avoid contact and email as this might get too long
      if (aContactDetails.length () > 0)
        aContactDetails.append ('\n');
      aContactDetails.append (sEmail);
    }

    // Append telephone number
    String sTelephone = StringHelper.trim (aContact.getTelephoneValue ());
    if (StringHelper.isNotEmpty (sTelephone))
    {
      if (aContactDetails.length () > 0)
        aContactDetails.append ('\n');

      // Take at last one line of telephone
      final int nIndex = sTelephone.indexOf ('\n');
      if (nIndex > 0)
        sTelephone = unifySpaces (sTelephone.substring (0, nIndex).trim ()) + " [+]";

      aContactDetails.append (EPDFText.PHONE_NUMBER.getDisplayTextWithArgs (aDisplayLocale, sTelephone));
    }

    return aContactDetails.toString ();
  }

  @Nonnull
  public static String createAddressString (@Nonnull final AddressType aAddress, @Nonnull final Locale aDisplayLocale)
  {
    final StringBuilder aSB = new StringBuilder ();
    // Street
    final String sStreet = StringHelper.trim (aAddress.getStreetNameValue ());
    if (StringHelper.isNotEmpty (sStreet))
    {
      if (aSB.length () > 0)
        aSB.append ('\n');
      aSB.append (unifySpaces (sStreet));
    }

    // PO Box
    final String sPOBox = StringHelper.trim (aAddress.getPostboxValue ());
    if (StringHelper.isNotEmpty (sPOBox))
    {
      if (aSB.length () > 0)
        aSB.append ('\n');
      aSB.append (unifySpaces (sPOBox));
    }

    // ZIP code + town
    final String sZipAndTown = StringHelper.getConcatenatedOnDemand (StringHelper.trim (aAddress.getPostalZoneValue ()),
                                                                     " ",
                                                                     StringHelper.trim (aAddress.getCityNameValue ()));
    if (StringHelper.isNotEmpty (sZipAndTown))
    {
      if (aSB.length () > 0)
        aSB.append ('\n');
      aSB.append (unifySpaces (sZipAndTown));
    }

    // Country
    final String sCountry = CountryCache.getInstance ()
                                        .getCountry (aAddress.getCountry ().getIdentificationCodeValue ())
                                        .getDisplayName (aDisplayLocale);
    if (StringHelper.isNotEmpty (sCountry))
    {
      if (aSB.length () > 0)
        aSB.append ('\n');
      aSB.append (unifySpaces (sCountry));
    }
    return aSB.toString ();
  }

  @Nonnull
  public static String createPersonAndAddressString (@Nonnull final PartyType aParty,
                                                     @Nonnull final Locale aDisplayLocale,
                                                     final boolean bIncludingContact)
  {
    final StringBuilder aSB = new StringBuilder ();

    // Salutation
    final String sSalutation = aParty.hasPersonEntries () ? StringHelper.trim (aParty.getPersonAtIndex (0)
                                                                                     .getGenderCodeValue ())
                                                          : null;
    if (StringHelper.isNotEmpty (sSalutation))
      aSB.append (unifySpaces (sSalutation));

    // Name
    final String sName = aParty.hasPartyNameEntries () ? StringHelper.trim (aParty.getPartyNameAtIndex (0)
                                                                                  .getNameValue ())
                                                       : null;
    if (StringHelper.isNotEmpty (sName))
    {
      if (aSB.length () > 0)
        aSB.append ('\n');
      aSB.append (unifySpaces (sName));
    }

    if (bIncludingContact)
    {
      final String sContact = createContactDetails (aDisplayLocale, aParty.getContact ());
      if (StringHelper.isNotEmpty (sContact))
      {
        if (aSB.length () > 0)
          aSB.append ('\n');
        aSB.append (unifySpaces (sContact));
      }
    }

    aSB.append (createAddressString (aParty.getPostalAddress (), aDisplayLocale));

    // VATIN
    String sVATIN = null;
    if (aParty.hasPartyTaxSchemeEntries ())
      sVATIN = aParty.getPartyTaxSchemeAtIndex (0).getCompanyIDValue ();
    final String sRealVATIN = StringHelper.trim (sVATIN);
    if (StringHelper.isNotEmpty (sRealVATIN))
    {
      if (aSB.length () > 0)
        aSB.append ('\n');
      aSB.append (EPDFText.VATIN.getDisplayText (aDisplayLocale)).append (": ").append (unifySpaces (sRealVATIN));
    }

    final ICommonsMap <String, String> aFurtherIdentifications = new CommonsHashMap <> ();
    for (final var aPI : aParty.getPartyIdentification ())
      if (aPI.getID () != null &&
          AbstractEbInterfaceUBLConverter.FURTHER_IDENTIFICATION_SCHEME_NAME_EBI2UBL.equals (aPI.getID ()
                                                                                                .getSchemeName ()))
        aFurtherIdentifications.put (aPI.getID ().getSchemeID (), aPI.getIDValue ());

    if (aFurtherIdentifications.isNotEmpty ())
    {
      // Office location
      final String sOfficeLocation = aFurtherIdentifications.get (EFurtherIdentification.OFFICE_LOCATION.getID ());
      if (StringHelper.isNotEmpty (sOfficeLocation))
      {
        if (aSB.length () > 0)
          aSB.append ('\n');
        aSB.append (EFurtherIdentification.OFFICE_LOCATION.getDisplayName ())
           .append (": ")
           .append (unifySpaces (sOfficeLocation));
      }

      // Commercial registration number
      final String sCommercialRegistrationNumber = aFurtherIdentifications.get (EFurtherIdentification.COMMERCIAL_REGISTER_NUMBER.getID ());
      if (StringHelper.isNotEmpty (sCommercialRegistrationNumber))
      {
        if (aSB.length () > 0)
          aSB.append ('\n');
        aSB.append (EFurtherIdentification.COMMERCIAL_REGISTER_NUMBER.getDisplayName ())
           .append (": ")
           .append (unifySpaces (sCommercialRegistrationNumber));
      }

      // Commercial court
      final String sCommercialCourt = aFurtherIdentifications.get (EFurtherIdentification.COMMERCIAL_COURT.getID ());
      if (StringHelper.isNotEmpty (sCommercialCourt))
      {
        if (aSB.length () > 0)
          aSB.append ('\n');
        aSB.append (EFurtherIdentification.COMMERCIAL_COURT.getDisplayName ())
           .append (": ")
           .append (unifySpaces (sCommercialCourt));
      }

      // BBG Partnernummer
      final String sBBGPartnerNumber = aFurtherIdentifications.get (EFurtherIdentification.CONSOLIDATOR.getID ());
      if (StringHelper.isNotEmpty (sBBGPartnerNumber))
      {
        if (aSB.length () > 0)
          aSB.append ('\n');
        aSB.append (EFurtherIdentification.CONSOLIDATOR.getDisplayName ())
           .append (": ")
           .append (unifySpaces (sBBGPartnerNumber));
      }

      // BBG Geschäftszahl (should be either or with the previous one)
      final String sBBGReferenceNumber = aFurtherIdentifications.get (EFurtherIdentification.BBG_GZ.getID ());
      if (StringHelper.isNotEmpty (sBBGReferenceNumber))
      {
        if (aSB.length () > 0)
          aSB.append ('\n');
        aSB.append (EFurtherIdentification.BBG_GZ.getDisplayName ())
           .append (": ")
           .append (unifySpaces (sBBGReferenceNumber));
      }
    }

    return StringHelper.trim (aSB.toString ());
  }

  /**
   * Find the appropriate delivery element, that should be put on the cover
   * page.
   *
   * @param aInvoice
   *        The invoice to scan for the delivery element. May not be
   *        <code>null</code>.
   * @return <code>null</code> if no matching delivery element was found
   */
  @Nullable
  public static DeliveryType findCoverDelivery (@Nonnull final InvoiceType aInvoice)
  {
    // Check if a delivery is present at biller (=header) level
    if (aInvoice.hasDeliveryEntries ())
      return aInvoice.getDeliveryAtIndex (0);

    // No delivery at biller -> search items
    final ICommonsList <DeliveryType> aDetailDeliveries = new CommonsArrayList <> ();
    if (aInvoice.hasInvoiceLineEntries ())
      for (final InvoiceLineType aDetailsItem : aInvoice.getInvoiceLine ())
        if (aDetailsItem.getDelivery () != null)
          aDetailDeliveries.addAll (aDetailsItem.getDelivery ());

    // Are there any detail deliveries?
    if (aDetailDeliveries.isEmpty ())
      return null;

    // Is there exactly one delivery?
    if (aDetailDeliveries.size () == 1)
      return aDetailDeliveries.getFirstOrNull ();

    // Now check, if all detail deliveries are equal
    boolean bAllEqual = true;
    final int nMax = aDetailDeliveries.size ();
    for (int i = 1; i < nMax; ++i)
      if (!aDetailDeliveries.get (i).equals (aDetailDeliveries.get (i - 1)))
      {
        bAllEqual = false;
        break;
      }

    // if all are equal, pick any element, else nothing to show
    return bAllEqual ? aDetailDeliveries.getFirstOrNull () : null;
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
