package at.ebinterface.web2.ui;

import java.util.Locale;
import java.util.function.Function;

import com.helger.base.string.StringHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.metadata.HCHead;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.photon.core.appid.RequestSettings;
import com.helger.photon.core.execcontext.ISimpleWebExecutionContext;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.photon.core.html.AbstractSWECHTMLProvider;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.xservlet.forcedredirect.ForcedRedirectException;

import at.ebinterface.web2.app.CApp;
import jakarta.annotation.Nonnull;

/**
 * Main class for creating HTML output
 *
 * @author Philip Helger
 */
public class AppLayoutHTMLProvider extends AbstractSWECHTMLProvider
{
  private final Function <LayoutExecutionContext, IHCNode> m_aFactory;

  public AppLayoutHTMLProvider (@Nonnull final Function <LayoutExecutionContext, IHCNode> aFactory)
  {
    m_aFactory = aFactory;
  }

  @Override
  protected void fillBody (@Nonnull final ISimpleWebExecutionContext aSWEC,
                           @Nonnull final HCHtml aHtml) throws ForcedRedirectException
  {
    final IRequestWebScopeWithoutResponse aRequestScope = aSWEC.getRequestScope ();
    final Locale aDisplayLocale = aSWEC.getDisplayLocale ();
    final IMenuItemPage aMenuItem = RequestSettings.getMenuItem (aRequestScope);
    final LayoutExecutionContext aLEC = new LayoutExecutionContext (aSWEC, aMenuItem);
    final HCHead aHead = aHtml.head ();
    final HCBody aBody = aHtml.body ();

    // Add menu item in page title
    aHead.setPageTitle (StringHelper.getConcatenatedOnDemand (CApp.getAppName (),
                                                              " - ",
                                                              aMenuItem.getDisplayText (aDisplayLocale)));

    final IHCNode aNode = m_aFactory.apply (aLEC);
    aBody.addChild (aNode);
  }
}
