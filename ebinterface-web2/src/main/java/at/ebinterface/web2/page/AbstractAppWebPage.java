package at.ebinterface.web2.page;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.html.hc.IHCNode;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public abstract class AbstractAppWebPage extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  public AbstractAppWebPage (@Nonnull @Nonempty final String sID, @Nonnull final String sName)
  {
    super (sID, sName);
  }

  @Nonnull
  protected static IHCNode _unescapeHTML (final String s)
  {
    return AbstractAppWebPageForm._unescapeHTML (s);
  }
}
