package at.ebinterface.validation.web.pages;

import java.util.List;

import com.helger.commons.collection.impl.CommonsArrayList;

/**
 * First page of the ebInterface Validation Service
 *
 * @author pl
 */
public class StartPage extends BasePage
{
  /**
   * The possible actions
   */
  public enum ActionType
  {
    SCHEMA_VALIDATION,
    SCHEMA_AND_SCHEMATRON_VALIDATION,
    VISUALIZATION_HTML,
    VISUALIZATION_PDF,
    CONVERSION_ZUGFERD
  }

  // choices in dropdown box ZUGFeRD
  static final List <String> ZUGFERD_LEVELS = new CommonsArrayList <> ("ZUGFeRD (1.0) Basic",
                                                                       "ZUGFeRD (1.0) Comfort",
                                                                       "ZUGFeRD (1.0) Extended").getAsUnmodifiable ();

  @Override
  protected void onInitialize ()
  {
    super.onInitialize ();

    final boolean bIsStartPage = true;

    // Add the input form
    final InputForm inputForm = new InputForm ("inputForm");
    add (inputForm);

    // Add the form for showing the supported rules
    final ShowRulesForm showRulesForm = new ShowRulesForm ("showRulesForm");
    add (showRulesForm);

    // Add the form for convert UBL to ebInterface
    final UblToEbiForm ublToEbiForm = new UblToEbiForm ("ublToEbiForm", bIsStartPage);
    add (ublToEbiForm);

    // Add the form for convert ebInterface to UBL
    final EbiToUblForm ebiToUblForm = new EbiToUblForm ("ebiToUblForm", bIsStartPage);
    add (ebiToUblForm);

    // Add the form for convert XRechnung to ebInterface
    final XRechnungToEbiForm xRechnungToEbiForm = new XRechnungToEbiForm ("xRechnungToEbiForm", bIsStartPage);
    add (xRechnungToEbiForm);

    // Add the form for convert ebInterface to XRechnung
    final EbiToXRechnungForm ebiToXRechnungForm = new EbiToXRechnungForm ("ebiToXRechnungForm", bIsStartPage);
    add (ebiToXRechnungForm);
  }
}
