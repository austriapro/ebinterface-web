package at.ebinterface.validation.parser;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.ebinterface.EEbInterfaceVersion;

public class EbiVersion
{
  private final EEbInterfaceVersion m_eVersion;
  private final boolean m_bSigned;
  private final String m_sSignatureNamespacePrefix;

  public EbiVersion (@Nonnull final EEbInterfaceVersion eVersion, final boolean bSigned, @Nonnull final String sSignatureNamespacePrefix)
  {
    m_eVersion = eVersion;
    m_bSigned = bSigned;
    m_sSignatureNamespacePrefix = sSignatureNamespacePrefix;
  }

  @Nonnull
  public EEbInterfaceVersion getVersion ()
  {
    return m_eVersion;
  }

  public boolean supportsSigning ()
  {
    return m_eVersion.ordinal () < EEbInterfaceVersion.V50.ordinal ();
  }

  public boolean isSigned ()
  {
    return m_bSigned;
  }

  @Nonnull
  public String getSignatureNamespacePrefix ()
  {
    return m_sSignatureNamespacePrefix;
  }

  @Nonnull
  @Nonempty
  public String getCaption ()
  {
    return "ebInterface " + m_eVersion.getVersion ().getAsString (false, true);
  }
}
