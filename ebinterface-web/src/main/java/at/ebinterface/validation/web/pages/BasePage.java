package at.ebinterface.validation.web.pages;

import at.ebinterface.validation.web.css.CssReference;
import at.ebinterface.validation.web.js.JsReference;
import at.ebinterface.validation.web.pages.images.ImagesLogoAccessor;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * Base page for all web pages
 *
 * @author pl
 */
public abstract class BasePage extends WebPage {


  /**
   *
   */
  public BasePage() {

    //Add the ebInterface Logo
    add(new Image("ebInterfacelogo",
                  new PackageResourceReference(ImagesLogoAccessor.class, "ebInterfacelogo.png")));


  }


  @Override
  public void renderHead(HtmlHeaderContainer container) {

    PackageResourceReference cssFile_one =
            new PackageResourceReference(CssReference.class, "style.css");
    CssHeaderItem cssItem_one = CssHeaderItem.forReference(cssFile_one);

    PackageResourceReference cssFile_three =
            new PackageResourceReference(CssReference.class, "wkostyle.css");
    CssHeaderItem cssItem_three = CssHeaderItem.forReference(cssFile_three);


    container.getHeaderResponse().render(cssItem_one);
    //container.getHeaderResponse().render(cssItem_two);
    container.getHeaderResponse().render(cssItem_three);

    PackageResourceReference jsReference = new PackageResourceReference(JsReference.class, "fileLabelChecker.js");
    JavaScriptReferenceHeaderItem headerItem = JavaScriptHeaderItem.forReference(jsReference);
    container.getHeaderResponse().render(headerItem);
    super.renderHead(container);


  }
}
