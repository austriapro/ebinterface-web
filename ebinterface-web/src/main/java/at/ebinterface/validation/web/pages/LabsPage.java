package at.ebinterface.validation.web.pages;

public class LabsPage extends BasePage {

  @Override
  protected void onInitialize() {
    super.onInitialize();

    //Add the input form
    final LabsForm inputForm = new LabsForm("inputForm");
    add(inputForm);

    //Add the form for showing the supported rules
    final ShowRulesForm showRulesForm = new ShowRulesForm("showRulesForm");
    add(showRulesForm);

    //Add the form for showing the supported rules
    final UblToEbiForm ublToEbiForm = new UblToEbiForm("ublToEbiForm", false);
    add(ublToEbiForm);

    //Add the form for convert ebInterface to UBL
    final EbiToUblForm ebiToUblForm = new EbiToUblForm("ebiToUblForm", true);
    add(ebiToUblForm);
  }
}
