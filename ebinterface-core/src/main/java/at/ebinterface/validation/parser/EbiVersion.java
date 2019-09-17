package at.ebinterface.validation.parser;

import com.helger.ebinterface.EEbInterfaceVersion;

public class EbiVersion
{
  private final EEbInterfaceVersion m_eVersion;
  private final boolean m_bSigned;
  private final String m_sSignatureNamespacePrefix;

  public EbiVersion (final EEbInterfaceVersion eVersion, final boolean bSigned, final String sSignatureNamespacePrefix)
  {
    m_eVersion = eVersion;
    m_bSigned = bSigned;
    m_sSignatureNamespacePrefix = sSignatureNamespacePrefix;
  }

  public EEbInterfaceVersion getVersion ()
  {
    return m_eVersion;
  }

  public boolean supportsSignign ()
  {
    return m_eVersion.ordinal () < EEbInterfaceVersion.V50.ordinal ();
  }

  public boolean isSigned ()
  {
    return m_bSigned;
  }

  public String getSignatureNamespacePrefix ()
  {
    return m_sSignatureNamespacePrefix;
  }

  public String getCaption ()
  {
    return "ebInterface " + m_eVersion.getVersion ().getAsString (false, true);
  }
}
