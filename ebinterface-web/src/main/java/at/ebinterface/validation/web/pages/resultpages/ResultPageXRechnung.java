package at.ebinterface.validation.web.pages.resultpages;

import org.apache.wicket.markup.html.WebPage;

import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of a transformation from ebInterface to XRechnung
 *
 * @author Philip Helger
 */
public class ResultPageXRechnung extends BasePage
{
  public ResultPageXRechnung (final byte [] xml, final String sErrorLog, Class <? extends WebPage> returnPage)
  {
    ResultPanelXRechnung resultPanel = new ResultPanelXRechnung ("resultPanelXRechnung", xml, sErrorLog, returnPage);
    add (resultPanel);
  }
}
