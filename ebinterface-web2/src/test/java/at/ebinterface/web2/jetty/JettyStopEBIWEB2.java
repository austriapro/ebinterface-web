package at.ebinterface.web2.jetty;

import java.io.IOException;

import com.helger.photon.jetty.JettyStopper;

public final class JettyStopEBIWEB2
{
  public static void main (final String [] args) throws IOException
  {
    new JettyStopper ().run ();
  }
}
