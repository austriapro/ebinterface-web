package at.ebinterface.validation.web.pages;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import at.austriapro.rendering.BaseRenderer;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.resultpages.ResultPageEbInterface;

/**
 * The input form class
 *
 * @author pl
 */
class ServiceForm extends Form {


  /**
   * Panel for providing feedback in case of errorneous input
   */
  FeedbackPanel feedbackPanel;

  /**
   * Upload field for the ebInterface instance
   */
  FileUploadField fileUploadField;

  public ServiceForm(final String id) {
    super(id);

    //Set the form to multi part since we use file upload
    setMultiPart(true);

    //Add a feedback panel
    feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
    feedbackPanel.setVisible(false);
    add(feedbackPanel);

    //Add the file upload field
    fileUploadField = new FileUploadField("fileInput");
    fileUploadField.setRequired(true);
    add(fileUploadField);

    //Add a button to visualize it as HTML
    add(new SubmitLink("submitButtonVisualizeHTML") {
      @Override
      public void onSubmit() {
        submit(StartPage.ActionType.VISUALIZATION_HTML);
      }
    });

    //Add a button to visualize it as PDF
    add(new SubmitLink("submitButtonVisualizePDF") {
      @Override
      public void onSubmit() {
        submit(StartPage.ActionType.VISUALIZATION_PDF);
      }
    });

  }

  /**
   * Process the input
   */
  protected void submit(final StartPage.ActionType selectedAction) {

    //Hide the feedback panel first (will be shown in case of an error)
    feedbackPanel.setVisible(false);

    //Schematron validation?
    //Schematron set must be selected

    byte[] pdf = null;
    byte[] zugferd = null;
    StringBuilder sbLog = new StringBuilder();

    //Get the file input
    final FileUpload upload = fileUploadField.getFileUpload();
    byte[] uploadedData = null;

    try {
      final InputStream inputStream = upload.getInputStream();
      uploadedData = IOUtils.toByteArray(inputStream);
    } catch (final IOException e) {
      StartPage.LOG.error("Die hochgeladene Datei kann nicht verarbeitet werden.", e);
    }

    //Validate the XML instance - performed in any case
    final EbInterfaceValidator validator = Application.get().getMetaData(
        Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
    final ValidationResult
        validationResult =
        validator.validateXMLInstanceAgainstSchema(uploadedData);

    if (validationResult.getDeterminedEbInterfaceVersion() == null) {
      error(
          "Das XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
      onError();
      return;
    }

    //Schematron validation too?
    //Visualization HTML?
    if (selectedAction == StartPage.ActionType.VISUALIZATION_HTML) {
      //Visualization is only possible for valid instances
      if (!StringUtils.isEmpty(validationResult.getSchemaValidationErrorMessage())) {
        error(
            "Die gewählte ebInterface Instanz ist nicht valide. Es können nur valide Schemainstanzen in der Druckansicht angezeigt werden.");
        onError();
        return;
      }

      //Get the transformed string
      final String
          s =
          validator
              .transformInput(uploadedData, validationResult.getDeterminedEbInterfaceVersion());
      //Redirect to the printview page
      setResponsePage(new PrintViewPage(s));
      return;


    }
    //ebInterface PDF-Generation
    else if (selectedAction == StartPage.ActionType.VISUALIZATION_PDF) {
      BaseRenderer renderer = new BaseRenderer();

      try {
        StartPage.LOG.debug("Load ebInterface JasperReport template from application context.");
        JasperReport
            jrReport =
            Application.get().getMetaData(Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);

        StartPage.LOG.debug("Rendering PDF from ebInterface file.");

        pdf = renderer.renderReport(jrReport, uploadedData, null);

      } catch (Exception ex) {
        StartPage.LOG.error("Error when generating PDF from ebInterface", ex);
        error("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
        onError();
        return;
      }
    }

    String selectedSchematronRule = "";

    //Redirect to the ebInterface result page
    setResponsePage(
        new ResultPageEbInterface(validationResult, selectedSchematronRule, selectedAction,
                                  pdf, null, null));

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
