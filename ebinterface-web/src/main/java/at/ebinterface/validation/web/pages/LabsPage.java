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
    final UblForm ublForm = new UblForm("ublForm");
    add(ublForm);

  }
}
