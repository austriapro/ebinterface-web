package at.ebinterface.validation.web;

import java.util.Locale;

import org.apache.wicket.MetaDataKey;

import at.ebinterface.validation.validator.EbInterfaceValidator;
import net.sf.jasperreports.engine.JasperReport;

/**
 * Created by paul on 1/14/16.
 */
public final class Constants
{
  public static final MetaDataKey <JasperReport> METADATAKEY_EBINTERFACE_JRTEMPLATE = new MetaDataKey <JasperReport> ()
  {};

  public static final MetaDataKey <EbInterfaceValidator> METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR = new MetaDataKey <EbInterfaceValidator> ()
  {};

  public static final Locale DE_AT = new Locale ("de", "AT");

  private Constants ()
  {}
}
