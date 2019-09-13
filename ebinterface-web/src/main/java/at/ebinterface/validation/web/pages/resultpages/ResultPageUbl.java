package at.ebinterface.validation.web.pages.resultpages;

import org.apache.wicket.markup.html.WebPage;

import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of a transformation from ebInterface to UBL
 *
 * @author Philip Helger
 */
public class ResultPageUbl extends BasePage
{
  /*
   * Create a new result page
   */
  public ResultPageUbl (final byte [] xml, final String log, Class <? extends WebPage> returnPage)
  {
    ResultPanelUbl resultPanel = new ResultPanelUbl ("resultPanel", xml, log, returnPage);
    add (resultPanel);
  }
}
