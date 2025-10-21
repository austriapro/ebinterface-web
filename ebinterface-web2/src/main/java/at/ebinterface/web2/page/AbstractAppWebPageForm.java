package at.ebinterface.web2.page;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPageForm;
import com.helger.photon.uicore.page.WebPageExecutionContext;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
    return null;
  }
}
