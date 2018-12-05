package at.ebinterface.validation.web.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * First page of the ebInterface Validation Service
 *
 * @author pl
 */
public class StartPage extends BasePage {

  /**
   * The three possible actions
   */
  public enum ActionType {
    SCHEMA_VALIDATION, SCHEMA_AND_SCHEMATRON_VALIDATION, VISUALIZATION_HTML, VISUALIZATION_PDF, CONVERSION_ZUGFERD
  }

  //choices in dropdown box ZUGFeRD
  public static final List<String> ZUGFERD_LEVELS = Arrays.asList(new String[]{
      "ZUGFeRD (1.0) Basic", "ZUGFeRD (1.0) Comfort", "ZUGFeRD (1.0) Extended"});

  public static final Logger LOG = LoggerFactory.getLogger(StartPage.class.getName());

  /**
   * Construc the start page
   */
  public StartPage() {

    //Add the input form
    final InputForm inputForm = new InputForm("inputForm");
    add(inputForm);

    //Add the form for showing the supported rules
    final ShowRulesForm showRulesForm = new ShowRulesForm("showRulesForm");
    add(showRulesForm);

    //Add the form for showing the supported rules
    final UblForm ublForm = new UblForm("ublForm");
    add(ublForm);


  }


}
