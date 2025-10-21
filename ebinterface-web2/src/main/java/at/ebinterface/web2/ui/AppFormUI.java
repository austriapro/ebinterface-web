package at.ebinterface.web2.ui;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.string.StringHelper;
import com.helger.photon.app.url.LinkHelper;
import com.helger.photon.core.userdata.UserUploadServlet;
import com.helger.photon.core.userdata.UserUploadXServletHandler;
import com.helger.photon.uicore.page.IWebPageExecutionContext;
import com.helger.photon.uictrls.fineupload.FineUploader;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class AppFormUI
{
  private AppFormUI ()
  {}

  @Nonnull
  public static FineUploader createFineUploader (@Nonnull final IWebPageExecutionContext aWPEC,
                                                 @Nullable final String sDirectory,
                                                 @Nonnull @Nonempty final String sID)
  {
    final FineUploader aFU = new FineUploader (aWPEC.getDisplayLocale ());
    aFU.setDebug (GlobalDebug.isDebugMode ())
       .setEndpoint (LinkHelper.getURLWithContext (aWPEC.getRequestScope (), UserUploadServlet.SERVLET_DEFAULT_PATH))
       .setMultiple (false)
       .setAutoUpload (false)
       .setForceMultipart (true)
       .setMaxConnections (1)
       .setInputName (UserUploadXServletHandler.PARAM_FILE);

    // Additional parameters
    if (StringHelper.isNotEmpty (sDirectory))
      aFU.addParam (UserUploadXServletHandler.PARAM_DIRECTORY, sDirectory);
    aFU.addParam (UserUploadXServletHandler.PARAM_ID, sID);
    return aFU;
  }
}
