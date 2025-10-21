package at.ebinterface.web2.servlet;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.photon.ajax.servlet.PhotonAjaxServlet;
import com.helger.photon.api.servlet.PhotonAPIServlet;
import com.helger.photon.core.go.GoServlet;
import com.helger.photon.core.resource.ResourceBundleServlet;
import com.helger.photon.core.servlet.AbstractObjectDeliveryHttpHandler;
import com.helger.photon.core.servlet.StreamServlet;
import com.helger.servlet.filter.CharacterEncodingFilter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

public class AppServletContainerInitializer implements ServletContainerInitializer
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AppServletContainerInitializer.class);

  public static void registerServlets (@Nonnull final ServletContext aSC)
  {
    ValueEnforcer.notNull (aSC, "ServletContext");

    // Check SC version
    if (aSC.getMajorVersion () < 3)
      throw new IllegalStateException ("At least servlet version 3 is required! Currently running version " +
                                       aSC.getMajorVersion () +
                                       "." +
                                       aSC.getMinorVersion ());

    LOGGER.info ("Registering default listeners and servlets");

    {
      final FilterRegistration.Dynamic aFilter = aSC.addFilter ("CharacterEncodingFilter",
                                                                CharacterEncodingFilter.class);
      if (aFilter != null)
      {
        // Filter is new
        aFilter.setAsyncSupported (true);
        aFilter.setInitParameter (CharacterEncodingFilter.INITPARAM_ENCODING, StandardCharsets.UTF_8.name ());
        aFilter.setInitParameter (CharacterEncodingFilter.INITPARAM_FORCE_ENCODING, Boolean.TRUE.toString ());
        aFilter.addMappingForUrlPatterns (null, false, "/*");
      }
    }

    {
      final ServletRegistration.Dynamic aServlet = aSC.addServlet ("PhotonAjaxServlet", PhotonAjaxServlet.class);
      if (aServlet != null)
      {
        aServlet.setAsyncSupported (true);
        aServlet.addMapping (PhotonAjaxServlet.SERVLET_DEFAULT_PATH + "/*");
      }
    }

    {
      final ServletRegistration.Dynamic aServlet = aSC.addServlet ("PhotonAPIServlet", PhotonAPIServlet.class);
      if (aServlet != null)
      {
        aServlet.setAsyncSupported (true);
        aServlet.addMapping (PhotonAPIServlet.SERVLET_DEFAULT_PATH + "/*");
      }
    }

    {
      final ServletRegistration.Dynamic aServlet = aSC.addServlet ("StreamServlet", StreamServlet.class);
      if (aServlet != null)
      {
        aServlet.setAsyncSupported (true);
        aServlet.setInitParameter (AbstractObjectDeliveryHttpHandler.INITPARAM_ALLOWED_EXTENSIONS,
                                   AbstractObjectDeliveryHttpHandler.EXTENSION_MACRO_WEB_DEFAULT);
        aServlet.addMapping (StreamServlet.SERVLET_DEFAULT_PATH + "/*");
      }
    }

    {
      final ServletRegistration.Dynamic aServlet = aSC.addServlet ("ResourceBundleServlet",
                                                                   ResourceBundleServlet.class);
      if (aServlet != null)
      {
        aServlet.setAsyncSupported (true);
        aServlet.setInitParameter (AbstractObjectDeliveryHttpHandler.INITPARAM_ALLOWED_EXTENSIONS, "js,css");
        aServlet.addMapping (ResourceBundleServlet.SERVLET_DEFAULT_PATH + "/*");
      }
    }

    {
      final ServletRegistration.Dynamic aServlet = aSC.addServlet ("GoServlet", GoServlet.class);
      if (aServlet != null)
      {
        aServlet.setAsyncSupported (true);
        aServlet.addMapping (GoServlet.SERVLET_DEFAULT_PATH + "/*");
      }
    }

    LOGGER.info ("Finished registering default listeners and servlets");
  }

  public void onStartup (@Nonnull final Set <Class <?>> aClasses,
                         @Nonnull final ServletContext aSC) throws ServletException
  {
    registerServlets (aSC);
  }
}
