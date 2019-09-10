package at.ebinterface.validation.parser;

import java.io.InputStream;

import org.xml.sax.InputSource;

import com.helger.commons.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.exception.NamespaceUnknownException;

/**
 * Custom parser for XML instances. Used to determine the used XML Schema based on the namespace
 *
 * @author pl
 */
public enum CustomParser {

  INSTANCE;

  private static final CustomHandler customHandler;

  static {
    customHandler = new CustomHandler();
  }

  /**
   * Determines the correct ebInterface version
   *
   * @return version
   */
  public EbiVersion getEbInterfaceDetails(final InputSource source)
      throws NamespaceUnknownException {

    //Get the namespace from the instance
    try {
      at.ebinterface.validation.validator.SAXParserFactory.newInstance()
          .parse(source, customHandler);
    } catch (final Exception e) {
      throw new NamespaceUnknownException(
          "Der Namespace des Invoice-ROOT Elements konnte nicht bestimmt werden.");
    }

    //Map it to an enumeration
    if (StringHelper.hasText(customHandler.getFoundNameSpace())) {
      for (final EEbInterfaceVersion v : EEbInterfaceVersion.values()) {
        if (v.getNamespaceURI ().equals(customHandler.getFoundNameSpace())) {
          //Set whether its signed or not
          //Set the NS of the Signature element
          return new EbiVersion (v, customHandler.isContainsSignature(), customHandler.getSignatureNamespacePrefix());
        }
      }
    }

    throw new NamespaceUnknownException(
        "Unbekannter Namespace gefunden: " + customHandler.getFoundNameSpace());

  }


  /**
   * Determines the ebInterface version of the upload
   *
   * @return version
   */
  public EbiVersion getEbInterfaceDetails(final InputStream inputStream)
      throws NamespaceUnknownException {
    return getEbInterfaceDetails(new InputSource(inputStream));
  }

}
