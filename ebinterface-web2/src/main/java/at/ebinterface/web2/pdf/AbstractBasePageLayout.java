package at.ebinterface.web2.pdf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.StringHelper;
import com.helger.font.open_sans.EFontResourceOpenSans;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Abstract PageLayout (PL)
 *
 * @author Philip Helger
 */
public abstract class AbstractBasePageLayout
{
  /** The page margin */
  public static final float PAGE_MARGIN_VAL = 30.0f;

  /** The x-padding within a single cell in PDF units */
  protected static final float CELL_PADDING_VAL = 3f;
  protected static final PaddingSpec CELL_PADDING = new PaddingSpec (CELL_PADDING_VAL);

  /** Width of 0 means hair line */
  protected static final BorderStyleSpec BORDER_BLACK = new BorderStyleSpec (PLColor.BLACK, 1f);

  /** Use OpenSans as font */
  protected static final PreloadFont OPEN_SANS_REGULAR = PreloadFont.createEmbedding (EFontResourceOpenSans.OPEN_SANS_NORMAL.getFontResource ());
  protected static final PreloadFont OPEN_SANS_BOLD = PreloadFont.createEmbedding (EFontResourceOpenSans.OPEN_SANS_BOLD.getFontResource ());

  protected static final FontSpec F8 = new FontSpec (OPEN_SANS_REGULAR, 8);
  protected static final FontSpec F10 = new FontSpec (OPEN_SANS_REGULAR, 10);
  protected static final FontSpec F10BOLD = F10.getCloneWithDifferentFont (OPEN_SANS_BOLD);
  protected static final FontSpec F16 = new FontSpec (OPEN_SANS_REGULAR, 16);
  protected static final FontSpec F16BOLD = F16.getCloneWithDifferentFont (OPEN_SANS_BOLD);

  /** A slightly lighter gray than light gray */
  protected static final PLColor LIGHTER_GRAY = new PLColor (220, 220, 220);

  @Nullable
  private static String _getCleanPDFText (@Nullable final String sText)
  {
    String sRealText = sText;
    if (sRealText != null)
    {
      // 0009 = tabulator -> not printable in PDF
      sRealText = StringHelper.replaceAll (sRealText, '\t', ' ');
    }
    return sRealText;
  }

  @Nonnull
  protected static final PLText createText (@Nullable final String sText, @Nonnull final FontSpec aFontSpec)
  {
    return new PLText (_getCleanPDFText (sText), aFontSpec).setVertSplittable (true);
  }

  @Nonnull
  protected static final PLText createTextStatic (@Nullable final String sText, @Nonnull final FontSpec aFontSpec)
  {
    return new PLText (_getCleanPDFText (sText), aFontSpec).setVertSplittable (false);
  }
}
