package at.ebinterface.validation.web.pages.resultpages;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;

import com.helger.commons.string.StringHelper;

import at.ebinterface.validation.dto.SignatureValidationResult;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.web.pages.BasePage;
import at.ebinterface.validation.web.pages.StartPage;
import at.ebinterface.validation.web.panels.SignatureDetailsPanel;

/**
 * Used to show the results of a validation
 *
 * @author pl
 */
public final class ResultPageZugferd extends BasePage {
  public ResultPageZugferd(final ValidationResult validationResult,
                           final byte[] zugferdXml,
                           final String mappingLog,
                           final byte[] zugferdPdf) {

    final StringBuilder schemaVersion = new StringBuilder();

    if (validationResult.getDeterminedEbInterfaceVersion() != null) {
      schemaVersion.append(validationResult.getDeterminedEbInterfaceVersion().getCaption());
      if (validationResult.getDeterminedEbInterfaceVersion().isSigned()) {
        schemaVersion.append(" (signiert)");
      } else {
        schemaVersion.append(" (unsigniert)");
      }
    }

    //Add a label with the schema version
    add(new Label("schemaVersion", Model.of(schemaVersion.toString())));

    //Schema OK Container
    final WebMarkupContainer schemaOkContainer = new WebMarkupContainer("schemvalidationOK");
    add(schemaOkContainer);

    //Schema NOK Container
    final WebMarkupContainer schemaNOkContainer = new WebMarkupContainer("schemvalidationNOK");
    schemaNOkContainer.add(new Label("schemaValidationError",
                                     Model.of(validationResult.getSchemaValidationErrorMessage())));
    add(schemaNOkContainer);

    //Is there a schema validation message?
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
        .of(signatureValidationResult.getSignatureText()), Model.of(signatureValidationResult
                                                                        .isSignatureValid())));

    //Certificate
    signatureResultContainer.add(new SignatureDetailsPanel("certificateDetails", Model
        .of(signatureValidationResult.getCertificateText()), Model.of(signatureValidationResult
                                                                          .isCertificateValid())));

    //Manifest
    signatureResultContainer.add(new SignatureDetailsPanel("manifestDetails", Model
        .of(signatureValidationResult.getManifestText()),
                                                           Model.of(signatureValidationResult
                                                                        .isManifestValid())));

    add(signatureResultContainer);

    //ZUGFeRD Container
    final WebMarkupContainer zugferdContainer = new WebMarkupContainer("zugferdMappingLog");
    add(zugferdContainer);

    final WebMarkupContainer zugferdSuccessContainer = new WebMarkupContainer("zugferdMappingLogSuccess");
    zugferdContainer.add(zugferdSuccessContainer);
    final WebMarkupContainer zugferdErrorContainer = new WebMarkupContainer("zugferdMappingLogError");
    zugferdContainer.add(zugferdErrorContainer);

    if (zugferdXml != null){
      zugferdErrorContainer.setVisible(false);
      zugferdSuccessContainer.setVisible(true);

      final Label slog = new Label("zugferdLogSuccessPanel", Model.of(new String(mappingLog).trim()));
      zugferdSuccessContainer.add(slog.setEscapeModelStrings(false));
      final EmptyPanel elog = new EmptyPanel("zugferdLogErrorPanel");
      zugferdErrorContainer.add(elog);
    } else {
      zugferdErrorContainer.setVisible(true);
      zugferdSuccessContainer.setVisible(false);

      final EmptyPanel slog = new EmptyPanel("zugferdLogSuccessPanel");
      zugferdSuccessContainer.add(slog);
      final Label elog = new Label("zugferdLogErrorPanel", Model.of(new String(mappingLog).trim()));
      zugferdErrorContainer.add(elog.setEscapeModelStrings(false));
    }

    add(new Link<Object>("returnLink") {
      @Override
      public void onClick() {
        setResponsePage(StartPage.class);
      }
    });

    final Link<Void> zugferdxmllink = new Link<Void>("linkZugferdXMLDownload") {
      @Override
      public void onClick() {
        final AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
          @Override
          public void write(final OutputStream output) throws IOException {
            output.write(zugferdXml);
          }
        };

        final ResourceStreamRequestHandler
            handler = new ResourceStreamRequestHandler(rstream, "ZUGFeRD.xml");
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
      }
    };
    zugferdxmllink.setVisible(zugferdXml != null);
    //Add a PDF-download button
    add(zugferdxmllink);

    final Link<Void> zugferdpdflink = new Link<Void>("linkZugferdPDFDownload") {
      @Override
      public void onClick() {
        final AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
          @Override
          public void write(final OutputStream output) throws IOException {
            output.write(zugferdPdf);
          }
        };

        final ResourceStreamRequestHandler
            handler = new ResourceStreamRequestHandler(rstream, "ZUGFeRD.pdf");
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
      }
    };
    zugferdpdflink.setVisible(zugferdPdf != null);
    //Add a PDF-download button
    add(zugferdpdflink);
  }
}

