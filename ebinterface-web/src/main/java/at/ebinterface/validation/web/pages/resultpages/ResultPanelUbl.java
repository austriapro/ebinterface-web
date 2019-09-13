package at.ebinterface.validation.web.pages.resultpages;

import java.io.IOException;
import java.io.OutputStream;

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

public class ResultPanelUbl extends Panel
{
  public ResultPanelUbl (String id, final byte [] xml, final String log, @Nullable Class <? extends WebPage> returnPage)
  {
    super (id);


    //Log Container
    final WebMarkupContainer mappingContainer = new WebMarkupContainer("mappingLog");
    add(mappingContainer);

    final WebMarkupContainer errorContainer = new WebMarkupContainer("mappingLogError");
    mappingContainer.add(errorContainer);
    errorContainer.setVisible(true);

    if (StringHelper.hasText (log)) {
      mappingContainer.setVisible(true);

      Label slog = new Label("logErrorPanel", Model.of(new String(log).trim()));
      errorContainer.add(slog.setEscapeModelStrings(false));
    } else {
      mappingContainer.setVisible(false);

      EmptyPanel slog = new EmptyPanel("logErrorPanel");
      errorContainer.add(slog);
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


    add(new Link<Object>("returnLink") {
      @Override
      public void onClick() {
        setResponsePage(returnPage);
      }
    }.setVisibilityAllowed(returnPage != null));
  }
}
