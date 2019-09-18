package at.ebinterface.validation.web.pages.convert.result;

import org.apache.wicket.markup.html.WebPage;

import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of the conversion from UBL to ebInterface
 *
 * @author Philip Helger
 */
public final class ResultPageUblToEbi extends BasePage
{
  public ResultPageUblToEbi (final byte [] pdf,
                             final byte [] xml,
                             final String log,
                             final Class <? extends WebPage> returnPage)
  {
    final ResultPanelUblToEbi resultPanel = new ResultPanelUblToEbi ("ublToEbiResultPanel", pdf, xml, log, returnPage);
    add (resultPanel);
  }
}
