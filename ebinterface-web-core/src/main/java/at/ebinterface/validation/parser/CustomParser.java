package at.ebinterface.validation.parser;

import javax.annotation.Nonnull;

import org.w3c.dom.Document;

import com.helger.commons.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.exception.NamespaceUnknownException;

/**
 * Custom parser for XML instances. Used to determine the used XML Schema based
 * on the namespace
 *
 * @author pl
 */
public enum CustomParser
{
  INSTANCE;

  /**
   * Determines the correct ebInterface version
   *
   * @return version and never <code>null</code>
   */
  @Nonnull
  public EbiVersion getEbInterfaceDetails (@Nonnull final Document aDoc) throws NamespaceUnknownException
  {
    // Instantiate per call to avoid threading issues
    final CustomHandler customHandler = new CustomHandler ();
    customHandler.parse (aDoc);

    // Map it to an enumeration
    final String sFoundNS = customHandler.getFoundNameSpace ();
    if (StringHelper.hasText (sFoundNS))
      for (final EEbInterfaceVersion v : EEbInterfaceVersion.values ())
        if (v.getNamespaceURI ().equals (sFoundNS))
        {
          // Set whether its signed or not
          // Set the NS of the Signature element
          return new EbiVersion (v, customHandler.isContainsSignature (), customHandler.getSignatureNamespacePrefix ());
        }

    throw new NamespaceUnknownException ("Der Namespace des XML-Wurzelelements konnte nicht bestimmt werden.");
  }
}
