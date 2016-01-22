package at.ebinterface.validation.web.pages;

import at.ebinterface.validation.web.css.CssReference;
import at.ebinterface.validation.web.pages.images.ImagesLogoAccessor;

import org.apache.wicket.markup.head.CssHeaderItem;
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
    add(new Image("ebInterfaceLogo",
                  new PackageResourceReference(ImagesLogoAccessor.class, "ebInterfaceLogo.jpg")));


  }


  @Override
  public void renderHead(HtmlHeaderContainer container) {


    PackageResourceReference cssFile =
        new PackageResourceReference(CssReference.class, "style.css");
    CssHeaderItem cssItem = CssHeaderItem.forReference(cssFile);
    container.getHeaderResponse().render(cssItem);

    super.renderHead(container);


  }
}
