package at.ebinterface.validation.web.standalone;

import java.io.IOException;

import com.helger.photon.jetty.JettyStopper;

public final class JettyStopEBIWEB
{
  public static void main (final String [] args) throws IOException
  {
    new JettyStopper ().run ();
  }
}
