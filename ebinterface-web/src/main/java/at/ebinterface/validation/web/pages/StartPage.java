package at.ebinterface.validation.web.pages;

import at.ebinterface.validation.web.pages.convert.EbiToUblForm;
import at.ebinterface.validation.web.pages.convert.EbiToXRechnungForm;
import at.ebinterface.validation.web.pages.convert.UblToEbiForm;
import at.ebinterface.validation.web.pages.convert.XRechnungToEbiForm;

/**
 * First page of the ebInterface Validation Service
 *
 * @author pl
 */
public final class StartPage extends BasePage
{
  @Override
  protected void onInitialize ()
  {
    super.onInitialize ();

    // Add the input form
    final StartForm inputForm = new StartForm ("inputForm");
    add (inputForm);

    // Add the form for converting UBL to ebInterface
    final UblToEbiForm ublToEbiForm = new UblToEbiForm ("ublToEbiForm", StartPage.class);
    add (ublToEbiForm);

    // Add the form for convert ebInterface to UBL
    final EbiToUblForm ebiToUblForm = new EbiToUblForm ("ebiToUblForm", StartPage.class);
    add (ebiToUblForm);

    // Add the form for convert XRechnung to ebInterface
    final XRechnungToEbiForm xRechnungToEbiForm = new XRechnungToEbiForm ("xRechnungToEbiForm", StartPage.class);
    add (xRechnungToEbiForm);

    // Add the form for convert ebInterface to XRechnung
    final EbiToXRechnungForm ebiToXRechnungForm = new EbiToXRechnungForm ("ebiToXRechnungForm", StartPage.class);
    add (ebiToXRechnungForm);
  }
}
