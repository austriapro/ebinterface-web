package at.ebinterface.validation.web.pages.resultpages;

import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.pages.BasePage;
import at.ebinterface.validation.web.pages.LabsPage;
import at.ebinterface.validation.web.pages.StartPage.ActionType;

/**
 * Used to show the results of a validation
 *
 * @author pl
 */
public class ResultPageEbInterface extends BasePage {

  /**
   * Create a new result page
   */
  public ResultPageEbInterface(final ValidationResult validationResult,
                               final String selectedSchematron,
                               final ActionType selectedAction,
                               final byte[] pdf,
                               final byte[] xml,
                               final String log) {

    ResultPanel
        resultPanel =
        new ResultPanel("resultPanel", validationResult, selectedSchematron, selectedAction, pdf,
                        xml, log, LabsPage.class);

    add(resultPanel);


  }

  protected String getFooterClass() {
    return "footer-section navbar-fixed-bottom";
  }

}

