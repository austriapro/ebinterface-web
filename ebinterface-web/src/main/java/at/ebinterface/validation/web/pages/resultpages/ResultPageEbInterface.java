package at.ebinterface.validation.web.pages.resultpages;

import org.apache.wicket.markup.html.WebPage;

import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of a validation
 *
 * @author pl
 */
public final class ResultPageEbInterface extends BasePage {

  public ResultPageEbInterface(final ValidationResult validationResult,
                               final byte[] pdf,
                               final byte[] xml,
                               final String log,
                               final Class<? extends WebPage> returnPage) {

    final ResultPanel resultPanel =
        new ResultPanel("resultPanel", validationResult, pdf,
                        xml, log, returnPage);
    add(resultPanel);
  }
}

