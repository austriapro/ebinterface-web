package at.ebinterface.validation.web.pages.convert;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.xml.bind.ValidationEventHandler;

import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;
import com.helger.ebinterface.builder.EbInterfaceReader;
import com.helger.ebinterface.v40.Ebi40InvoiceType;
import com.helger.ebinterface.v41.Ebi41InvoiceType;
import com.helger.ebinterface.v42.Ebi42InvoiceType;
import com.helger.ebinterface.v43.Ebi43InvoiceType;
import com.helger.ebinterface.v50.Ebi50InvoiceType;
import com.helger.ebinterface.v60.Ebi60InvoiceType;
import com.helger.ebinterface.v61.Ebi61InvoiceType;
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;
import com.helger.ubl21.UBL21Writer;
import com.helger.xml.serialize.read.DOMReader;

import at.austriapro.ebinterface.xrechnung.EXRechnungVersion;
import at.austriapro.ebinterface.xrechnung.to.ubl.EbInterface40ToXRechnungUBLConverter;
import at.austriapro.ebinterface.xrechnung.to.ubl.EbInterface41ToXRechnungUBLConverter;
import at.austriapro.ebinterface.xrechnung.to.ubl.EbInterface42ToXRechnungUBLConverter;
import at.austriapro.ebinterface.xrechnung.to.ubl.EbInterface43ToXRechnungUBLConverter;
import at.austriapro.ebinterface.xrechnung.to.ubl.EbInterface50ToXRechnungUBLConverter;
import at.austriapro.ebinterface.xrechnung.to.ubl.EbInterface60ToXRechnungUBLConverter;
import at.austriapro.ebinterface.xrechnung.to.ubl.EbInterface61ToXRechnungUBLConverter;
import at.ebinterface.validation.exception.NamespaceUnknownException;
import at.ebinterface.validation.parser.CustomParser;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.convert.result.ResultPageEbiToXRechnung;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Form for converting ebInterface to XRechnung Invoice
 *
 * @author Philip Helger
 */
public final class EbiToXRechnungForm extends Form <Object>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (EbiToXRechnungForm.class);
  private static final ICommonsList <EEbInterfaceVersion> POSSIBLE_EBI_VERSIONS = new CommonsArrayList <> (EEbInterfaceVersion.V61,
                                                                                                           EEbInterfaceVersion.V60,
                                                                                                           EEbInterfaceVersion.V50,
                                                                                                           EEbInterfaceVersion.V43,
                                                                                                           EEbInterfaceVersion.V42,
                                                                                                           EEbInterfaceVersion.V41,
                                                                                                           EEbInterfaceVersion.V40);

  /**
   * Panel for providing feedback in case of erroneous input
   */
  private final FeedbackPanel feedbackPanel;

  /**
   * Upload field for the ebInterface instance
   */
  private final FileUploadField fileUploadField;

  /**
   * The return page
   */
  private final Class <? extends WebPage> m_aReturnPage;

  public EbiToXRechnungForm (final String id, @Nonnull final Class <? extends WebPage> aReturnPage)
  {
    super (id);
    m_aReturnPage = aReturnPage;

    // Add a feedback panel
    feedbackPanel = new FeedbackPanel ("ebiToXRechnungFeedback", new ContainerFeedbackMessageFilter (this));
    feedbackPanel.setVisible (false);
    add (feedbackPanel);

    // Add the file upload field
    fileUploadField = new FileUploadField ("ebiToXRechnungInput");
    fileUploadField.setRequired (true);
    add (fileUploadField);

    // Add a submit button
    add (new SubmitLink ("ebiToXRechnungSubmit"));
  }

  @Override
  protected void onSubmit ()
  {
    feedbackPanel.setVisible (false);

    // Get the file input
    final FileUpload upload = fileUploadField.getFileUpload ();
    byte [] uploadedData = null;
    try
    {
      uploadedData = StreamHelper.getAllBytes (upload.getInputStream ());
    }
    catch (final IOException e)
    {
      LOGGER.error ("Die hochgeladene Datei kann nicht verarbeitet werden.", e);
    }

    // Step 0 - read XML
    final Document aDoc = DOMReader.readXMLDOM (uploadedData);
    if (aDoc == null)
    {
      error ("Die hochgeladene Datei konnte nicht als XML interpretiert werden.");
      onError ();
      return;
    }

    // Step 1 - determine the correct ebInterface version
    EEbInterfaceVersion eVersion;
    try
    {
      eVersion = CustomParser.INSTANCE.getEbInterfaceDetails (aDoc).getVersion ();
    }
    catch (final NamespaceUnknownException e1)
    {
      eVersion = null;
    }

    if (eVersion == null)
    {
      error ("Die hochgeladene Datei konnte nicht als ebInterface-Datei interpretiert werden.");
      onError ();
      return;
    }

    if (!POSSIBLE_EBI_VERSIONS.contains (eVersion))
    {
      error ("Es können nur ebInterface-Dateien in den folgenden Versionen konvertiert werden: " +
             StringHelper.getImplodedMapped (", ", POSSIBLE_EBI_VERSIONS, x -> x.getVersion ().getAsString (false, true)));
      onError ();
      return;
    }

    LOGGER.info ("Parsing upload as ebInterface " + eVersion.getVersion ().getAsString ());

    // Parse ebInterface against XSD
    final ErrorList aErrorListEbi = new ErrorList ();
    final ValidationEventHandler aValidationHdl = new WrappedCollectingValidationEventHandler (aErrorListEbi);
    final Object aParsedInvoice;
    switch (eVersion)
    {
      case V40:
        aParsedInvoice = EbInterfaceReader.ebInterface40 ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V41:
        aParsedInvoice = EbInterfaceReader.ebInterface41 ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V42:
        aParsedInvoice = EbInterfaceReader.ebInterface42 ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V43:
        aParsedInvoice = EbInterfaceReader.ebInterface43 ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V50:
        aParsedInvoice = EbInterfaceReader.ebInterface50 ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V60:
        aParsedInvoice = EbInterfaceReader.ebInterface60 ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V61:
        aParsedInvoice = EbInterfaceReader.ebInterface61 ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      default:
        throw new IllegalStateException ("Internal inconsistency: " + eVersion);
    }

    final Locale aDisplayLocale = Constants.DE_AT;
    final Locale aContentLocale = Constants.DE_AT;

    if (aParsedInvoice == null)
    {
      error ("Die ebInterface-Datei entspricht nicht dem XML Schema und kann daher nicht verarbeitet werden.");
      for (final IError aError : aErrorListEbi.getAllFailures ())
        error ("Fehler: " + aError.getAsString (aDisplayLocale));
      onError ();
      return;
    }

    LOGGER.info ("Converting ebInterface " + eVersion.getVersion ().getAsString () + " to XRechnung");

    final InvoiceType aUBLInvoice;
    final ErrorList aConvertErrorList = new ErrorList ();
    final EXRechnungVersion eXRechnungVersion = EXRechnungVersion.V22;
    // Convert ebInterface to XRechnung
    switch (eVersion)
    {
      case V40:
        aUBLInvoice = new EbInterface40ToXRechnungUBLConverter (aDisplayLocale,
                                                                aContentLocale,
                                                                eXRechnungVersion).convert ((Ebi40InvoiceType) aParsedInvoice,
                                                                                            aConvertErrorList);
        break;
      case V41:
        aUBLInvoice = new EbInterface41ToXRechnungUBLConverter (aDisplayLocale,
                                                                aContentLocale,
                                                                eXRechnungVersion).convert ((Ebi41InvoiceType) aParsedInvoice,
                                                                                            aConvertErrorList);
        break;
      case V42:
        aUBLInvoice = new EbInterface42ToXRechnungUBLConverter (aDisplayLocale,
                                                                aContentLocale,
                                                                eXRechnungVersion).convert ((Ebi42InvoiceType) aParsedInvoice,
                                                                                            aConvertErrorList);
        break;
      case V43:
        aUBLInvoice = new EbInterface43ToXRechnungUBLConverter (aDisplayLocale,
                                                                aContentLocale,
                                                                eXRechnungVersion).convert ((Ebi43InvoiceType) aParsedInvoice,
                                                                                            aConvertErrorList);
        break;
      case V50:
        aUBLInvoice = new EbInterface50ToXRechnungUBLConverter (aDisplayLocale,
                                                                aContentLocale,
                                                                eXRechnungVersion).convert ((Ebi50InvoiceType) aParsedInvoice,
                                                                                            aConvertErrorList);
        break;
      case V60:
        aUBLInvoice = new EbInterface60ToXRechnungUBLConverter (aDisplayLocale,
                                                                aContentLocale,
                                                                eXRechnungVersion).convert ((Ebi60InvoiceType) aParsedInvoice,
                                                                                            aConvertErrorList);
        break;
      case V61:
        aUBLInvoice = new EbInterface61ToXRechnungUBLConverter (aDisplayLocale,
                                                                aContentLocale,
                                                                eXRechnungVersion).convert ((Ebi61InvoiceType) aParsedInvoice,
                                                                                            aConvertErrorList);
        break;
      default:
        throw new IllegalStateException ("This ebInterface version is unknown: " + eVersion);
    }

    final StringBuilder aErrorLog = new StringBuilder ();
    final byte [] aUBLXML;
    if (aConvertErrorList.containsAtLeastOneError ())
    {
      aErrorLog.append ("<b>Bei der ebInterface-XRechnung-Konvertierung sind folgende Fehler aufgetreten:</b><br/>");
      for (final IError error : aConvertErrorList.getAllErrors ())
      {
        aErrorLog.append (error.getErrorFieldName ()).append (":<br/>").append (error.getErrorText (aDisplayLocale)).append ("<br/><br/>");
      }
      aUBLXML = null;
    }
    else
    {
      LOGGER.info ("Conversion from ebInterface to XRechnung was successful");
      // No need to collect errors here, because the validation was already
      // performed previously
      aUBLXML = UBL21Writer.invoice ().getAsBytes (aUBLInvoice);
    }

    // Redirect
    setResponsePage (new ResultPageEbiToXRechnung (aUBLXML, aErrorLog.toString (), m_aReturnPage));
  }

  /**
   * Process errors
   */
  @Override
  protected void onError ()
  {
    // Show the feedback panel in case on an error
    feedbackPanel.setVisible (true);
  }
}
