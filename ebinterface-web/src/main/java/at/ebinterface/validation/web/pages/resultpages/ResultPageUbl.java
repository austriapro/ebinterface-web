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
  public ResultPageUbl (final byte [] xml, final String sErrorLog, Class <? extends WebPage> returnPage)
  {
    ResultPanelUbl resultPanel = new ResultPanelUbl ("resultPanelUbl",  xml, sErrorLog, returnPage);
    add (resultPanel);
  }
}
