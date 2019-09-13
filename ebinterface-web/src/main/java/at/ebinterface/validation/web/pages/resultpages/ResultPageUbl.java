package at.ebinterface.validation.web.pages.resultpages;

import javax.annotation.Nonnull;

import org.apache.wicket.markup.html.WebPage;

import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.web.pages.BasePage;

/**
 * Used to show the results of a transformation from ebInterface to UBL
 *
 * @author Philip Helger
 */
public class ResultPageUbl extends BasePage
{
  public ResultPageUbl (@Nonnull EEbInterfaceVersion eVersion, final byte [] xml, final String log, Class <? extends WebPage> returnPage)
  {
    ResultPanelUbl resultPanel = new ResultPanelUbl ("resultPanelUbl", eVersion, xml, log, returnPage);
    add (resultPanel);
  }
}
