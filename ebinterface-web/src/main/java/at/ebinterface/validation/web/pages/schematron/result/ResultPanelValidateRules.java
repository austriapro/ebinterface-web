package at.ebinterface.validation.web.pages.schematron.result;

import javax.annotation.Nullable;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.helger.commons.string.StringHelper;

import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.validator.jaxb.Result;
import at.ebinterface.validation.web.panels.ErrorDetailsPanel;

public final class ResultPanelValidateRules extends Panel
{
  public ResultPanelValidateRules (final String id,
                                   final ValidationResult validationResult,
                                   final String selectedSchematron,
                                   @Nullable final Class <? extends WebPage> returnPage)
  {
    super (id);

    final String schemaVersion = validationResult.getDeterminedEbInterfaceVersion () != null ? validationResult.getDeterminedEbInterfaceVersion ()
                                                                                                               .getCaption ()
                                                                                             : "";

    final Label schemaVersionLabel;
    final Label schemaVersionLabelNoOk;
    if (schemaVersion.length () > 0)
    {
      // Add a label with the schema version
      schemaVersionLabel = new Label ("schemaVersion", Model.of (schemaVersion.toString ()));
      schemaVersionLabelNoOk = new Label ("schemaVersion", Model.of (schemaVersion.toString ()));
    }
    else
    {
      schemaVersionLabel = new Label ("schemaVersion", Model.of ("Es wurde keine gültige Version erkannt."));
      schemaVersionLabelNoOk = new Label ("schemaVersion", Model.of ("Es wurde keine gültige Version erkannt."));
    }

    // Schema OK Container
    final WebMarkupContainer schemaOkContainer = new WebMarkupContainer ("schemvalidationOK");
    schemaOkContainer.add (schemaVersionLabel);
    add (schemaOkContainer);

    // Schema NOK Container
    final WebMarkupContainer schemaNOkContainer = new WebMarkupContainer ("schemvalidationNOK");
    schemaNOkContainer.add (schemaVersionLabelNoOk);
    schemaNOkContainer.add (new Label ("schemaValidationError",
                                       Model.of (validationResult.getSchemaValidationErrorMessage ())));
    add (schemaNOkContainer);

    // Schema is OK
    if (StringHelper.hasNoText (validationResult.getSchemaValidationErrorMessage ()))
    {
      schemaOkContainer.setVisible (true);
      schemaNOkContainer.setVisible (false);
    }
    // Schema NOK
    else
    {
      schemaOkContainer.setVisible (false);
      schemaNOkContainer.setVisible (true);
    }

    // Schematron OK Container
    // Add a label with the selected Schematron
    final WebMarkupContainer schematronOkContainer = new WebMarkupContainer ("schematronOK");
    schematronOkContainer.add (new Label ("selectedSchematron", Model.of (selectedSchematron)));
    add (schematronOkContainer);

    // Schematron NOK Container
    final WebMarkupContainer schematronNokContainer = new WebMarkupContainer ("schematronNOK");
    schematronNokContainer.add (new Label ("selectedSchematron", Model.of (selectedSchematron)));
    add (schematronNokContainer);

    // Add schematron error messages if there some
    final Result schematronResult = validationResult.getSchematronResult ();
    if (schematronResult == null || schematronResult.getErrors () == null || schematronResult.getErrors ().isEmpty ())
    {
      schematronNokContainer.add (new EmptyPanel ("errorDetailsPanel"));
      schematronOkContainer.setVisible (true);
      schematronNokContainer.setVisible (false);
    }
    else
    {
      schematronNokContainer.add (new ErrorDetailsPanel ("errorDetailsPanel", schematronResult.getErrors ()));
      schematronOkContainer.setVisible (false);
      schematronNokContainer.setVisible (true);
    }

    add (new Link <Object> ("returnLink")
    {
      @Override
      public void onClick ()
      {
        setResponsePage (returnPage);
      }
    }.setVisibilityAllowed (returnPage != null));
  }
}
