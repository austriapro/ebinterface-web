package at.ebinterface.validation.parser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.xsds.xmldsig.CXMLDSig;

/**
 * Custom handler for ebInterface XML instances
 *
 * @author pl
 */
public class CustomHandler
{

  // ebInterface Namespace
  private String foundNameSpace;
  // Indicates whether the file is signed or not
  private boolean containsSignature;
  // Namespace Prefix of the Signature element
  private String signatureNamespacePrefix;

  private void _evalElement (final Element aElem)
  {
    if ("Invoice".equals (aElem.getLocalName ()))
    {
      foundNameSpace = aElem.getNamespaceURI ();
    }
    else
      if (CXMLDSig.NAMESPACE_URI.equals (aElem.getNamespaceURI ()) && "Signature".equals (aElem.getLocalName ()))
      {
        containsSignature = true;

        // Get the namespace prefix of the signature element
        signatureNamespacePrefix = aElem.getPrefix ();
      }
  }

  private void _parseRecursive (@Nonnull final Node node)
  {
    if (node.getNodeType () == Node.ELEMENT_NODE)
      _evalElement ((Element) node);

    final NodeList nodeList = node.getChildNodes ();
    for (int i = 0; i < nodeList.getLength (); i++)
    {
      final Node currentNode = nodeList.item (i);
      if (currentNode.getNodeType () == Node.ELEMENT_NODE)
      {
        // calls this method for all the children which is Element
        _parseRecursive (currentNode);
      }
    }
  }

  public void parse (@Nullable final Node node)
  {
    foundNameSpace = "";
    containsSignature = false;
    signatureNamespacePrefix = "";
    if (node != null)
      _parseRecursive (node);
  }

  @Nonnull
  public String getFoundNameSpace ()
  {
    return foundNameSpace;
  }

  public boolean isContainsSignature ()
  {
    return containsSignature;
  }

  @Nonnull
  public String getSignatureNamespacePrefix ()
  {
    return signatureNamespacePrefix;
  }
}
