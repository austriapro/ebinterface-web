package at.ebinterface.web2.page;

import com.helger.annotation.Nonempty;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.uicore.page.WebPageExecutionContext;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class AbstractAppWebPage extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  protected static final ICSSClassProvider CSS_CLASS_BTN_WKO = DefaultCSSClassProvider.create ("btn-wko");
  protected static final ICSSClassProvider CSS_CLASS_SECTION_LINE = DefaultCSSClassProvider.create ("section-line");
  protected static final ICSSClassProvider CSS_CLASS_INLINE_BTN = DefaultCSSClassProvider.create ("inline-btn");

  protected static final ICSSClassProvider CSS_CLASS_UPLOAD_FILE = DefaultCSSClassProvider.create ("uploadFile");
  protected static final ICSSClassProvider CSS_CLASS_INPUT_FILE = DefaultCSSClassProvider.create ("inputFile");
  protected static final ICSSClassProvider CSS_CLASS_UPLOAD_LABEL = DefaultCSSClassProvider.create ("upload-label");

  public AbstractAppWebPage (@Nonnull @Nonempty final String sID, @Nonnull final String sName)
  {
    super (sID, sName);
  }

  @Override
  @Nullable
  public String getHeaderText (@Nonnull final WebPageExecutionContext aWPEC)
  {
    return null;
  }
}
