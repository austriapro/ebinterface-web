package test.validator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.helger.ebinterface.EEbInterfaceVersion;

import at.ebinterface.validation.validator.EbInterfaceValidator;

/**
 * Used to test the application of stylesheets
 *
 * @author pl
 */
public class EbInterfaceStylesheetTest {


    @Test
    public void testApplyStylesheet() throws IOException {

        //3p02
        InputStream input = this.getClass().getResourceAsStream("/ebinterface/3p02/InvoiceExample1.xml");
        Assert.assertNotNull(input);
        byte[] inputData = IOUtils.toByteArray(input);

        EbInterfaceValidator validator = new EbInterfaceValidator();
        String s = validator.transformInput(inputData, EEbInterfaceVersion.V302);
        System.out.print(s);
        Assert.assertFalse(hasError(s));

        //3p0
        input = this.getClass().getResourceAsStream("/ebinterface/3p0/InvoiceExample1.xml");
        Assert.assertNotNull(input);
        inputData = IOUtils.toByteArray(input);
        s = validator.transformInput(inputData, EEbInterfaceVersion.V30);
        Assert.assertFalse(hasError(s));
        System.out.print(s);

        //4p0
        input = this.getClass().getResourceAsStream("/ebinterface/4p0/ebinterface4-test1.xml");
        Assert.assertNotNull(input);
        inputData = IOUtils.toByteArray(input);
        s = validator.transformInput(inputData, EEbInterfaceVersion.V40);
        Assert.assertFalse(hasError(s));
        System.out.print(s);


        //4p1
        input = this.getClass().getResourceAsStream("/ebinterface/4p1/ebInterface_4p1_sample.xml");
        Assert.assertNotNull(input);
        inputData = IOUtils.toByteArray(input);
        s = validator.transformInput(inputData, EEbInterfaceVersion.V41);
        Assert.assertFalse(hasError(s));
        System.out.print(s);


        //4p1
        input = this.getClass().getResourceAsStream("/ebinterface/4p3/ebInterface_4p3_sample.xml");
        Assert.assertNotNull(input);
        inputData = IOUtils.toByteArray(input);
        s = validator.transformInput(inputData, EEbInterfaceVersion.V43);
        Assert.assertFalse(hasError(s));
        System.out.print(s);

    }


    @Test
    public void testXXEExploit() throws IOException {

        InputStream input = this.getClass().getResourceAsStream("/ebinterface/4p0/xxe-exploit.xml");
        Assert.assertNotNull(input);
        byte[] inputData = IOUtils.toByteArray(input);
        EbInterfaceValidator validator = new EbInterfaceValidator();

        String s = validator.transformInput(inputData, EEbInterfaceVersion.V40);
        Assert.assertTrue(hasError(s));
        System.out.print(s);
    }



    private boolean hasError(String s) {
        return s.startsWith("XSLT Transformation konnte nicht ausgeführt werden");
    }

}
