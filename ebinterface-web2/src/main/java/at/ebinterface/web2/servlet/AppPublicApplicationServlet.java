package at.ebinterface.web2.servlet;

import com.helger.photon.app.html.IHTMLProvider;
import com.helger.photon.core.servlet.AbstractApplicationXServletHandler;
import com.helger.photon.core.servlet.AbstractPublicApplicationServlet;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import at.ebinterface.web2.ui.AppLayoutHTMLProvider;
import at.ebinterface.web2.ui.AppRendererPublic;

public final class AppPublicApplicationServlet extends AbstractPublicApplicationServlet
{
  public AppPublicApplicationServlet ()
  {
    super (new AbstractApplicationXServletHandler ()
    {
      @Override
      protected IHTMLProvider createHTMLProvider (final IRequestWebScopeWithoutResponse aRequestScope)
      {
        return new AppLayoutHTMLProvider (AppRendererPublic::getContent);
      }
    });
  }
}
