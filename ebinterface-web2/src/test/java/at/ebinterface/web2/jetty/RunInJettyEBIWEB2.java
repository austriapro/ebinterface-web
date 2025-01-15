package at.ebinterface.web2.jetty;

import javax.annotation.concurrent.Immutable;

import com.helger.photon.jetty.JettyStarter;

/**
 * Run as a standalone web application in Jetty on port 8080.<br>
 * http://localhost:8080/
 *
 * @author Philip Helger
 */
@Immutable
public final class RunInJettyEBIWEB2
{
  public static void main (final String [] args) throws Exception
  {
    new JettyStarter (RunInJettyEBIWEB2.class).run ();
  }
}
