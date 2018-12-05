package at.ebinterface.validation.web.pages;

public class ServicePage extends BasePage {

  @Override
  protected void onInitialize() {
    super.onInitialize();

    //Add the input form
    final ServiceForm inputForm = new ServiceForm("inputForm");
    add(inputForm);
  }
}
