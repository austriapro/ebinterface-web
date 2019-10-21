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

import at.ebinterface.validation.dto.SignatureValidationResult;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.panels.SignatureDetailsPanel;

public final class ResultPanel extends Panel {
  public ResultPanel(final String id,
                     final ValidationResult validationResult,
                     final byte[] pdf,
                     final byte[] xml,
                     final String log,
                     @Nullable final Class<? extends WebPage> returnPage) {
    super(id);

    final StringBuilder schemaVersion = new StringBuilder();

    Label schemaVersionLabel;
    Label schemaVersionLabelNoOk;
    if (validationResult.getDeterminedEbInterfaceVersion() != null) {
      schemaVersion.append(validationResult.getDeterminedEbInterfaceVersion().getCaption());

      if (validationResult.getDeterminedEbInterfaceVersion().supportsSigning()) {
        if (validationResult.getDeterminedEbInterfaceVersion().isSigned()) {
          schemaVersion.append(" (signiert)");
        } else {
          schemaVersion.append(" (unsigniert)");
        }
      }
    }

    if (schemaVersion.length() > 0) {
      //Add a label with the schema version
      schemaVersionLabel = new Label("schemaVersion", Model.of(schemaVersion.toString()));
      schemaVersionLabelNoOk = new Label("schemaVersion", Model.of(schemaVersion.toString()));
    } else {
      schemaVersionLabel =
          new Label("schemaVersion", Model.of("Es wurde keine gültige Version erkannt."));
      schemaVersionLabelNoOk =
          new Label("schemaVersion", Model.of("Es wurde keine gültige Version erkannt."));
    }

    //Schema OK Container
    final WebMarkupContainer schemaOkContainer = new WebMarkupContainer("schemvalidationOK");
    schemaOkContainer.add(schemaVersionLabel);
    add(schemaOkContainer);

    //Schema NOK Container
    final WebMarkupContainer schemaNOkContainer = new WebMarkupContainer("schemvalidationNOK");
    schemaNOkContainer.add(schemaVersionLabelNoOk);
    schemaNOkContainer.add(new Label("schemaValidationError",
                                     Model.of(validationResult.getSchemaValidationErrorMessage())));
    add(schemaNOkContainer);

    //Schema is OK
    if (StringHelper.hasNoText(validationResult.getSchemaValidationErrorMessage())) {
      schemaOkContainer.setVisible(true);
      schemaNOkContainer.setVisible(false);
    }
    //Schema NOK
    else {
      schemaOkContainer.setVisible(false);
      schemaNOkContainer.setVisible(true);
    }

    //Signature result container
    final WebMarkupContainer
        signatureResultContainer =
        new WebMarkupContainer("signatureResultContainer");
    //If no signature is applied we do not show the containers
    if (validationResult.getDeterminedEbInterfaceVersion() == null || !validationResult
        .getDeterminedEbInterfaceVersion().isSigned()) {
      signatureResultContainer.setVisibilityAllowed(false);
    }

    //Get the result details for the signature
    final SignatureValidationResult
        signatureValidationResult =
        new SignatureValidationResult(validationResult.getVerifyDocumentResponse());

    //Signature
    signatureResultContainer.add(new SignatureDetailsPanel("signatureDetails", Model
        .of(signatureValidationResult.getSignatureText()), Model.of(Boolean.valueOf(signatureValidationResult
                                                                        .isSignatureValid()))));

    //Certificate
    signatureResultContainer.add(new SignatureDetailsPanel("certificateDetails", Model
        .of(signatureValidationResult.getCertificateText()), Model.of(Boolean.valueOf(signatureValidationResult
                                                                          .isCertificateValid()))));

    //Manifest
    signatureResultContainer.add(new SignatureDetailsPanel("manifestDetails", Model
        .of(signatureValidationResult.getManifestText()),
                                                           Model.of(Boolean.valueOf(signatureValidationResult
                                                                        .isManifestValid()))));

    add(signatureResultContainer);

    //Log Container
    final WebMarkupContainer mappingContainer = new WebMarkupContainer("mappingLog");
    add(mappingContainer);

    final WebMarkupContainer errorContainer = new WebMarkupContainer("mappingLogError");
    mappingContainer.add(errorContainer);
    errorContainer.setVisible(true);

    if (log != null) {
      mappingContainer.setVisible(true);

      final Label slog = new Label("logErrorPanel", Model.of(new String(log).trim()));
      errorContainer.add(slog.setEscapeModelStrings(false));
    } else {
      mappingContainer.setVisible(false);

      final EmptyPanel slog = new EmptyPanel("logErrorPanel");
      errorContainer.add(slog);
    }

    add(new Link<Object>("returnLink") {
      @Override
      public void onClick() {
        setResponsePage(returnPage);
      }
    }.setVisibilityAllowed(returnPage != null));

    final Link<Void> pdflink = new Link<Void>("linkPDFDownload") {
      @Override
      public void onClick() {
        final AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
          @Override
          public void write(final OutputStream output) throws IOException {
            output.write(pdf);
          }
        };

        final ResourceStreamRequestHandler
            handler = new ResourceStreamRequestHandler(rstream, "ebInterface-Invoice.pdf");
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
      }
    };
    pdflink.setVisible(pdf != null);
    //Add a PDF-download button
    add(pdflink);

    final Link<Void> xmllink = new Link<Void>("linkXMLDownload") {
      @Override
      public void onClick() {
        final AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
          @Override
          public void write(final OutputStream output) throws IOException {
            output.write(xml);
          }
        };

        final ResourceStreamRequestHandler
            handler = new ResourceStreamRequestHandler(rstream, "ebInterface-Invoice.xml");
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
      }
    };
    xmllink.setVisible(xml != null);
    //Add a PDF-download button
    add(xmllink);
  }
}
