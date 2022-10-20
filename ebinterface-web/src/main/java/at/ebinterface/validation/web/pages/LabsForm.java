package at.ebinterface.validation.web.pages;

import java.io.IOException;

import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;

import at.austriapro.rendering.BaseRenderer;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.resultpages.ResultPageEbInterface;
import net.sf.jasperreports.engine.JasperReport;

/**
 * The input form class for labs.ebinterface.at
 *
 * @author pl
 */
final class LabsForm extends Form <Object>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LabsForm.class);

  /**
   * Panel for providing feedback in case of erroneous input
   */
  private final FeedbackPanel feedbackPanel;

  /**
   * Upload field for the ebInterface instance
   */
  private final FileUploadField fileUploadField;

  public LabsForm (final String id)
  {
    super (id);

    // Set the form to multi part since we use file upload
    setMultiPart (true);

    // Add a feedback panel
    feedbackPanel = new FeedbackPanel ("feedback", new ContainerFeedbackMessageFilter (this));
    feedbackPanel.setVisible (false);
    add (feedbackPanel);

    // Add the file upload field
    fileUploadField = new FileUploadField ("fileInput");
    fileUploadField.setRequired (true);
    add (fileUploadField);

    // Add a second submit button
    add (new SubmitLink ("submitButtonSchemaOnly")
    {
      @Override
      public void onSubmit ()
      {
        submit (EBasicEbiActionType.SCHEMA_VALIDATION);
      }
    });

    // Add a button to visualize it as HTML
    add (new SubmitLink ("submitButtonVisualizeHTML")
    {
      @Override
      public void onSubmit ()
      {
        submit (EBasicEbiActionType.VISUALIZATION_HTML);
      }
    });

    // Add a button to visualize it as PDF
    add (new SubmitLink ("submitButtonVisualizePDF")
    {
      @Override
      public void onSubmit ()
      {
        submit (EBasicEbiActionType.VISUALIZATION_PDF);
      }
    });
  }

  /*
   * Process the input
   */
  protected void submit (final EBasicEbiActionType selectedAction)
  {
    // Hide the feedback panel first (will be shown in case of an error)
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

    // Validate the XML instance - performed in any case
    final EbInterfaceValidator validator = Application.get ().getMetaData (Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
    final ValidationResult validationResult = validator.validateXMLInstanceAgainstSchema (uploadedData);

    if (validationResult.getDeterminedEbInterfaceVersion () == null)
    {
      error ("Das XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
      onError ();
      return;
    }

    switch (selectedAction)
    {
      case SCHEMA_VALIDATION:
        // Redirect to the ebInterface result page
        setResponsePage (new ResultPageEbInterface (validationResult, null, null, null, LabsPage.class));
        break;
      case VISUALIZATION_HTML:
        // Visualization is only possible for valid instances
        if (StringHelper.hasText (validationResult.getSchemaValidationErrorMessage ()))
        {
          error ("Die gewählte ebInterface Instanz ist nicht valide. Es können nur valide Schemainstanzen in der Druckansicht angezeigt werden.");
          onError ();
        }
        else
        {
          // Get the transformed string
          final String s = validator.transformInput (uploadedData, validationResult.getDeterminedEbInterfaceVersion ().getVersion ());
          // Redirect to the printview page
          setResponsePage (new PrintViewPage (s));
        }
        break;
      case VISUALIZATION_PDF:
        final BaseRenderer renderer = new BaseRenderer ();
        try
        {
          LOGGER.debug ("Load ebInterface JasperReport template from application context.");
          final JasperReport jrReport = Application.get ().getMetaData (Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);
          LOGGER.debug ("Rendering PDF from ebInterface file.");
          final byte [] pdf = renderer.renderReport (jrReport, uploadedData, null);

          // Redirect to the ebInterface result page
          setResponsePage (new ResultPageEbInterface (validationResult, pdf, null, null, LabsPage.class));
        }
        catch (final Exception ex)
        {
          LOGGER.error ("Error when generating PDF from ebInterface", ex);
          error ("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
          onError ();
        }
        break;
      default:
        throw new IllegalStateException ();
    }
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
