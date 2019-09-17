package at.ebinterface.validation.web.pages;

import java.nio.charset.StandardCharsets;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.helger.commons.io.stream.StreamHelper;

import at.ebinterface.validation.validator.Rule;
import at.ebinterface.validation.web.components.CodeBox;


/**
 * Page for showing the details of Schematron rules
 *
 * @author pl
 */
public class ShowRulesPage extends BasePage {


  public ShowRulesPage(IModel<Rule> ruleModel) {

    //Add a label showing the selected Schematron file
    add(new Label("selectedSchematron", Model.of(ruleModel.getObject().getName())));

    //Add a return link
    add(new Link<Object>("returnLink") {
      @Override
      public void onClick() {
        setResponsePage(StartPage.class);
      }
    });

    final String code = StreamHelper.getAllBytesAsString (getClass().getResourceAsStream(ruleModel.getObject().getFileReference()), StandardCharsets.UTF_8);

    //Add the codebox for showing the schematron rules
    final CodeBox codeBox = new CodeBox("message", code).setDisplayLineNumbers(true);
    add(codeBox);


  }

}
