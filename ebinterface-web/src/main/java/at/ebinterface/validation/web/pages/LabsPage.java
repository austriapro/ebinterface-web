package at.ebinterface.validation.web.pages;

public final class LabsPage extends BasePage
{
  @Override
  protected void onInitialize ()
  {
    super.onInitialize ();

    final boolean bIsStartPage = false;

    // Add the input form
    final LabsForm inputForm = new LabsForm ("inputForm");
    add (inputForm);

    // Add the form for showing the supported rules
    final ShowRulesForm showRulesForm = new ShowRulesForm ("showRulesForm");
    add (showRulesForm);

    // Add the form for converting UBL to ebInterface
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
