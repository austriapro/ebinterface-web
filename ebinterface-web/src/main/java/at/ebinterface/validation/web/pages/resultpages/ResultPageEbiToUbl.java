package at.ebinterface.validation.web.pages.resultpages;

import org.apache.wicket.markup.html.WebPage;

import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of a transformation from ebInterface to UBL
 *
 * @author Philip Helger
 */
public final class ResultPageEbiToUbl extends BasePage
{
  public ResultPageEbiToUbl (final byte [] xml, final String sErrorLog, final Class <? extends WebPage> returnPage)
  {
    final ResultPanelEbiToUbl resultPanel = new ResultPanelEbiToUbl ("ebiToUblResultPanel", xml, sErrorLog, returnPage);
    add (resultPanel);
  }
}
