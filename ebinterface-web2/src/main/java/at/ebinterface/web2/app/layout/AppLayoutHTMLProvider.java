package at.ebinterface.web2.app.layout;

import java.util.Locale;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.metadata.EHCLinkType;
import com.helger.html.hc.html.metadata.HCHead;
import com.helger.html.hc.html.metadata.HCLink;
import com.helger.html.hc.html.metadata.HCMeta;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.photon.app.url.LinkHelper;
import com.helger.photon.core.appid.RequestSettings;
import com.helger.photon.core.execcontext.ISimpleWebExecutionContext;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.photon.core.html.AbstractSWECHTMLProvider;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.xservlet.forcedredirect.ForcedRedirectException;

import at.ebinterface.web2.app.CApp;

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

  /**
   * Fill the HTML HEAD element.
   *
   * @param aHtml
   *        The HTML object to be filled.
   */
  @Override
  @OverrideOnDemand
  protected void fillHead (@Nonnull final ISimpleWebExecutionContext aSWEC, @Nonnull final HCHtml aHtml)
  {
    super.fillHead (aSWEC, aHtml);

    final HCHead aHead = aHtml.head ();
    for (final String sLength : new String [] { "57", "64", "72", "76", "114", "120", "144", "152", "180" })
    {
      final String s2 = sLength + "x" + sLength;
      aHead.links ()
           .add (new HCLink ().setRel (EHCLinkType.APPLE_TOUCH_ICON)
                              .setSizes (s2)
                              .setHref (LinkHelper.getURLWithContext ("/apple-icon-" + s2 + ".png")));
    }
    aHead.links ()
         .add (new HCLink ().setRel (EHCLinkType.ICON)
                            .setType (CMimeType.IMAGE_PNG)
                            .setSizes ("192x192")
                            .setHref (LinkHelper.getURLWithContext ("/android-icon-192x192.png")));
    for (final String sLength : new String [] { "32", "96", "16" })
    {
      final String s2 = sLength + "x" + sLength;
      aHead.links ()
           .add (new HCLink ().setRel (EHCLinkType.ICON)
                              .setType (CMimeType.IMAGE_PNG)
                              .setSizes (s2)
                              .setHref (LinkHelper.getURLWithContext ("/favicon-" + s2 + ".png")));
    }
    aHead.links ()
         .add (new HCLink ().setRel (EHCLinkType.MANIFEST).setHref (LinkHelper.getURLWithContext ("/manifest.json")));
    aHead.metaElements ().add (new HCMeta ().setName ("msapplication-TileColor").setContent ("#ffffff"));
    aHead.metaElements ()
         .add (new HCMeta ().setName ("msapplication-TileImage")
                            .setContent (LinkHelper.getURIWithContext ("/ms-icon-144x144.png")));
    aHead.metaElements ().add (new HCMeta ().setName ("theme-color").setContent ("#ffffff"));
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
    aHead.setPageTitle (StringHelper.getConcatenatedOnDemand (CApp.APP_NAME,
                                                              " - ",
                                                              aMenuItem.getDisplayText (aDisplayLocale)));

    final IHCNode aNode = m_aFactory.apply (aLEC);
    aBody.addChild (aNode);
  }
}
