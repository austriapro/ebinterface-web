package at.ebinterface.validation.web.pages.schematron;

import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import com.helger.commons.string.StringHelper;

import at.ebinterface.validation.validator.Rule;
import at.ebinterface.validation.validator.Rules;
import at.ebinterface.validation.web.components.AbstractChoiceRenderer;
import at.ebinterface.validation.web.pages.LabsPage;
import at.ebinterface.validation.web.pages.StartPage;

/**
 * Form for showing the rules which are currently supported
 *
 * @author pl
 */
public final class ShowRulesForm extends Form <Object>
{

  /**
   * Panel for providing feedback in case of erroneous input
   */
  private FeedbackPanel feedbackPanel;

  /**
   * Dropdown choice for the Schematron rules
   */
  private DropDownChoice <Rule> rules;

  private final boolean m_bIsStartPage;

  public ShowRulesForm (final String id, final boolean bIsStartPage)
  {
    super (id);

    m_bIsStartPage = bIsStartPage;

    // Add a feedback panel
    feedbackPanel = new FeedbackPanel ("feedback", new ContainerFeedbackMessageFilter (this));
    feedbackPanel.setVisible (false);
    add (feedbackPanel);

    // Add the drop down choice for the different rules which are currently
    // supported
    rules = new DropDownChoice <> ("ruleSelector",
                                   Model.of (new Rule ()),
                                   Rules.getRules (),
                                   new AbstractChoiceRenderer <Rule> ()
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
    add (new SubmitLink ("showSchematron"));
  }

  @Override
  protected void onSubmit ()
  {
    super.onSubmit ();

    // Did the user select a schematron file?
    final Rule rule = rules.getModelObject ();
    if (rule == null || StringHelper.hasNoText (rule.toString ()))
    {
      error (new ResourceModel ("ruleSelector.NoSelected").getObject ());
      onError ();
      return;
    }

    // Redirect
    setResponsePage (new ShowRulesPage (rule, m_bIsStartPage ? StartPage.class : LabsPage.class));
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
