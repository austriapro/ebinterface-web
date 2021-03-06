package at.ebinterface.validation.web.pages.convert.result;

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

public final class ResultPanelXRechnungToEbi extends Panel
{
  public ResultPanelXRechnungToEbi (final String id,
                                    final byte [] pdf,
                                    final byte [] xml,
                                    final String log,
                                    @Nullable final Class <? extends WebPage> returnPage)
  {
    super (id);

    // Log Container
    final WebMarkupContainer mappingContainer = new WebMarkupContainer ("mappingLog");
    add (mappingContainer);

    final WebMarkupContainer errorContainer = new WebMarkupContainer ("mappingLogError");
    mappingContainer.add (errorContainer);
    errorContainer.setVisible (true);

    if (StringHelper.hasText (log))
    {
      mappingContainer.setVisible (true);

      final Label slog = new Label ("logErrorPanel", Model.of (new String (log).trim ()));
      errorContainer.add (slog.setEscapeModelStrings (false));
    }
    else
    {
      mappingContainer.setVisible (false);

      final EmptyPanel slog = new EmptyPanel ("logErrorPanel");
      errorContainer.add (slog);
    }

    add (new Link <Object> ("returnLink")
    {
      @Override
      public void onClick ()
      {
        setResponsePage (returnPage);
      }
    }.setVisibilityAllowed (returnPage != null));

    final Link <Void> pdflink = new Link <Void> ("linkPDFDownload")
    {
      @Override
      public void onClick ()
      {
        final AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter ()
        {
          @Override
          public void write (final OutputStream output) throws IOException
          {
            output.write (pdf);
          }
        };

        final ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler (rstream, "ebInterface-Invoice.pdf");
        getRequestCycle ().scheduleRequestHandlerAfterCurrent (handler);
      }
    };
    pdflink.setVisible (pdf != null);
    // Add a PDF-download button
    add (pdflink);

    final Link <Void> xmllink = new Link <Void> ("linkXMLDownload")
    {
      @Override
      public void onClick ()
      {
        final AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter ()
        {
          @Override
          public void write (final OutputStream output) throws IOException
          {
            output.write (xml);
          }
        };

        final ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler (rstream, "ebInterface-Invoice.xml");
        getRequestCycle ().scheduleRequestHandlerAfterCurrent (handler);
      }
    };
    xmllink.setVisible (xml != null);
    // Add a PDF-download button
    add (xmllink);
  }
}
