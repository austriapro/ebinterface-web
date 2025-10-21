package at.ebinterface.web2.servlet;

import com.helger.http.EHttpMethod;
import com.helger.photon.core.servlet.AbstractPublicApplicationServlet;
import com.helger.photon.core.servlet.RootXServletHandler;
import com.helger.xservlet.AbstractXServlet;

public class AppRootServlet extends AbstractXServlet
{
  public AppRootServlet ()
  {
    handlerRegistry ().registerHandler (EHttpMethod.GET, new RootXServletHandler (AbstractPublicApplicationServlet.SERVLET_DEFAULT_PATH));
    handlerRegistry ().copyHandlerToAll (EHttpMethod.GET);
  }
}
