package at.ebinterface.web2.ui;

import javax.annotation.Nonnull;

import com.helger.commons.url.SimpleURL;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.embedded.HCImg;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.sections.HCHeader;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.grid.BootstrapCol;
import com.helger.photon.bootstrap4.grid.BootstrapRow;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapPageRenderer;
import com.helger.photon.core.execcontext.ILayoutExecutionContext;

/**
 * The viewport renderer (menu + content area)
 *
 * @author Philip Helger
 */
public final class AppRendererPublic
{
  private static final ICSSClassProvider CSS_CLASS_CONTENT = DefaultCSSClassProvider.create ("content");
  private static final ICSSClassProvider CSS_CLASS_CONTENT_INSIDE = DefaultCSSClassProvider.create ("content-inside");
  private static final ICSSClassProvider CSS_CLASS_TOP_SECTION = DefaultCSSClassProvider.create ("top-section");
  private static final ICSSClassProvider CSS_CLASS_HEADER = DefaultCSSClassProvider.create ("header");
  private static final ICSSClassProvider CSS_CLASS_HEADER_CONTENT = DefaultCSSClassProvider.create ("header__content");
  private static final ICSSClassProvider CSS_CLASS_HEADER_CONTENT_FLEX = DefaultCSSClassProvider.create ("header__content__flex");
  private static final ICSSClassProvider CSS_CLASS_HEADER_LOGO = DefaultCSSClassProvider.create ("header__logo");

  private static final ICSSClassProvider CSS_CLASS_FOOTER = DefaultCSSClassProvider.create ("footer");
  private static final ICSSClassProvider CSS_CLASS_FOOTER_INNER = DefaultCSSClassProvider.create ("footer-inner");

  private AppRendererPublic ()
  {}

  @Nonnull
  public static IHCNode getContent (@Nonnull final ILayoutExecutionContext aLEC)
  {
    final HCNodeList aOuterContainer = new HCNodeList ();

    final HCDiv aContent = aOuterContainer.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_CONTENT));
    final HCDiv aContentInside = aContent.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_CONTENT_INSIDE));
    {
      // Header
      final HCDiv aTopSection = aContentInside.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_TOP_SECTION));
      final HCHeader aHeader = aTopSection.addAndReturnChild (new HCHeader ());
      final HCDiv aLocationInfo = aHeader.addAndReturnChild (new HCDiv ().setID ("location-info"));
      final HCDiv aHeaderDiv = aLocationInfo.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_HEADER));
      final BootstrapContainer aContainer = aHeaderDiv.addAndReturnChild (new BootstrapContainer ().addClass (CSS_CLASS_HEADER_CONTENT));
      final BootstrapRow aRow = aContainer.addAndReturnChild (new BootstrapRow ());

      {
        final BootstrapCol aCol = aRow.createColumn (6, 9, -1, -1, -1);
        aCol.addClass (CSS_CLASS_HEADER_CONTENT_FLEX);
        final HCDiv aLogo = aCol.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_HEADER_LOGO));
        aLogo.addChild (new HCA (new SimpleURL ("https://www.wko.at/service/Startseite.html")).addChild (HCImg.create (new SimpleURL ("/imgs/wko-logo.png"))
                                                                                                              .setAlt ("WKO Logo")));
      }
      {
        final BootstrapCol aCol = aRow.createColumn (6, 3, -1, -1, -1);
        final HCDiv aLogo = aCol.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_HEADER_LOGO));
        aLogo.addChild (HCImg.create (new SimpleURL ("/imgs/ebinterface-logo.png")).setAlt ("ebInterface Logo"));
      }
    }

    // Main content
    {
      final BootstrapContainer aContainer = aContentInside.addAndReturnChild (new BootstrapContainer ());
      aContainer.addChild (BootstrapPageRenderer.getPageContent (aLEC));
    }

    // Footer
    {
      final HCDiv aFooter = aOuterContainer.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_FOOTER));
      final HCDiv aFooterInner = aFooter.addAndReturnChild (new HCDiv ().addClass (CSS_CLASS_FOOTER_INNER));

      aFooterInner.addChild (new HCA (new SimpleURL ("https://www.wko.at/service/netzwerke/austriapro-impressum.html")).setTargetBlank ()
                                                                                                                       .addChild ("Offenlegung"))
                  .addChild (" | ")
                  .addChild (new HCA (new SimpleURL ("dataprivacy.html")).addChild ("Datenschutz"))
                  .addChild (" | © 2025 WKO | Technische Umsetzung durch Philip Helger");
    }

    return aOuterContainer;
  }
}
