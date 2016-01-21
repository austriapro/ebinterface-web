package at.ebinterface.validation.web.pages;

import com.helger.commons.errorlist.ErrorList;
import com.helger.commons.errorlist.IError;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.inmemory.ReadableResourceByteArray;
import com.helger.ebinterface.EbInterface41Marshaller;
import com.helger.ebinterface.ubl.from.invoice.InvoiceToEbInterface41Converter;
import com.helger.ebinterface.v41.Ebi41InvoiceType;
import com.helger.ubl21.UBL21Reader;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.saxon.Controller;
import net.sf.saxon.serialize.MessageWarner;

import at.austriapro.Mapping;
import at.austriapro.MappingErrorHandler;
import at.austriapro.MappingErrorListener;
import at.austriapro.MappingFactory;
import at.austriapro.rendering.BaseRenderer;
import at.austriapro.rendering.ZugferdRenderer;
import at.ebinterface.validation.validator.*;
import at.ebinterface.validation.validator.jaxb.Result;
import at.ebinterface.validation.web.Constants;
import at.ebinterface.validation.web.pages.resultpages.ResultPageEbInterface;
import at.ebinterface.validation.web.pages.resultpages.ResultPageZugferd;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

/**
 * First page of the ebInterface Validation Service
 *
 * @author pl
 */
public class StartPage extends BasePage {

  /**
   * The three possible actions
   */
  public enum ActionType {
    SCHEMA_VALIDATION, SCHEMA_AND_SCHEMATRON_VALIDATION, VISUALIZATION_HTML, VISUALIZATION_PDF, CONVERSION_ZUGFERD
  }

  //choices in dropdown box ZUGFeRD
  private static final List<String> ZUGFERD_LEVELS = Arrays.asList(new String[]{
      "ZUGFeRD (1.0) Basic", "ZUGFeRD (1.0) Comfort", "ZUGFeRD (1.0) Extended"});

  private static final Logger LOG = LoggerFactory.getLogger(StartPage.class.getName());

  /**
   * Construc the start page
   */
  public StartPage() {

    //Add the input form
    final InputForm inputForm = new InputForm("inputForm");
    add(inputForm);

    //Add the form for showing the supported rules
    final ShowRulesForm showRulesForm = new ShowRulesForm("showRulesForm");
    add(showRulesForm);

    //Add the form for showing the supported rules
    final UblForm ublForm = new UblForm("ublForm");
    add(ublForm);


  }

  /**
   * Form for showing the rules which are currently supported
   *
   * @author pl
   */
  private class ShowRulesForm extends Form {

    /**
     * Panel for providing feedback in case of errorneous input
     */
    FeedbackPanel feedbackPanel;

    /**
     * Dropdown choice for the schmeatrno rules
     */
    DropDownChoice<Rule> rules;

    public ShowRulesForm(final String id) {
      super(id);

      //Add a feedback panel
      feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
      feedbackPanel.setVisible(false);
      add(feedbackPanel);

      //Add the drop down choice for the different rules which are currently supported
      rules =
          new DropDownChoice<Rule>("ruleSelector", Model.of(new Rule()), Rules.getRules(),
                                   new IChoiceRenderer<Rule>() {
                                     @Override
                                     public Object getDisplayValue(Rule object) {
                                       return object.getName();
                                     }

                                     @Override
                                     public String getIdValue(Rule object, int index) {
                                       return object.getName();
                                     }
                                   });

      add(rules);

      //Add a submit button
      add(new SubmitLink("showSchematron"));
    }

    @Override
    protected void onSubmit() {
      super.onSubmit();

      //Did the user select a schematron file?
      if (rules.getModelObject() == null || rules.getModelObject().toString().equals("")) {
        error(new ResourceModel("ruleSelector.NoSelected").getObject());
        onError();
        return;
      }

      //Redirect
      setResponsePage(new ShowRulesPage(rules.getModel()));
    }


    /**
     * Process errors
     */
    @Override
    protected void onError() {
      //Show the feedback panel in case on an error
      feedbackPanel.setVisible(true);
    }
  }

  /**
   * Form for showing the rules which are currently supported
   *
   * @author pl
   */
  private class UblForm extends Form {

    /**
     * Panel for providing feedback in case of errorneous input
     */
    FeedbackPanel feedbackPanel;

    /**
     * Upload field for the ebInterface instance
     */
    FileUploadField fileUploadField;

    public UblForm(final String id) {
      super(id);

      //Add a feedback panel
      feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
      feedbackPanel.setVisible(false);
      add(feedbackPanel);

      //Add the file upload field
      fileUploadField = new FileUploadField("ublInput");
      fileUploadField.setRequired(true);
      add(fileUploadField);

      //Add a submit button
      add(new SubmitLink("convertUbl"));
    }

    @Override
    protected void onSubmit() {
      super.onSubmit();

      feedbackPanel.setVisible(false);

      //Get the file input
      final FileUpload upload = fileUploadField.getFileUpload();
      byte[] uploadedData = null;

      try {
        final InputStream inputStream = upload.getInputStream();
        uploadedData = IOUtils.toByteArray(inputStream);
      } catch (final IOException e) {
        LOG.error("Die hochgeladene Datei kann nicht verarbeitet werden.", e);
      }

      // Read UBL
      final InvoiceType aUBLInvoice = UBL21Reader.invoice().read(uploadedData);

      if (aUBLInvoice == null){
        error(
            "Das UBL kann nicht verarbeitet werden.");
        onError();
        return;
      }

      // Convert to ebInterface
      final ErrorList aErrorList = new ErrorList ();
      final Ebi41InvoiceType aEbInvoice = new InvoiceToEbInterface41Converter(Locale.GERMANY,
                                                                              Locale.GERMANY,
                                                                              false).convertToEbInterface (aUBLInvoice, aErrorList);
      byte[] ebInterface = null;
      ValidationResult
          validationResult = null;
      byte[] pdf = null;

      StringBuilder sbLog = new StringBuilder();

      if(aErrorList.hasErrorsOrWarnings()) {
        validationResult = new ValidationResult();
        validationResult.setSchemaValidationErrorMessage("Die Schemavalidierung konnte nicht durchgeführt werden.");

        sbLog.append("<b>Bei der UBL - ebInterfacekonvertierung sind folgende Fehler aufgetreten:</b><br/>");
        for (IError error : aErrorList.getAllItems()){
          sbLog.append(error.getErrorFieldName()).append(":<br/>").append(error.getErrorText()).append("<br/><br/>");
        }
      } else {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        new EbInterface41Marshaller().write(aEbInvoice, bo);

        ebInterface = bo.toByteArray();

        //Validate the XML instance - performed in any case
        final EbInterfaceValidator validator = Application.get().getMetaData(
            Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
        validationResult =
            validator.validateXMLInstanceAgainstSchema(ebInterface);

        if (validationResult.getDeterminedEbInterfaceVersion() == null) {
          error(
              "Das konvertierte XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
          onError();
          return;
        }

        BaseRenderer renderer = new BaseRenderer();

        try {
          LOG.debug("Load ebInterface JasperReport template from application context.");
          JasperReport
              jrReport =
              Application.get().getMetaData(Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);

          LOG.debug("Rendering PDF.");

          pdf = renderer.renderReport(jrReport, ebInterface, null);

        } catch (Exception ex) {
          error("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
          onError();
          return;
        }
      }

      String log = null;
      if (sbLog.toString().length()>0){
        log = sbLog.toString();
      }

      //Redirect
      setResponsePage(new ResultPageEbInterface(validationResult, null, ActionType.SCHEMA_VALIDATION, pdf, ebInterface, log));
    }


    /**
     * Process errors
     */
    @Override
    protected void onError() {
      //Show the feedback panel in case on an error
      feedbackPanel.setVisible(true);
    }
  }


  /**
   * The input form class
   *
   * @author pl
   */
  private class InputForm extends Form {


    /**
     * Panel for providing feedback in case of errorneous input
     */
    FeedbackPanel feedbackPanel;

    /**
     * Dropdown choice for the schmeatrno rules
     */
    DropDownChoice<Rule> rules;

    /**
     * Dropdown choice for the ZUGFeRD level
     */
    DropDownChoice<String> zugferdlevels;

    /**
     * Upload field for the ebInterface instance
     */
    FileUploadField fileUploadField;

    public InputForm(final String id) {
      super(id);

      //Set the form to multi part since we use file upload
      setMultiPart(true);

      //Add a feedback panel
      feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
      feedbackPanel.setVisible(false);
      add(feedbackPanel);

      //Add the file upload field
      fileUploadField = new FileUploadField("fileInput");
      fileUploadField.setRequired(true);
      add(fileUploadField);

      //Add the drop down choice for the different rules which are currently supported
      rules =
          new DropDownChoice<Rule>("ruleSelector", Model.of(new Rule()), Rules.getRules(),
                                   new IChoiceRenderer<Rule>() {
                                     @Override
                                     public Object getDisplayValue(Rule object) {
                                       return object.getName();
                                     }

                                     @Override
                                     public String getIdValue(Rule object, int index) {
                                       return object.getName();
                                     }
                                   });

      add(rules);

      //Add the drop down choice for the different ZUGFeRD levels which are currently supported
      zugferdlevels =
          new DropDownChoice<String>(
              "zugferdSelector", Model.of(new String()), ZUGFERD_LEVELS,
              new IChoiceRenderer<String>() {
                @Override
                public Object getDisplayValue(String object) {
                  return object;
                }

                @Override
                public String getIdValue(String object, int index) {
                  return object;
                }
              });

      add(zugferdlevels);

      //Add a second submit button
      add(new SubmitLink("submitButtonSchemaOnly") {
        @Override
        public void onSubmit() {
          submit(ActionType.SCHEMA_VALIDATION);
        }
      });

      //Add a submit button
      add(new SubmitLink("submitButtonSchematron") {
        @Override
        public void onSubmit() {
          submit(ActionType.SCHEMA_AND_SCHEMATRON_VALIDATION);
        }
      });

      //Add a button to visualize it as HTML
      add(new SubmitLink("submitButtonVisualizeHTML") {
        @Override
        public void onSubmit() {
          submit(ActionType.VISUALIZATION_HTML);
        }
      });

      //Add a button to visualize it as PDF
      add(new SubmitLink("submitButtonVisualizePDF") {
        @Override
        public void onSubmit() {
          submit(ActionType.VISUALIZATION_PDF);
        }
      });

      //Add a button to convert to ZUGFerD
      add(new SubmitLink("submitButtonConvertZUGFeRD") {
        @Override
        public void onSubmit() {
          submit(ActionType.CONVERSION_ZUGFERD);
        }
      });
    }

    /**
     * Process the input
     */
    protected void submit(final ActionType selectedAction) {

      //Hide the feedback panel first (will be shown in case of an error)
      feedbackPanel.setVisible(false);

      //Schematron validation?
      //Schematron set must be selected
      if (selectedAction == ActionType.SCHEMA_AND_SCHEMATRON_VALIDATION) {
        if (rules.getModelObject() == null) {
          error(new ResourceModel("ruleSelector.Required").getObject());
          onError();
          return;
        }
      }

      byte[] pdf = null;
      byte[] zugferd = null;
      StringBuilder sbLog = new StringBuilder();

      //Get the file input
      final FileUpload upload = fileUploadField.getFileUpload();
      byte[] uploadedData = null;

      try {
        final InputStream inputStream = upload.getInputStream();
        uploadedData = IOUtils.toByteArray(inputStream);
      } catch (final IOException e) {
        LOG.error("Die hochgeladene Datei kann nicht verarbeitet werden.", e);
      }

      //Validate the XML instance - performed in any case
      final EbInterfaceValidator validator = Application.get().getMetaData(
          Constants.METADATAKEY_EBINTERFACE_XMLSCHEMAVALIDATOR);
      final ValidationResult
          validationResult =
          validator.validateXMLInstanceAgainstSchema(uploadedData);

      if (validationResult.getDeterminedEbInterfaceVersion() == null) {
        error(
            "Das XML kann nicht verarbeitet werden, das es keiner ebInterface Version entspricht.");
        onError();
        return;
      }

      //Schematron validation too?
      if (selectedAction == ActionType.SCHEMA_AND_SCHEMATRON_VALIDATION) {
        //Schematron validation may only be started in case of ebInterface 4p0
        if (validationResult.getDeterminedEbInterfaceVersion() == EbInterfaceVersion.E4P0 ||
            validationResult.getDeterminedEbInterfaceVersion() == EbInterfaceVersion.E4P1 ||
            validationResult.getDeterminedEbInterfaceVersion() == EbInterfaceVersion.E4P2) {

          Rule rule = rules.getModelObject();
          if (rule != null && !(rule.getEbInterfaceVersion()
                                    .equals(validationResult.getDeterminedEbInterfaceVersion()))) {
            error(new ResourceModel("schematron.version.mismatch").getObject());
            onError();
            return;
          }

          //Invoke the validation
          Result
              r =
              validator.validateXMLInstanceAgainstSchematron(uploadedData, rule.getFileReference());
          validationResult.setResult(r);
        }
        //Wrong ebInterface version
        else {
          error(
              "Schematronregeln können nur auf ebInterface 4.0/4.1/4.2 Instanzen angewendet werden. Erkannte ebInterface Version ist jedoch: "
              + validationResult.getDeterminedEbInterfaceVersion().getCaption());
          onError();
          return;
        }
      }
      //Visualization HTML?
      else if (selectedAction == ActionType.VISUALIZATION_HTML) {
        //Visualization is only possible for valid instances
        if (!StringUtils.isEmpty(validationResult.getSchemaValidationErrorMessage())) {
          error(
              "Die gewählte ebInterface Instanz ist nicht valide. Es können nur valide Schemainstanzen in der Druckansicht angezeigt werden.");
          onError();
          return;
        }

        //Get the transformed string
        final String
            s =
            validator
                .transformInput(uploadedData, validationResult.getDeterminedEbInterfaceVersion());
        //Redirect to the printview page
        setResponsePage(new PrintViewPage(s));
        return;


      }
      //ebInterface PDF-Generation
      else if (selectedAction == ActionType.VISUALIZATION_PDF) {
        BaseRenderer renderer = new BaseRenderer();

        try {
          LOG.debug("Load ebInterface JasperReport template from application context.");
          JasperReport
              jrReport =
              Application.get().getMetaData(Constants.METADATAKEY_EBINTERFACE_JRTEMPLATE);

          LOG.debug("Rendering PDF.");

          pdf = renderer.renderReport(jrReport, uploadedData, null);

        } catch (Exception ex) {
          error("Bei der ebInterface-PDF-Erstellung ist ein Fehler aufgetreten.");
          onError();
          return;
        }
      }
      //Conversion ZUGFerD?
      else if (selectedAction == ActionType.CONVERSION_ZUGFERD) {
        if (zugferdlevels.getModelObject() == null){
          error("Bitte wählen Sie ein ZUGFeRD Profil zur Konvertierung aus.");
          onError();
          return;
        }

        sbLog.append("<b>Ausgewähltes ZUGFeRD Profil: ").append(zugferdlevels.getModelObject()).append("</b><br/><br/>");

        MappingFactory mf = new MappingFactory();

        MappingFactory.ZugferdMappingType zugferdLevel;

        if (zugferdlevels.getModelObject().endsWith("Basic")){
          zugferdLevel = MappingFactory.ZugferdMappingType.ZUGFeRD_BASIC_1p0;
        } else if (zugferdlevels.getModelObject().endsWith("Comfort")){
          zugferdLevel = MappingFactory.ZugferdMappingType.ZUGFeRD_COMFORT_1p0;
        } else /*if (selectedZugferdLevel.startsWith("Extended"))*/{
          zugferdLevel = MappingFactory.ZugferdMappingType.ZUGFeRD_EXTENDED_1p0;
        }

        MappingFactory.EbInterfaceMappingType ebType;

        if (validationResult.getDeterminedEbInterfaceVersion() == EbInterfaceVersion.E4P0){
          error("ZUGFeRD Konvertierung für ebInterface 4.0 nicht unterstützt.");
          onError();
          return;

          /*zugFeRDMapping = mf.getMapper(MappingFactory.ZugferdMappingType.ZUGFeRD_EXTENDED_1p0,
                                        MappingFactory.EbInterfaceMappingType.EBINTERFACE_4p0);*/
        }else if (validationResult.getDeterminedEbInterfaceVersion() == EbInterfaceVersion.E4P1){
          ebType = MappingFactory.EbInterfaceMappingType.EBINTERFACE_4p1;
        }else /*(validationResult.getDeterminedEbInterfaceVersion() == EbInterfaceVersion.E4P2)*/ {
          ebType = MappingFactory.EbInterfaceMappingType.EBINTERFACE_4p2;
        }

        Mapping zugFeRDMapping = mf.getMapper(zugferdLevel,
                                              ebType);

        String sZugferd;
        SAXSource saxSource;

        //Map to ZUGFeRD Basic
        try {
          LOG.debug("Mapp ebInterface to ZUGFeRD.");
          sZugferd = new String(zugFeRDMapping.mapFromebInterface(new String(uploadedData)));

          zugferd = sZugferd.getBytes("UTF-8");

          saxSource = new SAXSource(new InputSource(
              new ByteArrayInputStream(zugferd)));

          Validator zugSchemaValidator = Application.get().getMetaData(Constants.METADATAKEY_ZUGFERD_XMLSCHEMA).newValidator();
          MappingErrorHandler eh = new MappingErrorHandler();
          zugSchemaValidator.setErrorHandler(eh);
          zugSchemaValidator.validate(saxSource);

          if (eh.catchedError()) {
            zugferd = null;
            sbLog.append("<b>ZUGFeRD XSD Validierung fehlgeschlagen:</b><br/>").append(
                eh.toString().replace("\n", "<br/>"));
          }
        } catch (Exception e) {
          error("Bei der ZUGFeRD-Konvertierung ist ein Fehler aufgetreten.");
          onError();
          return;
        }

        if (zugferd != null) {
          sbLog.append(zugFeRDMapping.getMappingLogHTML());

          Source source = new StreamSource(new StringReader(sZugferd));
          javax.xml.transform.Result result = new StreamResult(new StringWriter());

          try {
            Transformer transformer = Application.get().getMetaData(
                Constants.METADATAKEY_ZUGFERD_SCHEMATRONTEMPLATE).newTransformer();

            transformer.reset();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            MappingErrorListener el = new MappingErrorListener();

            transformer.setErrorListener(el);

            //saxon is used, MessageErmitter has to be set, otherwise, ErrorListener will mention Errors
            ((Controller) transformer).setMessageEmitter(new MessageWarner());

            transformer.transform(source, result);

            if (el.catchedError()) {
              zugferd = null;
              sbLog.append("<br/><p>").append("<b>Schematron Validierung fehlgeschlagen:</b><br/>")
                  .append(el.toString().replace("\n", "<br/>"));
            } else {
              sbLog.append("<br/><p>").append(
                  "<b>Schematron Validierung erfolgreich durchgeführt!</b><br/>");

              ZugferdRenderer renderer = new ZugferdRenderer();

              try {
                LOG.debug("Load ZUGFeRD JasperReport template from application context.");
                JasperReport
                    jrReport =
                    Application.get().getMetaData(Constants.METADATAKEY_ZUGFERD_JRTEMPLATE);

                LOG.debug("Rendering PDF.");

                pdf = renderer.renderReport(jrReport, zugferd, null);

              } catch (Exception ex) {
                error("Bei der ZUGFeRD-PDF-Erstellung ist ein Fehler aufgetreten.");
                onError();
                return;
              }
            }
          } catch (Exception e) {
            error("Bei der ZUGFeRD-Überprüfung ist ein Fehler aufgetreten.");
            onError();
            return;
          }
        }
      }

      String selectedSchematronRule = "";
      if (rules.getModelObject() != null) {
        selectedSchematronRule = rules.getModelObject().getName();
      }

      if (selectedAction != ActionType.CONVERSION_ZUGFERD) {
        //Redirect to the ebInterface result page
        setResponsePage(
            new ResultPageEbInterface(validationResult, selectedSchematronRule, selectedAction,
                                      pdf, null, null));
      } else {
        //Redirect to the ZUGFeRD result page
        setResponsePage(
            new ResultPageZugferd(validationResult, selectedSchematronRule, selectedAction,
                                      zugferd, sbLog.toString(), pdf));
      }

    }

    /**
     * Process errors
     */
    @Override
    protected void onError() {
      //Show the feedback panel in case on an error
      feedbackPanel.setVisible(true);
    }
  }
}
