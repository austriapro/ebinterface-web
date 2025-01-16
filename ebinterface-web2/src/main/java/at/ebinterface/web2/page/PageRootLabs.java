package at.ebinterface.web2.page;

import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public class PageRootLabs extends AbstractAppWebPage
{
  public PageRootLabs (final String sID)
  {
    super (sID, "labs.ebinterface.at");
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    aNodeList.addChild (h1 ("Labs"));
  }
}
