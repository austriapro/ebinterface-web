package at.ebinterface.validation.web.pages.convert;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Nonnull;

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
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;
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
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;
import com.helger.ubl21.UBL21Marshaller;
import com.helger.xml.serialize.read.DOMReader;

import at.austriapro.ebinterface.ubl.to.EbInterface40ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface41ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface42ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface43ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface50ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface60ToInvoiceConverter;
import at.austriapro.ebinterface.ubl.to.EbInterface61ToInvoiceConverter;
import at.ebinterface.validation.exception.NamespaceUnknownException;
import at.ebinterface.validation.parser.CustomParser;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.convert.result.ResultPageEbiToUbl;
import jakarta.xml.bind.ValidationEventHandler;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Form for converting ebInterface to UBL Invoice
 *
 * @author Philip Helger
 */
public final class EbiToUblForm extends Form <Object>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (EbiToUblForm.class);
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

  public EbiToUblForm (final String id, @Nonnull final Class <? extends WebPage> aReturnPage)
  {
    super (id);
    m_aReturnPage = aReturnPage;

    // Add a feedback panel
    feedbackPanel = new FeedbackPanel ("ebiToUblFeedback", new ContainerFeedbackMessageFilter (this));
    feedbackPanel.setVisible (false);
    add (feedbackPanel);

    // Add the file upload field
    fileUploadField = new FileUploadField ("ebiToUblInput");
    fileUploadField.setRequired (true);
    add (fileUploadField);

    // Add a submit button
    add (new SubmitLink ("ebiToUblSubmit"));
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
    EEbInterfaceVersion eVersion = null;
    try
    {
      eVersion = CustomParser.INSTANCE.getEbInterfaceDetails (aDoc).getVersion ();
    }
    catch (final NamespaceUnknownException e1)
    {
      // Ignore
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
             StringHelper.getImplodedMapped (", ",
                                             POSSIBLE_EBI_VERSIONS,
                                             x -> x.getVersion ().getAsString (false, true)));
      onError ();
      return;
    }

    LOGGER.info ("Parsing upload as ebInterface " + eVersion.getVersion ().getAsString ());

    // Parse ebInterface against XSD
    final ErrorList aErrorList = new ErrorList ();
    final ValidationEventHandler aValidationHdl = new WrappedCollectingValidationEventHandler (aErrorList);
    final Object aParsedInvoice;
    switch (eVersion)
    {
      case V40:
        aParsedInvoice = new EbInterface40Marshaller ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V41:
        aParsedInvoice = new EbInterface41Marshaller ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V42:
        aParsedInvoice = new EbInterface42Marshaller ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V43:
        aParsedInvoice = new EbInterface43Marshaller ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V50:
        aParsedInvoice = new EbInterface50Marshaller ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V60:
        aParsedInvoice = new EbInterface60Marshaller ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      case V61:
        aParsedInvoice = new EbInterface61Marshaller ().setValidationEventHandler (aValidationHdl).read (uploadedData);
        break;
      default:
        throw new IllegalStateException ("Internal inconsistency: " + eVersion);
    }

    final Locale aDisplayLocale = Constants.DE_AT;
    final Locale aContentLocale = Constants.DE_AT;

    if (aParsedInvoice == null)
    {
      error ("Die ebInterface-Datei entspricht nicht dem XML Schema und kann daher nicht verarbeitet werden.");
      for (final IError aError : aErrorList.getAllFailures ())
        error ("Fehler: " + aError.getAsString (aDisplayLocale));
      onError ();
      return;
    }

    LOGGER.info ("Converting ebInterface " + eVersion.getVersion ().getAsString () + " to UBL Invoice");

    final InvoiceType aUBLInvoice;
    // Convert ebInterface to UBL
    switch (eVersion)
    {
      case V40:
        aUBLInvoice = new EbInterface40ToInvoiceConverter (aDisplayLocale,
                                                           aContentLocale).convertInvoice ((Ebi40InvoiceType) aParsedInvoice);
        break;
      case V41:
        aUBLInvoice = new EbInterface41ToInvoiceConverter (aDisplayLocale,
                                                           aContentLocale).convertInvoice ((Ebi41InvoiceType) aParsedInvoice);
        break;
      case V42:
        aUBLInvoice = new EbInterface42ToInvoiceConverter (aDisplayLocale,
                                                           aContentLocale).convertInvoice ((Ebi42InvoiceType) aParsedInvoice);
        break;
      case V43:
        aUBLInvoice = new EbInterface43ToInvoiceConverter (aDisplayLocale,
                                                           aContentLocale).convertInvoice ((Ebi43InvoiceType) aParsedInvoice);
        break;
      case V50:
        aUBLInvoice = new EbInterface50ToInvoiceConverter (aDisplayLocale,
                                                           aContentLocale).convertInvoice ((Ebi50InvoiceType) aParsedInvoice);
        break;
      case V60:
        aUBLInvoice = new EbInterface60ToInvoiceConverter (aDisplayLocale,
                                                           aContentLocale).convertInvoice ((Ebi60InvoiceType) aParsedInvoice);
        break;
      case V61:
        aUBLInvoice = new EbInterface61ToInvoiceConverter (aDisplayLocale,
                                                           aContentLocale).convertInvoice ((Ebi61InvoiceType) aParsedInvoice);
        break;
      default:
        throw new IllegalStateException ("This ebInterface version is unknown: " + eVersion);
    }

    // Check if the result is okay or not
    final IErrorList aUBLErrorList = UBL21Marshaller.invoice ().validate (aUBLInvoice);

    final StringBuilder aErrorLog = new StringBuilder ();
    final byte [] aUBLXML;
    if (aUBLErrorList.containsAtLeastOneError ())
    {
      aErrorLog.append ("<b>Bei der ebInterface-UBL-Konvertierung sind folgende Fehler aufgetreten:</b><br/>");
      for (final IError error : aUBLErrorList.getAllErrors ())
      {
        aErrorLog.append (error.getErrorFieldName ())
                 .append (":<br/>")
                 .append (error.getErrorText (aDisplayLocale))
                 .append ("<br/><br/>");
      }
      aUBLXML = null;
    }
    else
    {
      LOGGER.info ("Conversion from ebInterface to UBL Invoice was successful");
      // No need to collect errors here, because the validation was already
      // performed previously
      aUBLXML = UBL21Marshaller.invoice ().getAsBytes (aUBLInvoice);
    }

    // Redirect
    setResponsePage (new ResultPageEbiToUbl (aUBLXML, aErrorLog.toString (), m_aReturnPage));
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
