package at.ebinterface.web2.page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.name.IHasDisplayName;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPageForm;
import com.helger.photon.uicore.UITextFormatter;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public abstract class AbstractAppWebPageForm <DATATYPE extends IHasID <String>> extends
                                            AbstractBootstrapWebPageForm <DATATYPE, WebPageExecutionContext>
{
  public AbstractAppWebPageForm (@Nonnull @Nonempty final String sID, @Nonnull final String sName)
  {
    super (sID, sName);
    setObjectLockingEnabled (true);
  }

  @Override
  @Nullable
  public String getHeaderText (@Nonnull final WebPageExecutionContext aWPEC)
  {
    String ret = super.getHeaderText (aWPEC);
    final DATATYPE aSelectedObject = getSelectedObject (aWPEC, getSelectedObjectID (aWPEC));
    if (aSelectedObject instanceof IHasDisplayName)
    {
      final String sName = ((IHasDisplayName) aSelectedObject).getDisplayName ();
      if (StringHelper.hasText (sName))
        ret += " - " + sName;
    }
    return ret;
  }

  @Nonnull
  protected static IHCNode _unescapeHTML (final String s)
  {
    final IHCNode ret = UITextFormatter.unescapeHTML (s);
    return new HCSpan ().addChild (ret);
  }
}
