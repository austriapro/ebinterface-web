package at.ebinterface.web2.app.layout;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.url.ISimpleURL;
import com.helger.css.property.CCSSProperties;
import com.helger.css.propertyvalue.CCSSValue;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.IHCElement;
import com.helger.html.hc.html.embedded.HCImg;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.html.textlevel.HCStrong;
import com.helger.photon.app.url.LinkHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbar;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarToggleable;
import com.helger.photon.bootstrap4.navbar.EBootstrapNavbarExpandType;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapMenuItemRenderer;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapMenuItemRendererHorz;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapPageRenderer;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.appid.CApplicationID;
import com.helger.photon.core.appid.PhotonGlobalState;
import com.helger.photon.core.execcontext.ILayoutExecutionContext;
import com.helger.photon.core.html.CLayout;
import com.helger.photon.core.menu.IMenuItemExternal;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuObject;
import com.helger.photon.core.menu.IMenuSeparator;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.core.menu.MenuItemDeterminatorCallback;
import com.helger.photon.core.servlet.AbstractSecureApplicationServlet;
import com.helger.photon.core.servlet.LogoutServlet;
import com.helger.photon.security.user.IUser;
import com.helger.photon.security.util.SecurityHelper;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import at.ebinterface.web2.app.CApp;
import at.ebinterface.web2.app.menu.CAppMenuPublic;

/**
 * The viewport renderer (menu + content area)
 *
 * @author Philip Helger
 */
public final class AppRendererPublic
{
  private static final ICSSClassProvider CSS_CLASS_FOOTER_LINKS = DefaultCSSClassProvider.create ("footer-links");

  private static final ICommonsList <IMenuObject> s_aFooterObjects = new CommonsArrayList <> ();

  static
  {
    PhotonGlobalState.state (CApplicationID.APP_ID_PUBLIC).getMenuTree ().iterateAllMenuObjects (aCurrentObject -> {
      if (aCurrentObject.attrs ().containsKey (CAppMenuPublic.FLAG_FOOTER))
        s_aFooterObjects.add (aCurrentObject);
    });
  }

  private AppRendererPublic ()
  {}

  @Nonnull
  static HCImg createImg ()
  {
    return new HCImg ().setSrc (LinkHelper.getURLWithContext ("/imgs/logo-50-50.png"))
                       .addStyle (CCSSProperties.MARGIN.newValue ("-15px"))
                       .addStyle (CCSSProperties.VERTICAL_ALIGN.newValue (CCSSValue.TOP))
                       .addStyle (CCSSProperties.PADDING.newValue ("0 6px"));
  }

  private static void _addNavbarLoginLogout (@Nonnull final ILayoutExecutionContext aLEC,
                                             @Nonnull final BootstrapNavbar aNavbar)
  {
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
    final IUser aUser = aLEC.getLoggedInUser ();

    final BootstrapNavbarToggleable aToggleable = aNavbar.addAndReturnToggleable ();

    if (aUser != null)
    {
      final Locale aDisplayLocale = aLEC.getDisplayLocale ();
      aToggleable.addChild (new BootstrapButton ().addChild ("Zur Verwaltung")
                                                  .addClass (CBootstrapCSS.ML_AUTO)
                                                  .setOnClick (LinkHelper.getURLWithContext (AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH)));
      aToggleable.addAndReturnText ()
                 .addChild ("Angemeldet als ")
                 .addChild (new HCStrong ().addChild (SecurityHelper.getUserDisplayName (aUser, aDisplayLocale)));
      aToggleable.addChild (new BootstrapButton ().setOnClick (LinkHelper.getURLWithContext (aRequestScope,
                                                                                             LogoutServlet.SERVLET_DEFAULT_PATH))
                                                  .addChild (EPhotonCoreText.LOGIN_LOGOUT.getDisplayText (aDisplayLocale)));
    }
    else
    {
      aToggleable.addChild (new BootstrapButton ().addChild ("Anmelden")
                                                  .addClass (CBootstrapCSS.ML_AUTO)
                                                  .setOnClick (LinkHelper.getURLWithContext (AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH)));
    }
  }

  @Nonnull
  private static BootstrapNavbar _getNavbar (@Nonnull final ILayoutExecutionContext aLEC)
  {
    final ISimpleURL aLinkToStartPage = aLEC.getLinkToMenuItem (aLEC.getMenuTree ().getDefaultMenuItemID ());

    final BootstrapNavbar aNavbar = new BootstrapNavbar ().setExpand (EBootstrapNavbarExpandType.EXPAND_MD);
    aNavbar.addBrand (createImg (), aLinkToStartPage);
    aNavbar.addBrand (new HCSpan ().addChild (CApp.APP_NAME), aLinkToStartPage);
    _addNavbarLoginLogout (aLEC, aNavbar);
    return aNavbar;
  }

  @Nonnull
  public static IHCNode getMenuContent (@Nonnull final ILayoutExecutionContext aLEC)
  {
    // Main menu
    final IMenuTree aMenuTree = aLEC.getMenuTree ();
    final MenuItemDeterminatorCallback aCallback = new MenuItemDeterminatorCallback (aMenuTree,
                                                                                     aLEC.getSelectedMenuItemID ())
    {
      @Override
      protected boolean isMenuItemValidToBeDisplayed (@Nonnull final IMenuObject aMenuObj)
      {
        // Don't show items that belong to the footer
        if (aMenuObj.attrs ().containsKey (CAppMenuPublic.FLAG_FOOTER))
          return false;

        // Use default code
        return super.isMenuItemValidToBeDisplayed (aMenuObj);
      }
    };
    final IHCElement <?> aMenu = BootstrapMenuItemRenderer.createSideBarMenu (aLEC, aCallback);

    return aMenu;
  }

  @Nonnull
  public static IHCNode getContent (@Nonnull final ILayoutExecutionContext aLEC)
  {
    final BootstrapContainer aOuterContainer = new BootstrapContainer ().setFluid (true);

    // Header
    aOuterContainer.addChild (_getNavbar (aLEC));

    // No Breadcrumbs

    // Content
    {
      final HCDiv aRow = aOuterContainer.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.D_MD_FLEX));
      final HCDiv aCol1 = aRow.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.D_MD_FLEX)
                                                              .addClass (CBootstrapCSS.MR_2));
      final HCDiv aCol2 = aRow.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.FLEX_FILL));

      // We need a wrapper span for easy AJAX content replacement
      aCol1.addClass (CBootstrapCSS.D_PRINT_NONE)
           .addChild (new HCSpan ().setID (CLayout.LAYOUT_AREAID_MENU).addChild (getMenuContent (aLEC)))
           .addChild (new HCDiv ().setID (CLayout.LAYOUT_AREAID_SPECIAL));

      // content - determine is exactly same as for view
      aCol2.addChild (BootstrapPageRenderer.getPageContent (aLEC));
    }

    // Footer
    {
      final HCDiv aDiv = new HCDiv ().addClass (CBootstrapCSS.CONTAINER_FLUID).setID (CLayout.LAYOUT_AREAID_FOOTER);

      aDiv.addChild (new HCP ().addChild ("Created by Philip for Karin"));

      final BootstrapMenuItemRendererHorz aRenderer = new BootstrapMenuItemRendererHorz (aLEC.getDisplayLocale ());
      final HCUL aUL = aDiv.addAndReturnChild (new HCUL ().addClass (CSS_CLASS_FOOTER_LINKS));
      for (final IMenuObject aMenuObj : s_aFooterObjects)
      {
        if (aMenuObj instanceof IMenuSeparator)
          aUL.addItem (aRenderer.renderSeparator (aLEC, (IMenuSeparator) aMenuObj));
        else
          if (aMenuObj instanceof IMenuItemPage)
            aUL.addItem (aRenderer.renderMenuItemPage (aLEC, (IMenuItemPage) aMenuObj, false, false, false));
          else
            if (aMenuObj instanceof IMenuItemExternal)
              aUL.addItem (aRenderer.renderMenuItemExternal (aLEC, (IMenuItemExternal) aMenuObj, false, false, false));
            else
              throw new IllegalStateException ("Unsupported menu object type!");
      }
      aOuterContainer.addChild (aDiv);
    }

    return aOuterContainer;
  }
}
