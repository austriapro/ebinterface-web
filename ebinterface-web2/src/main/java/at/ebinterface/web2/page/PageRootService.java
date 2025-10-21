package at.ebinterface.web2.page;

import java.util.Locale;

import com.helger.base.string.StringHelper;
import com.helger.css.property.CCSSProperties;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.list.IErrorList;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCButton_Submit;
import com.helger.html.hc.html.forms.HCEditFile;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.forms.HCLabel;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCHR;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.js.EJSEvent;
import com.helger.html.jscode.JSExpr;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.grid.BootstrapCol;
import com.helger.photon.bootstrap4.grid.BootstrapRow;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.icon.fontawesome.EFontAwesome5Icon;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.url.SimpleURL;
import com.helger.web.fileupload.IFileItem;

import at.ebinterface.validation.parser.EbiVersion;
import at.ebinterface.validation.validator.EbInterfaceValidator;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.web2.pdf.PDFHelper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class PageRootService extends AbstractAppWebPage
{
  private static final String FIELD_FILE_INPUT = "fileInput";

  public PageRootService (final String sID)
  {
    super (sID, "service.ebinterface.at");
  }

  @Nonnull
  private static IHCNode helpText (final String s)
  {
    return new HCP ().addStyle (CCSSProperties.FONT_SIZE.newValue ("12px"))
                     .addStyle (CCSSProperties.COLOR.newValue ("grey"))
                     .addClass (CBootstrapCSS.MT_3)
                     .addChild (s);
  }

  @Nullable
  private static IHCNode fieldErrors (final IErrorList aErrorList)
  {
    if (aErrorList.isEmpty ())
      return null;

    final BootstrapErrorBox ret = new BootstrapErrorBox ();
    for (final IError aError : aErrorList)
      ret.addChild (new HCDiv ().addChild (aError.getErrorText (Locale.ROOT)));
    return ret;
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    final BootstrapRow aRow = aNodeList.addAndReturnChild (new BootstrapRow ());
    final BootstrapCol aCol = aRow.createColumn (-1, 12, -1, -1, -1);
    aCol.addChild (h1 ("ebInterface Rechnungen erstellen, prüfen und anzeigen"));
    aCol.addChild (h3 ("Auf dieser Seite finden Sie die wichtigsten Services rund um die E-Rechnung im Format ebInterface."));

    final HCDiv aDiv = aCol.addAndReturnChild (div ());

    aDiv.addChild (new HCHR ().addClass (CSS_CLASS_SECTION_LINE));

    aDiv.addChild (h2 ("ebinterface Rechnung erstellen"));
    aDiv.addChild (p ().addChild ("Wenn Sie keine Möglichkeit haben, mit Ihrer eigenen Software ebInterface Rechnungen" +
                                  " zu erstellen bzw. nur selten Rechnungen im Format ebInterface benötigen, können Sie unser" +
                                  " Online-Rechnungsformular verwenden. Software-Hersteller und Dienstleister, die nach eigenen Angaben den" +
                                  " Rechnungsstandard ebInterface in ihren Produkten bzw. Services umgesetzt haben, finden Sie unter ")
                       .addChild (a (new SimpleURL ("https://partner.ebinterface.at")).setTargetBlank ()
                                                                                      .addChild ("partner.ebinterface.at"))
                       .addChild ("."));
    aDiv.addChild (a (new SimpleURL ("http://formular.ebinterface.at")).setTargetBlank ()
                                                                       .addClass (CSS_CLASS_BTN_WKO)
                                                                       .addChild ("Rechnung online erstellen"));
    aDiv.addChild (helpText ("Dieser Button führt Sie direkt zum ebInterface Online-Formular."));

    aDiv.addChild (new HCHR ().addClass (CSS_CLASS_SECTION_LINE));

    final BootstrapForm aForm = aDiv.addAndReturnChild (getUIHandler ().createFormFileUploadSelf (aWPEC));
    aForm.addChild (h2 ("ebInterface Rechnung prüfen und als PDF anzeigen"));
    aForm.addChild (p ("Dieses Tool prüft die Gültigkeit der ebInterface Rechnung und wandelt sie in eine leicht lesbare" +
                       " PDF-Datei um. Der QR-Code, der bei dieser Konvertierung auf der Rechnung generiert wird," +
                       " ermöglicht die automatische Übernahme der Daten ins Mobile Banking des Rechnungsempfängers."));
    aForm.addChild (new HCHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM));

    final HCDiv aUploadFile;
    {
      aUploadFile = aForm.addAndReturnChild (div ().addClass (CSS_CLASS_UPLOAD_FILE));
      final HCEditFile aInput = aUploadFile.addAndReturnChild (new HCEditFile (FIELD_FILE_INPUT).addClass (CSS_CLASS_INPUT_FILE)
                                                                                                .setID ("fileInput1"));
      aUploadFile.addChild (new HCLabel ().setFor (aInput)
                                          .addChild (EFontAwesome5Icon.FILE.getAsNode ())
                                          .addChild (" durchsuchen"));
      final HCP aUplopadText = aUploadFile.addAndReturnChild (p ().setID ("uploadText")
                                                                  .addClass (CSS_CLASS_UPLOAD_LABEL)
                                                                  .addChild ("Keine Datei ausgewählt"));
      aInput.addEventHandler (EJSEvent.CHANGE,
                              JSExpr.invoke ("getFileName").arg (aInput.getID ()).arg (aUplopadText.getID ()));
      EFontAwesome5Icon.registerResourcesForThisRequest ();
    }

    final FormErrorList aFormErrors = new FormErrorList ();
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      final IFileItem aFile = aWPEC.params ().getAsFileItem (FIELD_FILE_INPUT);
      if (aFile == null || aFile.getSize () == 0)
        aFormErrors.addFieldError (FIELD_FILE_INPUT, "Es muss eine eRechnung ausgewählt werden");

      if (aFormErrors.isEmpty ())
      {
        // Try read as XML and validate against XSD
        final ValidationResult aValResult = new EbInterfaceValidator ().validateXMLInstanceAgainstSchema (aFile.directGet ());
        final EbiVersion eDeterminedVersion = aValResult.getDeterminedEbInterfaceVersion ();
        if (eDeterminedVersion == null)
        {
          aFormErrors.addFieldError (FIELD_FILE_INPUT,
                                     "Das XML kann nicht verarbeitet werden, das es keiner ebInterface-Version entspricht.");
        }
        else
        {
          // Show determined version
          String sEbiVersion = eDeterminedVersion.getCaption ();
          if (eDeterminedVersion.supportsSigning ())
            sEbiVersion += eDeterminedVersion.isSigned () ? " (signiert)" : " (unsigniert)";

          // Was there a validation error?
          final String sValidationError = aValResult.getSchemaValidationErrorMessage ();
          if (StringHelper.isNotEmpty (sValidationError))
          {
            aFormErrors.addFieldError (FIELD_FILE_INPUT, sValidationError);
          }
          else
          {
            aForm.addChild (success ("Diese Datei ist gültig gemäß ebInterface Standard " + sEbiVersion));

            // Start creating XML
            final byte [] aPDFBytes = PDFHelper.createPDF (aValResult.getParsedXMLDocument (), eDeterminedVersion);
          }
        }
      }

      aUploadFile.addChild (fieldErrors (aFormErrors.getListOfField (FIELD_FILE_INPUT)));
    }

    final HCDiv aInlineBtn = aForm.addAndReturnChild (div ().addClass (CSS_CLASS_INLINE_BTN));
    aInlineBtn.addChild (new HCButton_Submit ("Rechnung als PDF anzeigen").addClass (CSS_CLASS_BTN_WKO));

    aForm.addChild (helpText ("Wählen Sie eine ebInterface Datei von Ihrer Festplatte aus."));

    aDiv.addChild (new HCHR ().addClass (CSS_CLASS_SECTION_LINE));

    // Footer row
    final BootstrapRow aRow2 = aNodeList.addAndReturnChild (new BootstrapRow ());
    aRow2.createColumn (-1, -1, 12, -1, -1)
         .addChild (p ().addChild ("Weiterführende Services und Konverter für Entwickler finden Sie unter ")
                        .addChild (a (new SimpleURL ("https://labs.ebinterface.at")).addChild ("labs.ebinterface.at"))
                        .addChild ("."));
  }
}
