package at.ebinterface.validation.web.pages.schematron;

import java.io.IOException;

import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.stream.StreamHelper;
import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.Rule;
import at.ebinterface.validation.validator.Rules;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.validator.jaxb.Result;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.LabsPage;
import at.ebinterface.validation.web.pages.StartPage;
import at.ebinterface.validation.web.pages.schematron.result.ResultPageValidateRules;

/**
 * The input form class used on the startPage
 *
 * @author Philip Helger
 */
public final class ValidateRulesForm extends Form <Object>
{
  private static final Logger LOG = LoggerFactory.getLogger (ValidateRulesForm.class);

  /**
   * Panel for providing feedback in case of erroneous input
   */
  private FeedbackPanel feedbackPanel;

  /**
   * Dropdown choice for the Schematron rules
   */
  private DropDownChoice <Rule> rules;

  /**
   * Upload field for the ebInterface instance
   */
  private FileUploadField fileUploadField;

  private boolean m_bFromStartPage;

  public ValidateRulesForm (final String id, final boolean bFromStartPage)
  {
    super (id);

    m_bFromStartPage = bFromStartPage;

    // Set the form to multi part since we use file upload
    setMultiPart (true);

    // Add a feedback panel
    feedbackPanel = new FeedbackPanel ("validateRulesFeedback", new ContainerFeedbackMessageFilter (this));
    feedbackPanel.setVisible (false);
    add (feedbackPanel);

    // Add the file upload field
    fileUploadField = new FileUploadField ("validateRulesInput");
    fileUploadField.setRequired (true);
    add (fileUploadField);

    // Add the drop down choice for the different rules which are currently
    // supported
    rules = new DropDownChoice <> ("validateRulesSelector",
                                   Model.of (new Rule ()),
                                   Rules.getRules (),
                                   new IChoiceRenderer <Rule> ()
                                   {
                                     @Override
                                     public Object getDisplayValue (final Rule object)
                                     {
                                       return object.getName ();
                                     }

                                     @Override
                                     public String getIdValue (final Rule object, final int index)
                                     {
                                       return object.getName ();
                                     }
                                   });
    add (rules);

    // Add a submit button
    add (new SubmitLink ("validateRulesSubmit"));
  }

  @Override
  protected void onSubmit ()
  {
    // Hide the feedback panel first (will be shown in case of an error)
    feedbackPanel.setVisible (false);

    // Schematron set must be selected
    final Rule rule = rules.getModelObject ();
    if (rule == null)
    {
      error (new ResourceModel ("ruleSelector.Required").getObject ());
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
    catch (final IOException ex)
    {
      LOG.error ("Die hochgeladene Datei kann nicht verarbeitet werden.", ex);
    }

    // Validate the XML instance - performed in any case
    final EbInterfaceValidator validator = Application.get ()
                                                      .getMetaData (Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
    final ValidationResult validationResult = validator.validateXMLInstanceAgainstSchema (uploadedData);
    if (validationResult.getDeterminedEbInterfaceVersion () == null)
    {
      error ("Das XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
      onError ();
      return;
    }

    // Schematron validation may only be started in case of ebInterface 4p0
    final EEbInterfaceVersion eUploadedVersion = validationResult.getDeterminedEbInterfaceVersion ().getVersion ();
    if (eUploadedVersion == EEbInterfaceVersion.V40 ||
        eUploadedVersion == EEbInterfaceVersion.V41 ||
        eUploadedVersion == EEbInterfaceVersion.V42 ||
        eUploadedVersion == EEbInterfaceVersion.V43)
    {
      // Selected rule and selected ebInterface version must match
      if (!rule.getEbInterfaceVersion ().equals (eUploadedVersion))
      {
        error (new ResourceModel ("schematron.version.mismatch").getObject ());
        onError ();
        return;
      }

      // Invoke the validation
      final Result r = validator.validateXMLInstanceAgainstSchematron (uploadedData, rule.getFileReference ());
      validationResult.setSchematronResult (r);
    }
    else
    {
      // Wrong ebInterface version
      error ("Schematronregeln können nur auf ebInterface 4.0/4.1/4.2/4.3 Instanzen angewendet werden. Erkannte ebInterface Version ist jedoch: " +
             validationResult.getDeterminedEbInterfaceVersion ().getCaption ());
      onError ();
      return;
    }

    // Redirect to the ebInterface result page
    setResponsePage (new ResultPageValidateRules (validationResult,
                                                  rule.getName (),
                                                  m_bFromStartPage ? StartPage.class : LabsPage.class));
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
