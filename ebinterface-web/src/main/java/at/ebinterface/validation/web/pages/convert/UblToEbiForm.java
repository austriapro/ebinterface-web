package at.ebinterface.validation.web.pages.convert;

import java.io.IOException;
import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.stream.StreamHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.ebinterface.EEbInterfaceVersion;
import com.helger.ebinterface.EbInterface41Marshaller;
import com.helger.ebinterface.EbInterface42Marshaller;
import com.helger.ebinterface.EbInterface43Marshaller;
import com.helger.ebinterface.EbInterface50Marshaller;
import com.helger.ebinterface.EbInterface60Marshaller;
import com.helger.ebinterface.EbInterface61Marshaller;
import com.helger.ebinterface.v41.Ebi41InvoiceType;
import com.helger.ebinterface.v42.Ebi42InvoiceType;
import com.helger.ebinterface.v43.Ebi43InvoiceType;
import com.helger.ebinterface.v50.Ebi50InvoiceType;
import com.helger.ebinterface.v60.Ebi60InvoiceType;
import com.helger.ebinterface.v61.Ebi61InvoiceType;
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;
import com.helger.ubl21.UBL21Marshaller;

import at.austriapro.ebinterface.ubl.from.IToEbinterfaceSettings;
import at.austriapro.ebinterface.ubl.from.ToEbinterfaceSettings;
import at.austriapro.ebinterface.ubl.from.creditnote.CreditNoteToEbInterface41Converter;
import at.austriapro.ebinterface.ubl.from.creditnote.CreditNoteToEbInterface42Converter;
import at.austriapro.ebinterface.ubl.from.creditnote.CreditNoteToEbInterface43Converter;
import at.austriapro.ebinterface.ubl.from.creditnote.CreditNoteToEbInterface50Converter;
import at.austriapro.ebinterface.ubl.from.creditnote.CreditNoteToEbInterface60Converter;
import at.austriapro.ebinterface.ubl.from.creditnote.CreditNoteToEbInterface61Converter;
import at.austriapro.ebinterface.ubl.from.invoice.InvoiceToEbInterface41Converter;
import at.austriapro.ebinterface.ubl.from.invoice.InvoiceToEbInterface42Converter;
import at.austriapro.ebinterface.ubl.from.invoice.InvoiceToEbInterface43Converter;
import at.austriapro.ebinterface.ubl.from.invoice.InvoiceToEbInterface50Converter;
import at.austriapro.ebinterface.ubl.from.invoice.InvoiceToEbInterface60Converter;
import at.austriapro.ebinterface.ubl.from.invoice.InvoiceToEbInterface61Converter;
import at.austriapro.rendering.BaseRenderer;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.components.AbstractChoiceRenderer;
import at.ebinterface.validation.web.pages.convert.result.ResultPageUblToEbi;
import jakarta.annotation.Nonnull;
import net.sf.jasperreports.engine.JasperReport;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Form for showing the rules which are currently supported
 *
 * @author pl
 */
public final class UblToEbiForm extends Form <Object>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (UblToEbiForm.class);
  private static final ICommonsList <EEbInterfaceVersion> POSSIBLE_EBI_VERSIONS = new CommonsArrayList <> (EEbInterfaceVersion.V61,
                                                                                                           EEbInterfaceVersion.V60,
                                                                                                           EEbInterfaceVersion.V50,
                                                                                                           EEbInterfaceVersion.V43,
                                                                                                           EEbInterfaceVersion.V42,
                                                                                                           EEbInterfaceVersion.V41);

  /**
   * Panel for providing feedback in case of erroneous input
   */
  private FeedbackPanel m_aFeedbackPanel;

  /**
   * Upload field for the ebInterface instance
   */
  private FileUploadField fileUploadField;

  /**
   * Dropdown choice for the ebInterface versions
   */
  private DropDownChoice <EEbInterfaceVersion> ebiVersions;

  /**
   * The return page
   */
  private final Class <? extends WebPage> m_aReturnPage;

  public UblToEbiForm (final String id, @Nonnull final Class <? extends WebPage> aReturnPage)
  {
    super (id);
    m_aReturnPage = aReturnPage;

    // Add a feedback panel
    m_aFeedbackPanel = new FeedbackPanel ("ublToEbiFeedback", new ContainerFeedbackMessageFilter (this));
    m_aFeedbackPanel.setVisible (false);
    add (m_aFeedbackPanel);

    // Add the file upload field
    fileUploadField = new FileUploadField ("ublToEbiInput");
    fileUploadField.setRequired (true);
    add (fileUploadField);

    // Add the drop down choice for the different rules which are currently
    // supported
    ebiVersions = new DropDownChoice <> ("ublToEbiVersionSelector",
                                         Model.of (POSSIBLE_EBI_VERSIONS.getFirstOrNull ()),
                                         POSSIBLE_EBI_VERSIONS,
                                         new AbstractChoiceRenderer <EEbInterfaceVersion> ()
                                         {
                                           @Override
                                           public Object getDisplayValue (final EEbInterfaceVersion object)
                                           {
                                             return "ebInterface " + object.getVersion ().getAsString (false, true);
                                           }

                                           @Override
                                           public String getIdValue (final EEbInterfaceVersion object, final int index)
                                           {
                                             return object.getNamespaceURI ();
                                           }
                                         });
    add (ebiVersions);

    // Add a submit button
    add (new SubmitLink ("ublToEbiSubmit"));
  }

  @Override
  protected void onSubmit ()
  {
    m_aFeedbackPanel.setVisible (false);

    // Get the selected version
    final EEbInterfaceVersion eVersion = ebiVersions.getModelObject ();
    if (eVersion == null)
    {
      error (new ResourceModel ("ebiVersion.Required").getObject ());
      onError ();
      return;
    }

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

    final Locale aDisplayLocale = Constants.DE_AT;
    final Locale aContentLocale = Constants.DE_AT;

    // Read UBL
    final ErrorList aReadErrors = new ErrorList ();
    // First try Invoice
    final InvoiceType aUBLInvoice = UBL21Marshaller.invoice ()
                                                   .setValidationEventHandler (new WrappedCollectingValidationEventHandler (aReadErrors))
                                                   .read (uploadedData);
    final CreditNoteType aUBLCreditNote;
    if (aUBLInvoice == null)
    {
      // No Invoice - try credit note
      aUBLCreditNote = UBL21Marshaller.creditNote ()
                                      .setValidationEventHandler (new WrappedCollectingValidationEventHandler (aReadErrors))
                                      .read (uploadedData);
    }
    else
      aUBLCreditNote = null;

    if (aUBLInvoice == null && aUBLCreditNote == null)
    {
      error ("Das UBL kann nicht verarbeitet werden. Es können nur UBL Invoice und CreditNote Dokumente verarbeitet werden.");
      // Log errors in case somebody cares
      LOGGER.warn ("UBL parsing errors:");
      for (final IError aError : aReadErrors.getAllFailures ())
        LOGGER.warn ("  " + aError.getAsString (aDisplayLocale));
      onError ();
      return;
    }

    // Convert to ebInterface
    final IToEbinterfaceSettings aToEbiSettings = new ToEbinterfaceSettings ();
    final ErrorList aErrorList = new ErrorList ();
    byte [] ebInterface = null;
    switch (eVersion)
    {
      case V41:
        final Ebi41InvoiceType aEb41Invoice;
        if (aUBLInvoice != null)
        {
          // It's an invoice
          aEb41Invoice = new InvoiceToEbInterface41Converter (aDisplayLocale,
                                                              aContentLocale,
                                                              aToEbiSettings).convertToEbInterface (aUBLInvoice,
                                                                                                    aErrorList);
        }
        else
        {
          // It' a credit note
          aEb41Invoice = new CreditNoteToEbInterface41Converter (aDisplayLocale,
                                                                 aContentLocale,
                                                                 aToEbiSettings).convertToEbInterface (aUBLCreditNote,
                                                                                                       aErrorList);
        }
        if (aEb41Invoice != null)
          ebInterface = new EbInterface41Marshaller ().getAsBytes (aEb41Invoice);
        break;
      case V42:
        final Ebi42InvoiceType aEb42Invoice;
        if (aUBLInvoice != null)
        {
          // It's an invoice
          aEb42Invoice = new InvoiceToEbInterface42Converter (aDisplayLocale,
                                                              aContentLocale,
                                                              aToEbiSettings).convertToEbInterface (aUBLInvoice,
                                                                                                    aErrorList);
        }
        else
        {
          // It' a credit note
          aEb42Invoice = new CreditNoteToEbInterface42Converter (aDisplayLocale,
                                                                 aContentLocale,
                                                                 aToEbiSettings).convertToEbInterface (aUBLCreditNote,
                                                                                                       aErrorList);
        }
        if (aEb42Invoice != null)
          ebInterface = new EbInterface42Marshaller ().getAsBytes (aEb42Invoice);
        break;
      case V43:
        final Ebi43InvoiceType aEb43Invoice;
        if (aUBLInvoice != null)
        {
          // It's an invoice
          aEb43Invoice = new InvoiceToEbInterface43Converter (aDisplayLocale,
                                                              aContentLocale,
                                                              aToEbiSettings).convertToEbInterface (aUBLInvoice,
                                                                                                    aErrorList);
        }
        else
        {
          // It' a credit note
          aEb43Invoice = new CreditNoteToEbInterface43Converter (aDisplayLocale,
                                                                 aContentLocale,
                                                                 aToEbiSettings).convertToEbInterface (aUBLCreditNote,
                                                                                                       aErrorList);
        }
        if (aEb43Invoice != null)
          ebInterface = new EbInterface43Marshaller ().getAsBytes (aEb43Invoice);
        break;
      case V50:
        final Ebi50InvoiceType aEb50Invoice;
        if (aUBLInvoice != null)
        {
          // It's an invoice
          aEb50Invoice = new InvoiceToEbInterface50Converter (aDisplayLocale,
                                                              aContentLocale,
                                                              aToEbiSettings).convertToEbInterface (aUBLInvoice,
                                                                                                    aErrorList);
        }
        else
        {
          // It' a credit note
          aEb50Invoice = new CreditNoteToEbInterface50Converter (aDisplayLocale,
                                                                 aContentLocale,
                                                                 aToEbiSettings).convertToEbInterface (aUBLCreditNote,
                                                                                                       aErrorList);
        }
        if (aEb50Invoice != null)
          ebInterface = new EbInterface50Marshaller ().getAsBytes (aEb50Invoice);
        break;
      case V60:
        final Ebi60InvoiceType aEb60Invoice;
        if (aUBLInvoice != null)
        {
          // It's an invoice
          aEb60Invoice = new InvoiceToEbInterface60Converter (aDisplayLocale,
                                                              aContentLocale,
                                                              aToEbiSettings).convertToEbInterface (aUBLInvoice,
                                                                                                    aErrorList);
        }
        else
        {
          // It' a credit note
          aEb60Invoice = new CreditNoteToEbInterface60Converter (aDisplayLocale,
                                                                 aContentLocale,
                                                                 aToEbiSettings).convertToEbInterface (aUBLCreditNote,
                                                                                                       aErrorList);
        }
        if (aEb60Invoice != null)
          ebInterface = new EbInterface60Marshaller ().getAsBytes (aEb60Invoice);
        break;
      case V61:
        final Ebi61InvoiceType aEb61Invoice;
        if (aUBLInvoice != null)
        {
          // It's an invoice
          aEb61Invoice = new InvoiceToEbInterface61Converter (aDisplayLocale,
                                                              aContentLocale,
                                                              aToEbiSettings).convertToEbInterface (aUBLInvoice,
                                                                                                    aErrorList);
        }
        else
        {
          // It' a credit note
          aEb61Invoice = new CreditNoteToEbInterface61Converter (aDisplayLocale,
                                                                 aContentLocale,
                                                                 aToEbiSettings).convertToEbInterface (aUBLCreditNote,
                                                                                                       aErrorList);
        }
        if (aEb61Invoice != null)
          ebInterface = new EbInterface61Marshaller ().getAsBytes (aEb61Invoice);
        break;
      default:
        throw new IllegalStateException ("This ebInterface version is unknown: " + eVersion);
    }

    final StringBuilder sbLog = new StringBuilder ();

    final ValidationResult validationResult;
    byte [] pdf = null;
    if (aErrorList.containsAtLeastOneError () || ebInterface == null)
    {
      validationResult = new ValidationResult ();
      validationResult.setSchemaValidationErrorMessage ("Die Schemavalidierung konnte nicht durchgeführt werden.");

      sbLog.append ("<b>Bei der UBL-ebInterface-Konvertierung sind folgende Fehler aufgetreten:</b><br/>");
      for (final IError error : aErrorList.getAllErrors ())
      {
        sbLog.append (error.getErrorFieldName ())
             .append (":<br/>")
             .append (error.getErrorText (aDisplayLocale))
             .append ("<br/><br/>");
      }
    }
    else
    {
      // Validate the XML instance - performed in any case
      final EbInterfaceValidator validator = Application.get ()
                                                        .getMetaData (Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
      validationResult = validator.validateXMLInstanceAgainstSchema (ebInterface);

      if (validationResult.getDeterminedEbInterfaceVersion () == null)
      {
        error ("Das konvertierte XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
        onError ();
        return;
      }

      final BaseRenderer renderer = new BaseRenderer ();
      try
      {
        LOGGER.debug ("Load ebInterface JasperReport template from application context.");
        final JasperReport jrReport = Application.get ().getMetaData (Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);

        LOGGER.debug ("Rendering PDF.");

        pdf = renderer.renderReport (jrReport, ebInterface, null);
      }
      catch (final Exception ex)
      {
        error ("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
        onError ();
        return;
      }
    }

    // Redirect
    setResponsePage (new ResultPageUblToEbi (pdf, ebInterface, sbLog.toString (), m_aReturnPage));
  }

  /**
   * Process errors
   */
  @Override
  protected void onError ()
  {
    // Show the feedback panel in case on an error
    m_aFeedbackPanel.setVisible (true);
  }
}
