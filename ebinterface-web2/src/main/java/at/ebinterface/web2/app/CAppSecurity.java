package at.ebinterface.web2.app;

import java.util.List;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.photon.security.CSecurity;

@Immutable
public final class CAppSecurity
{
  // Security role IDs
  public static final String ROLEID_CONFIG = "config";

  // User group IDs
  public static final String USERGROUPID_SUPERUSER = CSecurity.USERGROUP_ADMINISTRATORS_ID;
  public static final String USERGROUPID_CONFIG = "ugconfig";

  @CodingStyleguideUnaware
  public static final List <String> REQUIRED_ROLE_IDS_CONFIG = new CommonsArrayList <> (ROLEID_CONFIG).getAsUnmodifiable ();

  private CAppSecurity ()
  {}
}
