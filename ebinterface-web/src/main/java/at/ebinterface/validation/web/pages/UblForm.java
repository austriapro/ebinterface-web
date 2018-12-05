package at.ebinterface.validation.web.pages;

import com.helger.commons.errorlist.ErrorList;
import com.helger.commons.errorlist.IError;
import com.helger.ebinterface.EbInterface41Marshaller;
import com.helger.ebinterface.ubl.from.invoice.InvoiceToEbInterface41Converter;
import com.helger.ebinterface.v41.Ebi41InvoiceType;
import com.helger.ubl21.UBL21Reader;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import at.austriapro.rendering.BaseRenderer;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.resultpages.ResultPageEbInterface;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Form for showing the rules which are currently supported
 *
 * @author pl
 */
class UblForm extends Form {

  /**
   * Panel for providing feedback in case of errorneous input
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
      StartPage.LOG.error("Die hochgeladene Datei kann nicht verarbeitet werden.", e);
    }

    // Read UBL
    final InvoiceType aUBLInvoice = UBL21Reader.invoice().read(uploadedData);

    if (aUBLInvoice == null){
      error(
          "Das UBL kann nicht verarbeitet werden.");
      onError();
      return;
    }

    // Convert to ebInterface
    final ErrorList aErrorList = new ErrorList ();
    final Ebi41InvoiceType aEbInvoice = new InvoiceToEbInterface41Converter(Locale.GERMANY,
                                                                            Locale.GERMANY,
                                                                            false).convertToEbInterface (aUBLInvoice, aErrorList);
    byte[] ebInterface = null;
    ValidationResult
        validationResult = null;
    byte[] pdf = null;

    StringBuilder sbLog = new StringBuilder();

    if(aErrorList.hasErrorsOrWarnings()) {
      validationResult = new ValidationResult();
      validationResult.setSchemaValidationErrorMessage("Die Schemavalidierung konnte nicht durchgeführt werden.");

      sbLog.append("<b>Bei der UBL - ebInterfacekonvertierung sind folgende Fehler aufgetreten:</b><br/>");
      for (IError error : aErrorList.getAllItems()){
        sbLog.append(error.getErrorFieldName()).append(":<br/>").append(error.getErrorText()).append("<br/><br/>");
      }
    } else {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();

      new EbInterface41Marshaller().write(aEbInvoice, bo);

      ebInterface = bo.toByteArray();

      //Validate the XML instance - performed in any case
      final EbInterfaceValidator validator = Application.get().getMetaData(
          Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
      validationResult =
          validator.validateXMLInstanceAgainstSchema(ebInterface);

      if (validationResult.getDeterminedEbInterfaceVersion() == null) {
        error(
            "Das konvertierte XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
        onError();
        return;
      }

      BaseRenderer renderer = new BaseRenderer();

      try {
        StartPage.LOG.debug("Load ebInterface JasperReport template from application context.");
        JasperReport
            jrReport =
            Application.get().getMetaData(Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);

        StartPage.LOG.debug("Rendering PDF.");

        pdf = renderer.renderReport(jrReport, ebInterface, null);

      } catch (Exception ex) {
        error("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
        onError();
        return;
      }
    }

    String log = null;
    if (sbLog.toString().length()>0){
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
