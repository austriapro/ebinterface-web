package at.ebinterface.web2.app.menu;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.photon.core.menu.IMenuTree;

@Immutable
public final class AppMenuPublic
{
  private AppMenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // Set default
    aMenuTree.setDefaultMenuItemID (CAppMenuPublic.MENU_ROOT);
  }
}
