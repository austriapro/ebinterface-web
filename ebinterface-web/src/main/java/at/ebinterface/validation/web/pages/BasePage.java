package at.ebinterface.validation.web.pages;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.request.resource.PackageResourceReference;

import at.ebinterface.validation.web.css.CssReference;
import at.ebinterface.validation.web.js.JsReference;
import at.ebinterface.validation.web.pages.images.ImagesLogoAccessor;

/**
 * Base page for all web pages
 *
 * @author pl
 */
public abstract class BasePage extends WebPage {
  /**
   *Constructor
   */
  public BasePage() {
    add(new Image("ebInterface-logo",
                  new PackageResourceReference(ImagesLogoAccessor.class, "ebinterface-logo.png")));
    add(new Image("wko-logo",
                  new PackageResourceReference(ImagesLogoAccessor.class, "wko-logo.png")));

  }


  @Override
  public void internalRenderHead(final HtmlHeaderContainer container) {

    final PackageResourceReference cssFile_wko =
        new PackageResourceReference(CssReference.class, "wkostyle.css");
    final CssHeaderItem cssItem_wko = CssHeaderItem.forReference(cssFile_wko);

    final PackageResourceReference cssFile_page =
        new PackageResourceReference(CssReference.class, "style.css");
    final CssHeaderItem cssItem_page = CssHeaderItem.forReference(cssFile_page);

    container.getHeaderResponse().render(cssItem_wko);
    container.getHeaderResponse().render(cssItem_page);

    final PackageResourceReference
        jsReference =
        new PackageResourceReference(JsReference.class, "fileLabelChecker.js");
    final JavaScriptReferenceHeaderItem headerItem = JavaScriptHeaderItem.forReference(jsReference);
    container.getHeaderResponse().render(headerItem);
    super.internalRenderHead(container);
  }
}
