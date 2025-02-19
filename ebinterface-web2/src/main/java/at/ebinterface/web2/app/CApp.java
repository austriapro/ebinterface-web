package at.ebinterface.web2.app;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.locale.LocaleCache;

@Immutable
public final class CApp
{
  public static final Locale LOCALE_DE_AT = LocaleCache.getInstance ().getLocale ("de", "AT");
  public static final Locale DEFAULT_LOCALE = LOCALE_DE_AT;

  // Define the global app mode (default to SERVICE)
  public static final EAppMode APP_MODE = EAppMode.getFromIDOrDefault (System.getenv ("APPLICATION_PATH"),
                                                                       EAppMode.SERVICE);

  private CApp ()
  {}

  @Nonnull
  @Nonempty
  public static String getAppName ()
  {
    return APP_MODE.isService () ? "ebInterface Services" : "ebInterface Labs";
  }
}
