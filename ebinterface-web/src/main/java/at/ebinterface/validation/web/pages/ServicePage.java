package at.ebinterface.validation.web.pages;

import org.apache.wicket.markup.html.panel.EmptyPanel;

import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.pages.resultpages.ResultPanel;

public final class ServicePage extends BasePage {
  public ServicePage(final ValidationResult validationResult,
                     final byte[] pdf) {
    //Add the input form
    final ServiceForm inputForm = new ServiceForm("inputForm");
    add(inputForm);

    if (validationResult != null) {
      add(new ResultPanel("resultPanel", validationResult, pdf, null, null, null));
    } else {
      add(new EmptyPanel("resultPanel"));
    }
  }
}
