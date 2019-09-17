package at.ebinterface.validation.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Custom handler for ebInterface XML instances
 *
 * @author pl
 */
public class CustomHandler extends DefaultHandler {

  //ebInterface Namespace
  private String foundNameSpace;
  //Indicates whether the file is signed or not
  private boolean containsSignature;
  //Namespace Prefix of the Signature element
  private String signatureNamespacePrefix;


  @Override
  public void startDocument() throws SAXException {
    foundNameSpace = "";
    containsSignature = false;
    signatureNamespacePrefix = "";
  }

 
  @Override
  public void startElement(final String uri, final String localName, final String qName,
                           final Attributes attributes) throws SAXException {
    //Get the Invoice ROOT element
    if (qName.endsWith("Invoice")) {

      //Does the Invoice element have a namespace prefix? If not - there must be a xmlns attribute present
      if (qName.equals("Invoice")) {
        for (int i = 0; i < attributes.getLength(); i++) {
          final String q = attributes.getQName(i);
          //Try to get the xmlns attribute
          if (q.equals("xmlns")) {
            foundNameSpace = attributes.getValue(q);
            break;
          }
        }
      } else {
        //Get the namespace prefix of the Invoice element
        String nameSpace = qName.substring(0, qName.indexOf(":"));
        nameSpace = "xmlns:" + nameSpace;

        for (int i = 0; i < attributes.getLength(); i++) {
          final String q = attributes.getQName(i);
          //Try to get the xmlns:namespaceprefix attribute
          if (q.equals(nameSpace)) {
            foundNameSpace = attributes.getValue(q);
            break;
          }
        }
      }

    } else if (qName.endsWith("Signature")) {
      containsSignature = true;

      //Get the namespace prefix of the signature element
      final String nameSpace = qName.substring(0, qName.indexOf(":"));
      signatureNamespacePrefix = nameSpace;
    }
  }

  public String getFoundNameSpace() {
    return foundNameSpace;
  }

  public boolean isContainsSignature() {
    return containsSignature;
  }

  public String getSignatureNamespacePrefix() {
    return signatureNamespacePrefix;
  }
}
