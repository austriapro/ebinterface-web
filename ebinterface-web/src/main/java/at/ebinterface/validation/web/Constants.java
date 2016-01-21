package at.ebinterface.validation.web;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.wicket.MetaDataKey;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.austriapro.Mapping;

/**
 * Created by paul on 1/14/16.
 */
public class Constants {
  public static final MetaDataKey<JasperReport> METADATAKEY_EBINTERFACE_JRTEMPLATE = new MetaDataKey<JasperReport>() { };
  public static final MetaDataKey<JasperReport> METADATAKEY_ZUGFERD_JRTEMPLATE = new MetaDataKey<JasperReport>() { };

  public static final MetaDataKey<EbInterfaceValidator> METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR = new MetaDataKey<EbInterfaceValidator>() { };
  public static final MetaDataKey<Schema> METADATAKEY_ZUGFERD_XMLSCHEMA = new MetaDataKey<Schema>() { };

  public static final MetaDataKey<Templates> METADATAKEY_ZUGFERD_SCHEMATRONTEMPLATE = new MetaDataKey<Templates>() { };

}
