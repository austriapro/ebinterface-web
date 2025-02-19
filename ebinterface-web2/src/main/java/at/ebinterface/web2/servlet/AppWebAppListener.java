package at.ebinterface.web2.servlet;

import javax.annotation.Nonnull;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.vendor.VendorInfo;
import com.helger.html.hc.config.HCConversionSettings;
import com.helger.html.hc.config.HCSettings;
import com.helger.html.hc.ext.HCCustomizerAutoFocusFirstCtrl;
import com.helger.html.hc.impl.HCCustomizerList;
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jscode.JSAssocArray;
import com.helger.html.resource.css.ICSSPathProvider;
import com.helger.html.resource.js.IJSPathProvider;
import com.helger.photon.ajax.IAjaxRegistry;
import com.helger.photon.app.html.PhotonCSS;
import com.helger.photon.app.html.PhotonJS;
import com.helger.photon.bootstrap4.BootstrapCustomConfig;
import com.helger.photon.bootstrap4.ext.BootstrapSystemMessage;
import com.helger.photon.bootstrap4.servlet.BootstrapCustomizer;
import com.helger.photon.bootstrap4.servlet.WebAppListenerBootstrap;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.appid.CApplicationID;
import com.helger.photon.core.appid.PhotonGlobalState;
import com.helger.photon.core.configfile.ConfigurationFileManager;
import com.helger.photon.core.locale.ILocaleManager;
import com.helger.photon.core.menu.MenuTree;
import com.helger.photon.core.requestparam.RequestParameterHandlerURLPathNamed;
import com.helger.photon.core.requestparam.RequestParameterManager;
import com.helger.photon.core.servlet.AbstractPublicApplicationServlet;
import com.helger.photon.uicore.EUICoreJSPathProvider;
import com.helger.photon.uictrls.autonumeric.AbstractHCAutoNumeric;
import com.helger.photon.uictrls.datatables.DataTablesLengthMenu;
import com.helger.photon.uictrls.datatables.EDataTablesFilterType;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTables;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTablesI18N;
import com.helger.photon.uictrls.datatables.plugins.DataTablesPluginSearchHighlight;
import com.helger.photon.uictrls.famfam.EFamFamIcon;
import com.helger.scope.singleton.SingletonHelper;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.xservlet.requesttrack.RequestTrackerSettings;

import at.ebinterface.web2.app.AppConfig;
import at.ebinterface.web2.app.AppInternalErrorHandler;
import at.ebinterface.web2.app.AppMenuPublic;
import at.ebinterface.web2.app.CApp;
import at.ebinterface.web2.app.CAppAjax;
import at.ebinterface.web2.app.DefaultSecurity;
import jakarta.servlet.ServletContext;

/**
 * Callbacks for the application server
 *
 * @author Philip Helger
 */
public final class AppWebAppListener extends WebAppListenerBootstrap
{
  private static final DataTablesLengthMenu LENGTH_MENU = new DataTablesLengthMenu ().addItem (25)
                                                                                     .addItem (50)
                                                                                     .addItem (100)
                                                                                     .addItemAll ();

  @Override
  protected String getInitParameterDebug (@Nonnull final ServletContext aSC)
  {
    return AppConfig.getGlobalDebug ();
  }

  @Override
  protected String getInitParameterProduction (@Nonnull final ServletContext aSC)
  {
    return AppConfig.getGlobalProduction ();
  }

  @Override
  protected String getDataPath (@Nonnull final ServletContext aSC)
  {
    return AppConfig.getDataPath ();
  }

  @Override
  protected boolean shouldCheckFileAccess (@Nonnull final ServletContext aSC)
  {
    return AppConfig.isCheckFileAccess ();
  }

  @Override
  protected void beforeContextInitialized (final ServletContext aSC)
  {
    // Logging: JUL to SLF4J
    SLF4JBridgeHandler.removeHandlersForRootLogger ();
    SLF4JBridgeHandler.install ();

    if (GlobalDebug.isDebugMode ())
    {
      // Disable in debug mode
      RequestTrackerSettings.setLongRunningRequestsCheckEnabled (false);
      RequestTrackerSettings.setParallelRunningRequestsCheckEnabled (false);
    }
  }

  @Override
  protected void initGlobalSettings ()
  {
    // Set new customizer only if the default customizer is present
    if (HCConversionSettings.isDefaultCustomizer (HCSettings.getConversionSettings ().getCustomizer ()))
    {
      // Special Bootstrap customizer
      HCSettings.getMutableConversionSettings ()
                .setCustomizer (new HCCustomizerList (new BootstrapCustomizer (),
                                                      new HCCustomizerAutoFocusFirstCtrl ()));
    }

    // Set default icon set if none is defined
    EFamFamIcon.setAsDefault ();

    // Never use a thousand separator in HCAutoNumeric fields because of
    // parsing problems
    AbstractHCAutoNumeric.setDefaultThousandSeparator ("");

    // Add default mapping from Application ID to path
    PhotonGlobalState.state (CApplicationID.APP_ID_PUBLIC)
                     .setServletPath (AbstractPublicApplicationServlet.SERVLET_DEFAULT_PATH);
    PhotonGlobalState.getInstance ().setDefaultApplicationID (CApplicationID.APP_ID_PUBLIC);

    // Internal stuff:
    VendorInfo.setInceptionYear (2024);

    // Avoid startup error logs
    SingletonHelper.setDebugConsistency (false);
  }

  @Override
  protected void initLocales (@Nonnull final ILocaleManager aLocaleMgr)
  {
    aLocaleMgr.registerLocale (CApp.DEFAULT_LOCALE);
    aLocaleMgr.setDefaultLocale (CApp.DEFAULT_LOCALE);
  }

  @Override
  protected void initAjax (@Nonnull final IAjaxRegistry aAjaxRegistry)
  {
    aAjaxRegistry.registerFunction (CAppAjax.DATATABLES);
    aAjaxRegistry.registerFunction (CAppAjax.DATATABLES_I18N);
  }

  @Override
  protected void initMenu ()
  {
    // Create all menu items
    {
      final MenuTree aMenuTree = new MenuTree ();
      AppMenuPublic.init (aMenuTree);
      PhotonGlobalState.state (CApplicationID.APP_ID_PUBLIC).setMenuTree (aMenuTree);
    }
  }

  @Override
  protected void initSecurity ()
  {
    // Set all security related stuff
    DefaultSecurity.init ();
  }

  @Override
  protected void initManagers ()
  {
    RequestParameterManager.getInstance ().setParameterHandler (new RequestParameterHandlerURLPathNamed ());
    AppInternalErrorHandler.doSetup ();

    ConfigurationFileManager.getInstance ().registerAll (AppConfig.getConfig ());
  }

  @Override
  protected void initUI ()
  {
    // UI stuff
    BootstrapDataTables.setConfigurator ( (aLEC, aTable, aDataTables) -> {
      final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
      aDataTables.setAutoWidth (false)
                 .setLengthMenu (LENGTH_MENU)
                 .setAjaxBuilder (new JQueryAjaxBuilder ().url (CAppAjax.DATATABLES.getInvocationURL (aRequestScope))
                                                          .data (new JSAssocArray ().add (AjaxExecutorDataTables.OBJECT_ID,
                                                                                          aTable.getID ())))
                 .setServerFilterType (EDataTablesFilterType.ALL_TERMS_PER_ROW)
                 .setTextLoadingURL (CAppAjax.DATATABLES_I18N.getInvocationURL (aRequestScope),
                                     AjaxExecutorDataTablesI18N.REQUEST_PARAM_LANGUAGE_ID)
                 .addPlugin (new DataTablesPluginSearchHighlight ());
    });

    // By default allow markdown in system message
    BootstrapSystemMessage.setDefaultUseMarkdown (true);

    // Global CSS
    for (final ICSSPathProvider aPP : BootstrapCustomConfig.getAllBootstrapCSS ())
      PhotonCSS.registerCSSIncludeForGlobal (aPP);
    PhotonCSS.readCSSIncludesForGlobal (new ClassPathResource (PhotonCSS.DEFAULT_FILENAME));

    // Global JS
    PhotonJS.registerJSIncludeForGlobal (EUICoreJSPathProvider.JQUERY_3);
    for (final IJSPathProvider aPP : BootstrapCustomConfig.getAllBootstrapJS ())
      PhotonJS.registerJSIncludeForGlobal (aPP);
    PhotonJS.readJSIncludesForGlobal (new ClassPathResource (PhotonJS.DEFAULT_FILENAME));
  }
}
