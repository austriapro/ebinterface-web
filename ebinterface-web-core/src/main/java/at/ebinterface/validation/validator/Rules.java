package at.ebinterface.validation.validator;

import java.util.ArrayList;
import java.util.List;

import com.helger.ebinterface.EEbInterfaceVersion;

/**
 * Servers as a wrapper for the different Schematron rules which may be checked with this service
 *
 * @author pl
 */
public final class Rules {


  /**
   * Stores the rules which are currently supported by this validation service. Use Rule as the key
   * in order to allow retrieval based on type and ebInterface version
   */
  private static final List<Rule> rules = new ArrayList<> ();


  static {
    //  https://github.com/austriapro/ebinterface-web/issues/11
    if (false) {
      final Rule
          rule1 =
          new Rule("Sozialversicherung (ebInterface 4p0)", EEbInterfaceVersion.V40,
                   "/schematron/sv/sv-rules-4p0.sch");
      rules.add(rule1);
    }

    final Rule
        rule2 =
        new Rule("Sozialversicherung (ebInterface 4p1)", EEbInterfaceVersion.V41,
                 "/schematron/sv/sv-rules-4p1.sch");
    rules.add(rule2);

    final Rule
        rule3 =
        new Rule("Sozialversicherung (ebInterface 4p2)", EEbInterfaceVersion.V42,
                 "/schematron/sv/sv-rules-4p2.sch");
    rules.add(rule3);

    final Rule
        rule4 =
        new Rule("Sozialversicherung (ebInterface 4p3)", EEbInterfaceVersion.V43,
                 "/schematron/sv/sv-rules-4p3.sch");
    rules.add(rule4);
  }

  private Rules (){}

  /**
   * Get a certain rule
   * @param name Name of the rule
   * @param version ebInterface version to use
   * @return <code>null</code> if no match is found
   */
  public static Rule getRule(final String name, final EEbInterfaceVersion version) {
    for (final Rule r : rules) {
      if (r.getName().equals(name) && r.getEbInterfaceVersion().equals(version)) {
        return r;
      }
    }
    return null;
  }


  public static List<Rule> getRules() {
    return rules;
  }
}
