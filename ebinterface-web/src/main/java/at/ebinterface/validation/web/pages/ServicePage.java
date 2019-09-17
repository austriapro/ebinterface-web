package at.ebinterface.validation.web.pages;

import org.apache.wicket.markup.html.panel.EmptyPanel;

import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.pages.resultpages.ResultPanel;

public class ServicePage extends BasePage {

  public ServicePage() {
    super();
    setup();
  }

  public ServicePage(ValidationResult validationResult, String selectedSchematronRule,
                     StartPage.ActionType selectedAction, byte[] pdf) {
    super();
    setup(validationResult, selectedSchematronRule, selectedAction, pdf);
  }

   private void setup() {
    setup(null, null, null, null);
   }

  private void setup(ValidationResult validationResult, String selectedSchematronRule,
                     StartPage.ActionType selectedAction, byte[] pdf) {

        //Add the input form
    final ServiceForm inputForm = new ServiceForm("inputForm");
    add(inputForm);

    if (validationResult != null) {
      add(new ResultPanel("resultPanel", validationResult, selectedSchematronRule, selectedAction, pdf, null, null, null));
    } else {
      add(new EmptyPanel("resultPanel"));
    }

  }
}
