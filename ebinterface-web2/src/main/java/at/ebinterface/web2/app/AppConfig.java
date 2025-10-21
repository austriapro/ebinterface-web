package at.ebinterface.web2.app;

import com.helger.base.debug.GlobalDebug;
import com.helger.config.ConfigFactory;
import com.helger.config.IConfig;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class provides access to the settings as contained in the
 * <code>webapp.properties</code> file.
 *
 * @author Philip Helger
 */
public final class AppConfig
{
  private AppConfig ()
  {}

  @Nonnull
  public static IConfig getConfig ()
  {
    return ConfigFactory.getDefaultConfig ();
  }

  @Nullable
  public static String getGlobalDebug ()
  {
    return getConfig ().getAsString ("global.debug");
  }

  @Nullable
  public static String getGlobalProduction ()
  {
    return getConfig ().getAsString ("global.production");
  }

  @Nullable
  public static String getDataPath ()
  {
    return getConfig ().getAsString ("webapp.datapath");
  }

  public static boolean isCheckFileAccess ()
  {
    return getConfig ().getAsBoolean ("webapp.checkfileaccess", true);
  }

  public static boolean isTestVersion ()
  {
    return getConfig ().getAsBoolean ("webapp.testversion", GlobalDebug.isDebugMode ());
  }
}
