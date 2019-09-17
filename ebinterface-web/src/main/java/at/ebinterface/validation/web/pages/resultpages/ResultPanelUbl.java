package at.ebinterface.validation.web.pages.resultpages;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;

import com.helger.commons.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;

public class ResultPanelUbl extends Panel
{
  public ResultPanelUbl (String id, final byte [] xml, final String sErrorLog, @Nullable Class <? extends WebPage> returnPage)
  {
    super (id);

    // Log Containers
    final WebMarkupContainer mappingLogSuccessContainer = new WebMarkupContainer ("mappingLogSuccess");
    add (mappingLogSuccessContainer);
    
    final WebMarkupContainer mappingLogErrorContainer = new WebMarkupContainer ("mappingLogError");
    add (mappingLogErrorContainer);
    

    if (StringHelper.hasText (sErrorLog))
    {
      mappingLogSuccessContainer.setVisible (false);
      mappingLogErrorContainer.setVisible (true);

      Label slog = new Label ("logErrorPanel", Model.of (sErrorLog.trim ()));
      mappingLogErrorContainer.add (slog.setEscapeModelStrings (false));
    }
    else
    {
      mappingLogSuccessContainer.setVisible (true);
      mappingLogErrorContainer.setVisible (false);
    }

    Link <Void> xmllink = new Link <Void> ("linkXMLDownload")
    {
      @Override
      public void onClick ()
      {
        AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter ()
        {
          @Override
          public void write (OutputStream output) throws IOException
          {
            output.write (xml);
          }
        };

        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler (rstream, "UBL-Invoice.xml");
        getRequestCycle ().scheduleRequestHandlerAfterCurrent (handler);
      }
    };
    xmllink.setVisible (xml != null);
    add (xmllink);

    add (new Link <Object> ("returnLink")
    {
      @Override
      public void onClick ()
      {
        setResponsePage (returnPage);
      }
    }.setVisibilityAllowed (returnPage != null));
  }
}
