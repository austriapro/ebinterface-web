package at.ebinterface.web2.pdf;

import java.io.OutputStream;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.WillClose;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringHelper;
import com.helger.ebinterface.EEbInterfaceVersion;
import com.helger.pdflayout.IPDDocumentCustomizer;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.pdfbox.PDPageContentStreamExt;

import at.ebinterface.web2.app.CApp;
import at.ebinterface.web2.pdf.cover.PLCover;
import at.ebinterface.web2.pdf.cover.ReportDataCover;
import at.ebinterface.web2.pdf.details.PLDetails;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * This is the main class for creating PDF documents from AustroFIX invoices.
 *
 * @author Philip Helger
 */
public final class InvoicePDFGenerator
{
  private static final Logger LOGGER = LoggerFactory.getLogger (InvoicePDFGenerator.class);

  private final Locale m_aLocale;
  private String m_sWatermark;

  /**
   * Constructor
   *
   * @param aLocale
   *        The locale to be used for formatting values.
   */
  public InvoicePDFGenerator (@Nonnull final Locale aLocale)
  {
    m_aLocale = ValueEnforcer.notNull (aLocale, "Locale");
  }

  /**
   * Create a PDF from the invoice specified by the given XML invoice filename.
   *
   * @param eSourceFormat
   *        Source format of the invoice (e.g. "ebInterface 6.0")
   * @param aInvoice
   *        The invoice to convert. May not be <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is
   *        closed automatically!
   * @param bDetailsInLandscape
   *        Show details in landscape (<code>true</code>) or in portrait
   *        (<code>false</code>).
   * @throws PDFGenerationException
   *         In case of an error.
   */
  public void runPDFCreation (@Nullable final EEbInterfaceVersion eSourceFormat,
                              @Nonnull final InvoiceType aInvoice,
                              @Nonnull @WillClose final OutputStream aOS,
                              final boolean bDetailsInLandscape) throws PDFGenerationException
  {
    ValueEnforcer.notNull (aInvoice, "AFInvoice");
    ValueEnforcer.notNull (aOS, "OutputStream");

    LOGGER.info ("Creating PDF for invoice '" + aInvoice.getIDValue () + "'");

    try
    {
      // create a new invoice pdf
      final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (true);

      // Set document properties
      {
        aPageLayout.setDocumentAuthor (CApp.getAppName ());
        aPageLayout.setDocumentCreator (CApp.getAppName ());
        final String sBiller = PDFHelper.createPersonAndAddressString (aInvoice.getAccountingSupplierParty ()
                                                                               .getParty (),
                                                                       m_aLocale,
                                                                       true);
        final String sInvoiceRecipient = PDFHelper.createPersonAndAddressString (aInvoice.getAccountingCustomerParty ()
                                                                                         .getParty (),
                                                                                 m_aLocale,
                                                                                 true);
        aPageLayout.setDocumentKeywords (EPDFText.DOCUMENT_KEYWORDS.getDisplayTextWithArgs (m_aLocale,
                                                                                            aInvoice.getIDValue (),
                                                                                            sBiller,
                                                                                            sInvoiceRecipient));
        aPageLayout.setDocumentSubject (EPDFText.DOCUMENT_SUBJECT.getDisplayTextWithArgs (m_aLocale,
                                                                                          aInvoice.getIDValue ()));
        aPageLayout.setDocumentTitle (EPDFText.DOCUMENT_TITLE.getDisplayText (m_aLocale));
      }

      // add the cover document
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Start creating cover PDF page");

      final ReportDataCover aCoverData = ReportDataCover.create (m_aLocale, aInvoice);

      final PLCover aCover = new PLCover ();
      final PLPageSet aCoverPage = aCover.createCoverPage (aCoverData);
      if (aCoverPage == null)
        throw new PDFGenerationException (EPDFGenerationError.PDF_ERROR, "Failed to create cover page");

      // add cover page to PDF
      aPageLayout.addPageSet (aCoverPage);

      // we have details -> add the details page(s)
      if (aInvoice.hasInvoiceLineEntries ())
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Start creating details PDF page(s)");

        // create temporary report - attachment is added later on
        final PLDetails aDetails = new PLDetails (bDetailsInLandscape);
        final PLPageSet aDetailsPages = aDetails.createDetailsPages (m_aLocale, aInvoice, aCoverData);
        if (aDetailsPages == null)
          throw new PDFGenerationException (EPDFGenerationError.PDF_ERROR, "Failed to create details page(s)");

        // add details page(s) to PDF
        aPageLayout.addPageSet (aDetailsPages);
      }

      // Add watermark for preview
      final IPDDocumentCustomizer aWatermarkCustomizer = StringHelper.isEmpty (m_sWatermark) ? null : aDoc -> {
        final PDFont aFont = new PDType1Font (Standard14Fonts.FontName.HELVETICA_BOLD);
        final float fFontSize = 100.0f;
        final String sMessage = m_sWatermark;

        for (final PDPage aPage : aDoc.getPages ())
        {
          final PDRectangle aPageSize = aPage.getMediaBox ();
          // calculate to center of the page
          final float fCenterX = aPageSize.getWidth () * 0.2f;
          final float fCenterY = aPageSize.getHeight () * 0.1f;
          // prepend the content to the existing stream
          try (
              final PDPageContentStreamExt aCustomizeCS = new PDPageContentStreamExt (aDoc,
                                                                                      aPage,
                                                                                      PDPageContentStream.AppendMode.PREPEND,
                                                                                      true,
                                                                                      true))
          {
            aCustomizeCS.saveGraphicsState ();
            aCustomizeCS.beginText ();
            // set font and font size
            aCustomizeCS.setFont (aFont, fFontSize);
            // set text color to grey
            aCustomizeCS.setNonStrokingColor (220, 220, 220);
            // rotate the text according to the page rotation
            aCustomizeCS.setTextMatrix (Matrix.getRotateInstance (1.3 * Math.PI / 4, fCenterX, fCenterY));
            aCustomizeCS.showText (sMessage);
            aCustomizeCS.endText ();
            aCustomizeCS.restoreGraphicsState ();
          }
        }
      };

      // write PDF to output stream
      aPageLayout.setDocumentCustomizer (aWatermarkCustomizer);
      aPageLayout.renderTo (aOS);
    }
    catch (final PDFCreationException ex)
    {
      throw new PDFGenerationException (EPDFGenerationError.PDF_ERROR, ex);
    }
    catch (final Exception ex)
    {
      throw new PDFGenerationException (EPDFGenerationError.IO_ERROR, ex);
    }
    finally
    {
      // Close always
      StreamHelper.close (aOS);
    }
  }
}
