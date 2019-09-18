package at.ebinterface.validation.web.pages;

import java.io.IOException;
import java.io.InputStream;

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

import com.helger.commons.string.StringHelper;

import at.austriapro.rendering.BaseRenderer;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.Constants;
import net.sf.jasperreports.engine.JasperReport;

/**
 * The input form class
 *
 * @author pl
 */
final class ServiceForm extends Form<Object> {
private static final Logger LOG = LoggerFactory.getLogger (ServiceForm.class);

  /**
   * Panel for providing feedback in case of errorneous input
   */
  private final FeedbackPanel feedbackPanel;

  /**
   * Upload field for the ebInterface instance
   */
  private final FileUploadField fileUploadField;

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
        submit(EBasicEbiActionType.VISUALIZATION_HTML);
      }
      // latest requirements: only pdf visualization
    }.setVisibilityAllowed(false));

    //Add a button to visualize it as PDF
    add(new SubmitLink("submitButtonVisualizePDF") {
      @Override
      public void onSubmit() {
        submit(EBasicEbiActionType.VISUALIZATION_PDF);
      }
    });

  }

  /**
   * Process the input
   */
  protected void submit(final EBasicEbiActionType selectedAction) {

    //Hide the feedback panel first (will be shown in case of an error)
    feedbackPanel.setVisible(false);

    //Schematron validation?
    //Schematron set must be selected

    byte[] pdf = null;

    //Get the file input
    final FileUpload upload = fileUploadField.getFileUpload();
    byte[] uploadedData = null;

    try {
      final InputStream inputStream = upload.getInputStream();
      uploadedData = IOUtils.toByteArray(inputStream);
    } catch (final IOException e) {
      LOG.error("Die hochgeladene Datei kann nicht verarbeitet werden.", e);
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
    if (selectedAction == EBasicEbiActionType.VISUALIZATION_HTML) {
      //Visualization is only possible for valid instances
      if (StringHelper.hasText(validationResult.getSchemaValidationErrorMessage())) {
        error(
            "Die gewählte ebInterface Instanz ist nicht valide. Es können nur valide Schemainstanzen in der Druckansicht angezeigt werden.");
        onError();
        return;
      }

      //Get the transformed string
      final String
          s =
          validator
              .transformInput(uploadedData, validationResult.getDeterminedEbInterfaceVersion().getVersion ());
      //Redirect to the printview page
      setResponsePage(new PrintViewPage(s));
      return;


    }
    //ebInterface PDF-Generation
    else if (selectedAction == EBasicEbiActionType.VISUALIZATION_PDF) {
      final BaseRenderer renderer = new BaseRenderer();

      try {
        LOG.debug("Load ebInterface JasperReport template from application context.");
        final JasperReport
            jrReport =
            Application.get().getMetaData(Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);

        LOG.debug("Rendering PDF from ebInterface file.");

        pdf = renderer.renderReport(jrReport, uploadedData, null);

      } catch (final Exception ex) {
        LOG.error("Error when generating PDF from ebInterface", ex);
        error("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
        onError();
        return;
      }
    }

    //Redirect to the ebInterface result page
    setResponsePage(
        new ServicePage(validationResult, pdf));
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
