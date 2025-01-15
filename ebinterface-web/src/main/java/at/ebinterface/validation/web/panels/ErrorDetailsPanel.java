package at.ebinterface.validation.web.panels;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import at.ebinterface.validation.validator.jaxb.Result;

/**
 * This class is used to show the generated error messages from the schematron
 * validation
 *
 * @author pl
 */
public class ErrorDetailsPanel extends Panel
{

  public ErrorDetailsPanel (final String id, final List <Result.Error> errors)
  {
    super (id);

    // Create a table with the error messages
    final ListView <Result.Error> listview = new ListView <> ("repeater", errors)
    {
      @Override
      protected void populateItem (final ListItem <Result.Error> item)
      {
        item.add (new Label ("errorElement", Model.of (item.getModelObject ().getViolatingElement ())));
        item.add (new Label ("errorMessage", Model.of (item.getModelObject ().getErrorMessage ())));
      }

    };
    this.add (listview);

  }

}
