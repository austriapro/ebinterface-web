package at.ebinterface.web2.app;

import com.helger.annotation.concurrent.Immutable;
import com.helger.photon.core.menu.IMenuObjectFilter;
import com.helger.photon.core.menu.IMenuTree;

import at.ebinterface.web2.page.PageRootLabs;
import at.ebinterface.web2.page.PageRootService;
import jakarta.annotation.Nonnull;

@Immutable
public final class AppMenuPublic
{
  private AppMenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    final IMenuObjectFilter aDisplayFilterIsService = t -> CApp.APP_MODE.isService ();
    final IMenuObjectFilter aDisplayFilterIsLabs = t -> CApp.APP_MODE.isLabs ();

    aMenuTree.createRootItem (new PageRootService (CAppMenuPublic.MENU_ROOT_SERVICE))
             .setDisplayFilter (aDisplayFilterIsService);
    aMenuTree.createRootItem (new PageRootLabs (CAppMenuPublic.MENU_ROOT_LABS)).setDisplayFilter (aDisplayFilterIsLabs);

    // Set default depending on app mode
    aMenuTree.setDefaultMenuItemID (CApp.APP_MODE.isService () ? CAppMenuPublic.MENU_ROOT_SERVICE
                                                               : CAppMenuPublic.MENU_ROOT_LABS);
  }
}
