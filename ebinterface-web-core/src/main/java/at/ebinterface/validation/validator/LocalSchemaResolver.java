package at.ebinterface.validation.validator;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.crypto.dsig.XMLSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Resolver for local schema definitions
 */
public class LocalSchemaResolver implements LSResourceResolver
{

  private final LSResourceResolver parent;

  private static final Logger LOG = LoggerFactory.getLogger (LocalSchemaResolver.class);

  public LocalSchemaResolver (final LSResourceResolver parent)
  {
    this.parent = parent;
  }

  @Override
  public LSInput resolveResource (final String type,
                                  final String namespaceURI,
                                  final String publicId,
                                  final String systemId,
                                  final String baseURI)
  {
    final LSInput input = new LSInputImpl ();
    String url = null;

    // Replace specific schema
    if (XMLSignature.XMLNS.equals (namespaceURI))
    {
      LOG.debug ("Trying to resolve XMLDSIG Schema");
      url = "/schemas/xmldsig-core-schema.xsd";
    }
    // Replace the 4p0 extension schema
    else
      if ("http://www.ebinterface.at/schema/4p0/extensions/ext".equals (namespaceURI))
      {
        LOG.debug ("Trying to resolve 4p0 extension Schema");
        url = "/ebinterface/ebInterfaceExtension4p0.xsd";
      }
      // Replace the 4p0 SV extension schema
      else
        if ("http://www.ebinterface.at/schema/4p0/extensions/sv".equals (namespaceURI))
        {
          LOG.debug ("Trying to resolve 4p0 SV extension Schema");
          url = "/ebinterface/ext4p0/ebInterfaceExtension_SV.xsd";
        }
        // Replace the 4p1 extension schema
        else
          if ("http://www.ebinterface.at/schema/4p1/extensions/ext".equals (namespaceURI))
          {
            LOG.debug ("Trying to resolve 4p1 extension Schema");
            url = "/ebinterface/ebInterfaceExtension4p1.xsd";
          }
          // Replace the 4p1 SV extension schema
          else
            if ("http://www.ebinterface.at/schema/4p1/extensions/sv".equals (namespaceURI))
            {
              LOG.debug ("Trying to resolve 4p1 SV extension Schema");
              url = "/ebinterface/ext4p1/ebInterfaceExtension_SV.xsd";
            }
            // Replace the 4p2 extension schema
            else
              if ("http://www.ebinterface.at/schema/4p2/extensions/ext".equals (namespaceURI))
              {
                LOG.debug ("Trying to resolve 4p2 extension Schema");
                url = "/ebinterface/ebInterfaceExtension4p2.xsd";
              }
              // Replace the 4p2 SV extension schema
              else
                if ("http://www.ebinterface.at/schema/4p2/extensions/sv".equals (namespaceURI))
                {
                  LOG.debug ("Trying to resolve 4p2 SV extension Schema");
                  url = "/ebinterface/ext4p2/ebInterfaceExtension_SV.xsd";
                }
                // Replace the 4p3 extension schema
                else
                  if ("http://www.ebinterface.at/schema/4p3/extensions/ext".equals (namespaceURI))
                  {
                    LOG.debug ("Trying to resolve 4p3 extension Schema");
                    url = "/ebinterface/ebInterfaceExtension4p3.xsd";
                  }
                  // Replace the 4p3 SV extension schema
                  else
                    if ("http://www.ebinterface.at/schema/4p3/extensions/sv".equals (namespaceURI))
                    {
                      LOG.debug ("Trying to resolve 4p3 SV extension Schema");
                      url = "/ebinterface/ext4p3/ebInterfaceExtension_SV.xsd";
                    }

    // Replace with local url
    if (url != null)
    {
      final InputStream is = this.getClass ().getResourceAsStream (url);
      if (is != null)
      {
        LOG.debug ("Found Schema at {}", this.getClass ().getResource (url).toString ());
        input.setByteStream (is);
        return input;
      }
      LOG.warn ("Schema not found at {}", this.getClass ().getResource (url).toString ());
    }

    if (parent != null)
      return parent.resolveResource (type, namespaceURI, publicId, systemId, baseURI);

    return null;
  }

  static class LSInputImpl implements LSInput
  {
    private String systemId = null;
    private InputStream byteStream = null;

    @Override
    public Reader getCharacterStream ()
    {
      return null;
    }

    @Override
    public void setCharacterStream (final Reader characterStream)
    {}

    @Override
    public InputStream getByteStream ()
    {
      InputStream retval = null;
      if (byteStream != null)
      {
        retval = byteStream;
      }
      return retval;
    }

    @Override
    public void setByteStream (final InputStream byteStream)
    {
      this.byteStream = byteStream;
    }

    @Override
    public String getStringData ()
    {
      return null;
    }

    @Override
    public void setStringData (final String stringData)
    {}

    @Override
    public String getSystemId ()
    {
      return systemId;
    }

    @Override
    public void setSystemId (final String systemId)
    {
      this.systemId = systemId;
    }

    @Override
    public String getPublicId ()
    {
      return null;
    }

    @Override
    public void setPublicId (final String publicId)
    {}

    @Override
    public String getBaseURI ()
    {
      return null;
    }

    @Override
    public void setBaseURI (final String baseURI)
    {}

    @Override
    public String getEncoding ()
    {
      return null;
    }

    @Override
    public void setEncoding (final String encoding)
    {}

    @Override
    public boolean getCertifiedText ()
    {
      return false;
    }

    @Override
    public void setCertifiedText (final boolean certifiedText)
    {}
  }

}
