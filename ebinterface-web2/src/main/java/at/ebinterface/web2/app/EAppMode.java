package at.ebinterface.web2.app;

import javax.annotation.Nullable;

import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Defines the overall application we're in.
 *
 * @author Philip Helger
 */
public enum EAppMode implements IHasID <String>
{
  SERVICE ("service"),
  LABS ("labs");

  private final String m_sID;

  EAppMode (final String sID)
  {
    m_sID = sID;
  }

  public String getID ()
  {
    return m_sID;
  }

  public boolean isService ()
  {
    return this == SERVICE;
  }

  public boolean isLabs ()
  {
    return this == LABS;
  }

  @Nullable
  public static EAppMode getFromIDOrNull (final String sID)
  {
    return EnumHelper.getFromIDOrNull (EAppMode.class, sID);
  }

  @Nullable
  public static EAppMode getFromIDOrDefault (final String sID, final EAppMode eDefault)
  {
    return EnumHelper.getFromIDOrDefault (EAppMode.class, sID, eDefault);
  }
}
