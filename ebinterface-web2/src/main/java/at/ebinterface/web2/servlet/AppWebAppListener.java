package at.ebinterface.web2.servlet;

import javax.annotation.Nonnull;

import com.helger.commons.vendor.VendorInfo;
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jscode.JSAssocArray;
import com.helger.photon.ajax.IAjaxRegistry;
import com.helger.photon.bootstrap4.ext.BootstrapSystemMessage;
import com.helger.photon.bootstrap4.servlet.WebAppListenerBootstrap;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.appid.CApplicationID;
import com.helger.photon.core.appid.PhotonGlobalState;
import com.helger.photon.core.configfile.ConfigurationFileManager;
import com.helger.photon.core.locale.ILocaleManager;
import com.helger.photon.core.menu.MenuTree;
import com.helger.photon.core.requestparam.RequestParameterHandlerURLPathNamed;
import com.helger.photon.core.requestparam.RequestParameterManager;
import com.helger.photon.uictrls.datatables.DataTablesLengthMenu;
import com.helger.photon.uictrls.datatables.EDataTablesFilterType;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTables;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTablesI18N;
import com.helger.photon.uictrls.datatables.plugins.DataTablesPluginSearchHighlight;
import com.helger.scope.singleton.SingletonHelper;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import at.ebinterface.web2.app.AppConfig;
import at.ebinterface.web2.app.AppInternalErrorHandler;
import at.ebinterface.web2.app.CApp;
import at.ebinterface.web2.app.CAppAjax;
import at.ebinterface.web2.app.DefaultSecurity;
import at.ebinterface.web2.app.menu.AppMenuPublic;
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
  protected void initGlobalSettings ()
  {
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
  }
}
