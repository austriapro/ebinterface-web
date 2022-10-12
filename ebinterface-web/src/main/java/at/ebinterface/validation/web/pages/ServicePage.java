package at.ebinterface.validation.web.pages;

import javax.annotation.Nullable;

import org.apache.wicket.markup.html.panel.EmptyPanel;

import com.helger.commons.annotation.UsedViaReflection;

import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.pages.resultpages.ResultPanel;

public final class ServicePage extends BasePage
{
  /**
   * Default constructor for initial showing.
   */
  @UsedViaReflection
  public ServicePage ()
  {
    // Add the input form
    final ServiceForm inputForm = new ServiceForm ("inputForm");
    add (inputForm);

    add (new EmptyPanel ("resultPanel"));
  }

  /**
   * Constructor for the result page
   *
   * @param validationResult
   *        Validation result
   * @param pdf
   *        Created PDF
   */
  public ServicePage (final ValidationResult validationResult, @Nullable final byte [] pdf)
  {
    // Add the input form
    final ServiceForm inputForm = new ServiceForm ("inputForm");
    add (inputForm);

    if (validationResult != null)
      add (new ResultPanel ("resultPanel", validationResult, pdf, null, null, null));
    else
      add (new EmptyPanel ("resultPanel"));
  }
}
