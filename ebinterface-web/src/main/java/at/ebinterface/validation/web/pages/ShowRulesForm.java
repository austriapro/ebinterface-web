package at.ebinterface.validation.web.pages;

import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import at.ebinterface.validation.validator.Rule;
import at.ebinterface.validation.validator.Rules;

/**
 * Form for showing the rules which are currently supported
 *
 * @author pl
 */
class ShowRulesForm extends Form {

  /**
   * Panel for providing feedback in case of errorneous input
   */
  FeedbackPanel feedbackPanel;

  /**
   * Dropdown choice for the schmeatrno rules
   */
  DropDownChoice<Rule> rules;

  public ShowRulesForm(final String id) {
    super(id);

    //Add a feedback panel
    feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
    feedbackPanel.setVisible(false);
    add(feedbackPanel);

    //Add the drop down choice for the different rules which are currently supported
    rules =
        new DropDownChoice<Rule>("ruleSelector", Model.of(new Rule()), Rules.getRules(),
                                 new IChoiceRenderer<Rule>() {
                                   @Override
                                   public Object getDisplayValue(Rule object) {
                                     return object.getName();
                                   }

                                   @Override
                                   public String getIdValue(Rule object, int index) {
                                     return object.getName();
                                   }
                                 });

    add(rules);

    //Add a submit button
    add(new SubmitLink("showSchematron"));
  }

  @Override
  protected void onSubmit() {
    super.onSubmit();

    //Did the user select a schematron file?
    if (rules.getModelObject() == null || rules.getModelObject().toString().equals("")) {
      error(new ResourceModel("ruleSelector.NoSelected").getObject());
      onError();
      return;
    }

    //Redirect
    setResponsePage(new ShowRulesPage(rules.getModel()));
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
