package at.ebinterface.validation.web.pages.convert.result;

import org.apache.wicket.markup.html.WebPage;

import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of a transformation from ebInterface to XRechnung
 *
 * @author Philip Helger
 */
public final class ResultPageEbiToXRechnung extends BasePage
{
  public ResultPageEbiToXRechnung (final byte [] xml, final String sErrorLog, final Class <? extends WebPage> returnPage)
  {
    final ResultPanelEbiToXRechnung resultPanel = new ResultPanelEbiToXRechnung ("ebiToXRechnungResultPanel", xml, sErrorLog, returnPage);
    add (resultPanel);
  }
}
