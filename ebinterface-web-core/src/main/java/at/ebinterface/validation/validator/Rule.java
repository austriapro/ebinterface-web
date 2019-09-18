package at.ebinterface.validation.validator;

import java.io.Serializable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.ebinterface.EEbInterfaceVersion;

/**
 * Represents a Schematron rule
 */
public class Rule implements Serializable
{
  private String name;
  private EEbInterfaceVersion ebInterfaceVersion;
  private String fileReference;

  public Rule ()
  {}

  public Rule (final String name, final EEbInterfaceVersion ebInterfaceVersion, final String fileReference)
  {
    this.name = name;
    this.ebInterfaceVersion = ebInterfaceVersion;
    this.fileReference = fileReference;
  }

  public String getName ()
  {
    return name;
  }

  public void setName (final String name)
  {
    this.name = name;
  }

  public EEbInterfaceVersion getEbInterfaceVersion ()
  {
    return ebInterfaceVersion;
  }

  public void setEbInterfaceVersion (final EEbInterfaceVersion ebInterfaceVersion)
  {
    this.ebInterfaceVersion = ebInterfaceVersion;
  }

  public String getFileReference ()
  {
    return fileReference;
  }

  public void setFileReference (final String fileReference)
  {
    this.fileReference = fileReference;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;

    final Rule rhs = (Rule) o;
    return EqualsHelper.equals (name, rhs.name) &&
           EqualsHelper.equals (ebInterfaceVersion, rhs.ebInterfaceVersion) &&
           EqualsHelper.equals (fileReference, rhs.fileReference);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (name).append (ebInterfaceVersion).append (fileReference).getHashCode ();
  }
}
