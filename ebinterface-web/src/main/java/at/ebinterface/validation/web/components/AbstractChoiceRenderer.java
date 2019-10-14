package at.ebinterface.validation.web.components;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * Abstract base implementation of {@link IChoiceRenderer}. Introduced when
 * migrating from Wicket 6 to Wicket 7.
 *
 * @author Philip Helger
 * @param <T> Data type
 */
public abstract class AbstractChoiceRenderer <T> implements IChoiceRenderer <T>
{
  @Override
  public T getObject (final String id, final IModel <? extends List <? extends T>> choices)
  {
    final List <? extends T> aChoices = choices.getObject ();
    for (int i = 0; i < aChoices.size (); i++)
    {
      // Get next choice
      final T aChoice = aChoices.get (i);
      if (getIdValue (aChoice, i).equals (id))
        return aChoice;
    }
    return null;
  }
}
