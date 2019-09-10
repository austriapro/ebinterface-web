package at.ebinterface.validation.validator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Factory providing XXE-save SAX-parser
 */
public class SAXParserFactory {


  private static final Logger LOG = LoggerFactory.getLogger(SAXParserFactory.class.getName());


  /**
   * SAX-Parser factory
   */
  private static javax.xml.parsers.SAXParserFactory saxParserFactory;

  static {

    //Get a SAXParser factory and avoid potential XXE
    saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance();

    try {
      saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


  /**
   * Creates a new SAXParser instance
   */
  public static SAXParser newInstance() {

    try {
      return saxParserFactory.newSAXParser();
    } catch (ParserConfigurationException e) {
      LOG.error("Unable to instantiate new SAXParser", e);
    } catch (SAXException e) {
      LOG.error("Unable to instantiate new SAXParser", e);
    }

    return null;


  }


}
