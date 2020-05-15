package test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.ebinterface.EEbInterfaceVersion;
import com.helger.xml.serialize.read.DOMReader;

import at.ebinterface.validation.exception.NamespaceUnknownException;
import at.ebinterface.validation.parser.CustomParser;

/**
 * Used to test the custom parser class
 *
 * @author pl
 */
public class CustomParserTest
{

  @Test
  public void testCustomParser () throws NamespaceUnknownException
  {

    // invalid text input
    InputStream inputStream = this.getClass ().getResourceAsStream ("/ebinterface/2p2/textdummy.txt");
    assertNotNull (inputStream);
    Document source = DOMReader.readXMLDOM (inputStream);
    assertNull (source);

    try
    {
      CustomParser.INSTANCE.getEbInterfaceDetails (null);
      fail ();
    }
    catch (final NamespaceUnknownException ne)
    {
      assertNotNull (ne);
      System.out.println (ne.getMessage ());
    }

    // Invalid binary dummy
    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/2p2/binarydummy.jpg");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    try
    {
      CustomParser.INSTANCE.getEbInterfaceDetails (source);
    }
    catch (final NamespaceUnknownException ne)
    {
      assertNotNull (ne);
      System.out.println (ne.getMessage ());
    }

    // 2p2 - invalid
    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/2p2/Instance_2p2.xml");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    try
    {
      CustomParser.INSTANCE.getEbInterfaceDetails (source);
    }
    catch (final NamespaceUnknownException ne)
    {
      assertNotNull (ne);
      System.out.println (ne.getMessage ());
    }

    // 3p0
    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/3p0/ebInterface_Instance_withExtension.xml");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    assertEquals (EEbInterfaceVersion.V30, CustomParser.INSTANCE.getEbInterfaceDetails (source).getVersion ());

    // 3p02
    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/3p02/InvoiceExample2.xml");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    assertEquals (EEbInterfaceVersion.V302, CustomParser.INSTANCE.getEbInterfaceDetails (source).getVersion ());

    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/3p02/InvoiceExample1.xml");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    assertEquals (EEbInterfaceVersion.V302, CustomParser.INSTANCE.getEbInterfaceDetails (source).getVersion ());

    // 4p0
    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/4p0/testinstance-valid-schema.xml");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    assertEquals (EEbInterfaceVersion.V40, CustomParser.INSTANCE.getEbInterfaceDetails (source).getVersion ());

    // 4p1
    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/4p1/ebInterface_4p1_sample.xml");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    assertEquals (EEbInterfaceVersion.V41, CustomParser.INSTANCE.getEbInterfaceDetails (source).getVersion ());

    // 4p3
    inputStream = this.getClass ().getResourceAsStream ("/ebinterface/4p3/ebInterface_4p3_sample.xml");
    assertNotNull (inputStream);
    source = DOMReader.readXMLDOM (inputStream);

    assertEquals (EEbInterfaceVersion.V43, CustomParser.INSTANCE.getEbInterfaceDetails (source).getVersion ());
  }
}
