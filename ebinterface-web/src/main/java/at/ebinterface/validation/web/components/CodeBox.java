package at.ebinterface.validation.web.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import at.ebinterface.validation.web.components.prettyfy.ExtraJSResourceReference;
import at.ebinterface.validation.web.components.prettyfy.PrettifyCSSResourceReference;
import at.ebinterface.validation.web.components.prettyfy.PrettifyJSResourceReference;

/**
 * A code block which does syntax highlighting of the code contents. Wraps the
 * Prettify library by Google http://code.google.com/p/google-code-prettify/
 * <p>
 * It is not necessary to specify a language, prettify will guess the language
 * based on content, however an override is available should it be required.
 * <p>
 * Apply to a &lt;pre&gt; or &lt;code&gt;
 *
 * @author Richard Nichols
 * @version $Id: CodeBox.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 */
public class CodeBox extends WebComponent
{
  private static final long serialVersionUID = 1L;

  private boolean m_bDisplayLineNumbers = false;
  private CodeBoxLanguage m_eLanguageOverride;

  /**
   * Create a Codebox with static content with the given `id`.
   *
   * @param id
   *        ID
   */
  public CodeBox (final String id)
  {
    super (id);
  }

  /**
   * Create a Codebox with the provided code content and the given `id`.
   *
   * @param id
   *        ID
   * @param code
   *        source code to display
   */
  public CodeBox (final String id, final String code)
  {
    this (id, new Model <> (code));
  }

  /**
   * Create a codebox with source code provided by an `IModel` and the given
   * `id`.
   *
   * @param id
   *        ID
   * @param model
   *        a model that will provide the source code to display
   */
  public CodeBox (final String id, final IModel <?> model)
  {
    super (id, model);
  }

  /**
   * Override and return false to suppress static JavaScript and CSS
   * contributions. (May be desired if you are concatenating / compressing
   * resources as part of build process)
   *
   * @return true
   */
  protected boolean autoAddToHeader ()
  {
    return true;
  }

  @Override
  public void renderHead (final IHeaderResponse response)
  {
    if (autoAddToHeader ())
    {
      response.render (CssHeaderItem.forReference (new PrettifyCSSResourceReference ()));
      response.render (JavaScriptHeaderItem.forReference (new PrettifyJSResourceReference ()));
    }
    if (getLanguageOverride () != null && getLanguageOverride ().getExtraJSfile () != null)
    {
      response.render (JavaScriptHeaderItem.forReference (new ExtraJSResourceReference (getLanguageOverride ())));
    }
    response.render (OnDomReadyHeaderItem.forScript ("prettyPrint()"));
  }

  @Override
  protected void onComponentTag (final ComponentTag tag)
  {
    super.onComponentTag (tag);
    // check applied to code/pre (need to bring some code in from parent as
    // can apply to either)
    if (!tag.getName ().equalsIgnoreCase ("pre") && !tag.getName ().equalsIgnoreCase ("code"))
    {
      findMarkupStream ().throwMarkupException ("Component " +
                                                getId () +
                                                " must be applied to a tag of type 'code' or 'pre', not " +
                                                tag.toUserDebugString ());
    }
    // change display class
    if (getLanguageOverride () == null)
    {
      tag.put ("class", "prettyprint");
    }
    else
    {
      tag.put ("class", "prettyprint " + getLanguageOverride ().getCSSClass ());
    }
  }

  @Override
  public void onComponentTagBody (final MarkupStream markupStream, final ComponentTag openTag)
  {
    String code = this.getDefaultModelObjectAsString ();
    if (code != null)
    {
      if (isDisplayLineNumbers ())
      {
        code = _formatLineNumbers (code);
      }
      replaceComponentTagBody (markupStream, openTag, code);
    }
    else
    {
      super.onComponentTagBody (markupStream, openTag);
    }
  }

  private static String _formatLineNumbers (final String code)
  {
    final StringBuilder codeWithLines = new StringBuilder (code.length () * 2);
    final String [] lines = code.split ("\n");
    final int numPlaces = Integer.toString (lines.length).length ();
    int lineNo = 1;
    for (final String line : lines)
    {
      codeWithLines.append ("<span class=\"nocode\">");
      codeWithLines.append (_rightJustifyAndPad (lineNo++, numPlaces));
      codeWithLines.append (":</span> ");
      codeWithLines.append (line);
      codeWithLines.append ('\n');
    }
    return codeWithLines.toString ();
  }

  public boolean isDisplayLineNumbers ()
  {
    return m_bDisplayLineNumbers;
  }

  /**
   * Toggle the display of line numbers in the left gutter.
   *
   * @param displayLineNumbers
   *        toggle
   * @return this
   */
  public CodeBox setDisplayLineNumbers (final boolean displayLineNumbers)
  {
    m_bDisplayLineNumbers = displayLineNumbers;
    return this;
  }

  public CodeBoxLanguage getLanguageOverride ()
  {
    return m_eLanguageOverride;
  }

  /**
   * Override the language used for syntax highlighting.
   *
   * @param languageOverride
   *        language
   * @return this
   */
  public CodeBox setLanguageOverride (final CodeBoxLanguage languageOverride)
  {
    m_eLanguageOverride = languageOverride;
    return this;
  }

  private static String _rightJustifyAndPad (final int lineNo, final int places)
  {
    final StringBuilder result = new StringBuilder (places);
    result.append (lineNo);
    while (result.length () < places)
    {
      result.insert (0, ' ');
    }
    return result.toString ();
  }
}
