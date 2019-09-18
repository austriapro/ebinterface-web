package at.ebinterface.validation.web.pages.schematron;

import java.nio.charset.StandardCharsets;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

import com.helger.commons.io.stream.StreamHelper;

import at.ebinterface.validation.validator.Rule;
import at.ebinterface.validation.web.components.CodeBox;
import at.ebinterface.validation.web.pages.BasePage;


/**
 * Page for showing the details of Schematron rules
 *
 * @author pl
 */
public final class ShowRulesPage extends BasePage {
  public ShowRulesPage(final Rule rule, final Class <? extends WebPage> returnPage) {

    //Add a label showing the selected Schematron file
    add(new Label("selectedSchematron", Model.of(rule.getName())));

    //Add a return link
    add(new Link<Object>("returnLink") {
      @Override
      public void onClick() {
        setResponsePage(returnPage);
      }
    }).setVisibilityAllowed (returnPage != null);

    final String code = StreamHelper.getAllBytesAsString (getClass().getResourceAsStream(rule.getFileReference()), StandardCharsets.UTF_8);

    //Add the codebox for showing the schematron rules
    final CodeBox codeBox = new CodeBox("message", code).setDisplayLineNumbers(true);
    add(codeBox);
  }
}
