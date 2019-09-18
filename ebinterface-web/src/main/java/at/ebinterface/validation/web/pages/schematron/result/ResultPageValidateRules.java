package at.ebinterface.validation.web.pages.schematron.result;

import org.apache.wicket.markup.html.WebPage;

import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of a Schematron validation
 *
 * @author Philip Helger
 */
public final class ResultPageValidateRules extends BasePage
{
  public ResultPageValidateRules (final ValidationResult validationResult,
                                  final String selectedSchematron,
                                  final Class <? extends WebPage> returnPage)
  {
    final ResultPanelValidateRules resultPanel = new ResultPanelValidateRules ("validateRulesResultPanel",
                                                                               validationResult,
                                                                               selectedSchematron,
                                                                               returnPage);
    add (resultPanel);
  }
}
