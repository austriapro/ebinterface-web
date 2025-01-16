package at.ebinterface.web2.app;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.locale.LocaleCache;

@Immutable
public final class CApp
{
  public static final Locale LOCALE_DE = LocaleCache.getInstance ().getLocale ("de", "AT");
  public static final Locale DEFAULT_LOCALE = LOCALE_DE;

  public static final String APP_NAME = "ebInterface Services";

  // Define the global app mode
  public static final EAppMode APP_MODE = EAppMode.getFromIDOrDefault (System.getenv ("APPLICATION_PATH"),
                                                                       EAppMode.SERVICE);

  private CApp ()
  {}
}
