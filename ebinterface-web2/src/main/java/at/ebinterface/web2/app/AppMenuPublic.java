package at.ebinterface.web2.app;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.photon.core.menu.IMenuTree;

import at.ebinterface.web2.page.PageRoot;

@Immutable
public final class AppMenuPublic
{
  private AppMenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    aMenuTree.createRootItem (new PageRoot (CAppMenuPublic.MENU_ROOT));

    // Set default
    aMenuTree.setDefaultMenuItemID (CAppMenuPublic.MENU_ROOT);
  }
}
