package at.ebinterface.validation.web.pages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.ebinterface.EbInterface41Marshaller;
import com.helger.ebinterface.ubl.from.IToEbinterfaceSettings;
import com.helger.ebinterface.ubl.from.ToEbinterfaceSettings;
import com.helger.ebinterface.ubl.from.creditnote.CreditNoteToEbInterface41Converter;
import com.helger.ebinterface.ubl.from.invoice.InvoiceToEbInterface41Converter;
import com.helger.ebinterface.v41.Ebi41InvoiceType;
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;
import com.helger.ubl21.UBL21Reader;

import at.austriapro.rendering.BaseRenderer;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.resultpages.ResultPageEbInterface;
import net.sf.jasperreports.engine.JasperReport;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Form for showing the rules which are currently supported
 *
 * @author pl
 */
class UblForm extends Form<Object> {
  private static final Logger LOG = LoggerFactory.getLogger (UblForm.class);
  
  /**
   * Panel for providing feedback in case of erroneous input
   */
  FeedbackPanel feedbackPanel;

  /**
   * Upload field for the ebInterface instance
   */
  FileUploadField fileUploadField;

  public UblForm(final String id) {
    super(id);

    //Add a feedback panel
    feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
    feedbackPanel.setVisible(false);
    add(feedbackPanel);

    //Add the file upload field
    fileUploadField = new FileUploadField("ublInput");
    fileUploadField.setRequired(true);
    add(fileUploadField);

    //Add a submit button
    add(new SubmitLink("convertUbl"));
  }

  @Override
  protected void onSubmit() {
    super.onSubmit();

    feedbackPanel.setVisible(false);

    //Get the file input
    final FileUpload upload = fileUploadField.getFileUpload();
    byte[] uploadedData = null;

    try {
      final InputStream inputStream = upload.getInputStream();
      uploadedData = IOUtils.toByteArray(inputStream);
    } catch (final IOException e) {
      LOG.error("Die hochgeladene Datei kann nicht verarbeitet werden.", e);
    }

    final Locale aDisplayLocale = Locale.GERMANY;
    final Locale aContentLocale = Locale.GERMANY;

    // Read UBL
    ErrorList aReadErrors = new ErrorList ();
    final InvoiceType aUBLInvoice = UBL21Reader.invoice().setValidationEventHandler (new WrappedCollectingValidationEventHandler (aReadErrors)).read(uploadedData);
    final CreditNoteType aUBLCreditNote;
    if (aUBLInvoice == null)
      aUBLCreditNote = UBL21Reader.creditNote ().setValidationEventHandler (new WrappedCollectingValidationEventHandler (aReadErrors)).read (uploadedData);
    else
      aUBLCreditNote = null;

    if (aUBLInvoice == null && aUBLCreditNote == null){
      error(
          "Das UBL kann nicht verarbeitet werden. Es können nur UBL Invoice und CreditNote Dokumente verarbeitet werden.");
      // Log errors in case somebody cares
      for (final IError aError: aReadErrors.getAllFailures ())
        LOG.warn ("UBL parsing: " + aError.getAsString (aDisplayLocale));
      onError();
      return;
    }

    // Convert to ebInterface
    final IToEbinterfaceSettings aToEbiSettings = new ToEbinterfaceSettings ();
    final ErrorList aErrorList = new ErrorList ();
    final Ebi41InvoiceType aEb41Invoice;
    if (aUBLInvoice != null) {
      // It's an invoice
      aEb41Invoice = new InvoiceToEbInterface41Converter(aDisplayLocale, aContentLocale, aToEbiSettings).convertToEbInterface (aUBLInvoice, aErrorList);
    }
    else {
      // It' a credit note 
      aEb41Invoice = new CreditNoteToEbInterface41Converter(aDisplayLocale, aContentLocale, aToEbiSettings).convertToEbInterface (aUBLCreditNote, aErrorList);
    }
    
    byte[] ebInterface = null;
    ValidationResult validationResult = null;
    byte[] pdf = null;

    final StringBuilder sbLog = new StringBuilder();

    if(aErrorList.containsAtLeastOneError ()) {
      validationResult = new ValidationResult();
      validationResult.setSchemaValidationErrorMessage("Die Schemavalidierung konnte nicht durchgeführt werden.");

      sbLog.append("<b>Bei der UBL - ebInterfacekonvertierung sind folgende Fehler aufgetreten:</b><br/>");
      for (IError error : aErrorList.getAllErrors ()){
        sbLog.append(error.getErrorFieldName()).append(":<br/>").append(error.getErrorText(Locale.GERMANY)).append("<br/><br/>");
      }
    } else {
      ebInterface = new EbInterface41Marshaller().getAsBytes (aEb41Invoice);

      //Validate the XML instance - performed in any case
      final EbInterfaceValidator validator = Application.get().getMetaData(Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
      validationResult = validator.validateXMLInstanceAgainstSchema(ebInterface);

      if (validationResult.getDeterminedEbInterfaceVersion() == null) {
        error(
            "Das konvertierte XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
        onError();
        return;
      }

      BaseRenderer renderer = new BaseRenderer();

      try {
        LOG.debug("Load ebInterface JasperReport template from application context.");
        JasperReport
            jrReport =
            Application.get().getMetaData(Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);

        LOG.debug("Rendering PDF.");

        pdf = renderer.renderReport(jrReport, ebInterface, null);

      } catch (Exception ex) {
        error("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
        onError();
        return;
      }
    }

    String log = null;
    if (sbLog.length()>0){
      log = sbLog.toString();
    }

    //Redirect
    setResponsePage(new ResultPageEbInterface(validationResult, null, StartPage.ActionType.SCHEMA_VALIDATION, pdf, ebInterface, log));
  }


  /**
   * Process errors
   */
  @Override
  protected void onError() {
    //Show the feedback panel in case on an error
    feedbackPanel.setVisible(true);
  }
}
