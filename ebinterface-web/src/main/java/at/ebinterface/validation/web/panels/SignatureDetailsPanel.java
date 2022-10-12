package at.ebinterface.validation.web.panels;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Shows the details of a signature verification (signature, certificate,
 * manifest)
 */
public class SignatureDetailsPanel extends Panel
{
  public SignatureDetailsPanel (final String id, final IModel <Boolean> isValid, final IModel <String> detailsModel)
  {
    super (id);
    final boolean bValid = isValid.getObject ().booleanValue ();
    add (new Label ("status", bValid ? "Gültig" : "Ungültig").add (new AttributeAppender ("class",
                                                                                          bValid ? Model.of ("alert alert-success")
                                                                                                 : Model.of ("alert alert-danger"))));
    add (new Label ("details", detailsModel));
  }
}
