package at.ebinterface.validation.web.pages;

import at.ebinterface.validation.web.pages.convert.EbiToUblForm;
import at.ebinterface.validation.web.pages.convert.EbiToXRechnungForm;
import at.ebinterface.validation.web.pages.convert.UblToEbiForm;
import at.ebinterface.validation.web.pages.convert.XRechnungToEbiForm;
import at.ebinterface.validation.web.pages.schematron.ShowRulesForm;
import at.ebinterface.validation.web.pages.schematron.ValidateRulesForm;

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

    // Disable according to mail from Alex Foidl
    if (false)
    {
      // Add the form for validating against the Schematrons
      final ValidateRulesForm validateRulesForm = new ValidateRulesForm ("validateRulesForm", bIsStartPage);
      add (validateRulesForm);

      // Add the form for showing the supported Schematrons
      final ShowRulesForm showRulesForm = new ShowRulesForm ("showRulesForm", bIsStartPage);
      add (showRulesForm);
    }
  }
}
