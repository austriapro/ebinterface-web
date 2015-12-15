package at.ebinterface.validation.rtr.generated;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for SignaturePropertyType complex type. <p/> <p>The following schema fragment
 * specifies the expected content contained within this class. <p/>
 * <pre>
 * &lt;complexType name="SignaturePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;any processContents='lax' namespace='##other'/>
 *       &lt;/choice>
 *       &lt;attribute name="Target" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI"
 * />
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignaturePropertyType", propOrder = {
    "content"
})
public class SignaturePropertyType {

  @XmlMixed
  @XmlAnyElement(lax = true)
  protected List<Object> content;
  @XmlAttribute(name = "Target", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String target;
  @XmlAttribute(name = "Id")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlID
  @XmlSchemaType(name = "ID")
  protected String id;

  /**
   * Gets the value of the content property. <p/> <p/> This accessor method returns a reference to
   * the live list, not a snapshot. Therefore any modification you make to the returned list will be
   * present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
   * content property. <p/> <p/> For example, to add a new item, do as follows:
   * <pre>
   *    getContent().add(newItem);
   * </pre>
   * <p/> <p/> <p/> Objects of the following type(s) are allowed in the list {@link String } {@link
   * Object } {@link Element }
   */
  public List<Object> getContent() {
    if (content == null) {
      content = new ArrayList<Object>();
    }
    return this.content;
  }

  /**
   * Gets the value of the target property.
   *
   * @return possible object is {@link String }
   */
  public String getTarget() {
    return target;
  }

  /**
   * Sets the value of the target property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTarget(String value) {
    this.target = value;
  }

  /**
   * Gets the value of the id property.
   *
   * @return possible object is {@link String }
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value allowed object is {@link String }
   */
  public void setId(String value) {
    this.id = value;
  }

}
