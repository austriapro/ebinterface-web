package at.ebinterface.validation.web.pages.resultpages;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

import at.austriapro.rendering.BaseRenderer;
import at.ebinterface.validation.dto.SignatureValidationResult;
import at.ebinterface.validation.validator.ValidationResult;
import at.ebinterface.validation.validator.jaxb.Result;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.BasePage;
import at.ebinterface.validation.web.pages.StartPage;
import at.ebinterface.validation.web.pages.StartPage.ActionType;
import at.ebinterface.validation.web.panels.ErrorDetailsPanel;
import at.ebinterface.validation.web.panels.SignatureDetailsPanel;

/**
 * Used to show the results of a validation
 *
 * @author pl
 */
public class ResultPageZugferd extends BasePage {
  private static final Logger LOG = LoggerFactory.getLogger(ResultPageZugferd.class.getName());

  /**
   * Create a new result page
   */
  public ResultPageZugferd(final ValidationResult validationResult,
                           final String selectedSchematron,
                           final ActionType selectedAction,
                           final byte[] zugferdXml,
                           final String mappingLog,
                           final byte[] zugferdPdf) {

    final StringBuffer schemaVersion = new StringBuffer();

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
    if (StringUtils.isEmpty(validationResult.getSchemaValidationErrorMessage())) {
      schemaOkContainer.setVisible(true);
      schemaNOkContainer.setVisible(false);
    }
    //Schema NOK
    else {
      schemaOkContainer.setVisible(false);
      schemaNOkContainer.setVisible(true);
    }

    //Signature result container
    WebMarkupContainer
        signatureResultContainer =
        new WebMarkupContainer("signatureResultContainer");
    //If no signature is applied we do not show the containers
    if (validationResult.getDeterminedEbInterfaceVersion() == null || !validationResult
        .getDeterminedEbInterfaceVersion().isSigned()) {
      signatureResultContainer.setVisibilityAllowed(false);
    }

    //Get the result details for the signature
    SignatureValidationResult
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

    //Schematron OK Container
    final WebMarkupContainer schematronOkContainer = new WebMarkupContainer("schematronOK");

    //Add a label with the selected Schematron
    schematronOkContainer.add(new Label("selectedSchematron", Model.of(selectedSchematron)));
    add(schematronOkContainer);

    //Schematron NOK Container
    final WebMarkupContainer schematronNokContainer = new WebMarkupContainer("schematronNOK");

    schematronNokContainer.add(new Label("selectedSchematron", Model.of(selectedSchematron)));

    final Result schematronResult = validationResult.getResult();

    //Add schematron error messages if there some
    if (schematronResult == null || schematronResult.getErrors() == null
        || schematronResult.getErrors().size() == 0) {
      schematronNokContainer.add(new EmptyPanel("errorDetailsPanel"));
    } else {
      schematronNokContainer
          .add(new ErrorDetailsPanel("errorDetailsPanel", schematronResult.getErrors()));
    }

    add(schematronNokContainer);

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

      Label slog = new Label("zugferdLogSuccessPanel", Model.of(new String(mappingLog).trim()));
      zugferdSuccessContainer.add(slog.setEscapeModelStrings(false));
      EmptyPanel elog = new EmptyPanel("zugferdLogErrorPanel");
      zugferdErrorContainer.add(elog);
    } else {
      zugferdErrorContainer.setVisible(true);
      zugferdSuccessContainer.setVisible(false);

      EmptyPanel slog = new EmptyPanel("zugferdLogSuccessPanel");
      zugferdSuccessContainer.add(slog);
      Label elog = new Label("zugferdLogErrorPanel", Model.of(new String(mappingLog).trim()));
      zugferdErrorContainer.add(elog.setEscapeModelStrings(false));
    }

    //In case the Schema validation failed, or only schema validation is turned on we do not show anything about the schematron
    if (selectedAction == ActionType.SCHEMA_VALIDATION || !StringUtils
        .isEmpty(validationResult.getSchemaValidationErrorMessage())) {
      schematronOkContainer.setVisible(false);
      schematronNokContainer.setVisible(false);
    }
    //Are there schematron validation messages?
    //Everything OK
    else if (schematronResult == null || schematronResult.getErrors() == null
             || schematronResult.getErrors().size() == 0) {
      schematronOkContainer.setVisible(true);
      schematronNokContainer.setVisible(false);
    }
    //NOK
    else {
      schematronOkContainer.setVisible(false);
      schematronNokContainer.setVisible(true);
    }

    add(new Link<Object>("returnLink") {
      @Override
      public void onClick() {
        setResponsePage(StartPage.class);
      }
    });

    Link<Void> zugferdxmllink = new Link<Void>("linkZugferdXMLDownload") {
      @Override
      public void onClick() {
        AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
          @Override
          public void write(OutputStream output) throws IOException {
            output.write(zugferdXml);
          }
        };

        ResourceStreamRequestHandler
            handler = new ResourceStreamRequestHandler(rstream, "ZUGFeRD.xml");
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
      }
    };
    zugferdxmllink.setVisible(zugferdXml != null);
    //Add a PDF-download button
    add(zugferdxmllink);

    Link<Void> zugferdpdflink = new Link<Void>("linkZugferdPDFDownload") {
      @Override
      public void onClick() {
        AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
          @Override
          public void write(OutputStream output) throws IOException {
            output.write(zugferdPdf);
          }
        };

        ResourceStreamRequestHandler
            handler = new ResourceStreamRequestHandler(rstream, "ZUGFeRD.pdf");
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
      }
    };
    zugferdpdflink.setVisible(zugferdPdf != null);
    //Add a PDF-download button
    add(zugferdpdflink);
  }
}

