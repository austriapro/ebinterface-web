![ebInterface Logo](https://github.com/pliegl/ebinterface/blob/master/site/images/logo.jpg?raw=true "ebInterface e-Invoice standard")

#ebInterface Web

ebInterface Web allows to validate ebInterface instances according to ebInterface XML Schema version 3.0, 3.02, 4.0, and 4.01. Furthermore, XML instances may be converted to human-readable HTML representations as well as PDF files.

The validation includes XML Schema conformance, conformance to predefined Schematron rules, and the validation of digital signatures. For the validation of digital signatures the Web Service of [Rundfunk & Telekom Regulierungs-GmbH](http://www.rtr.at) [https://pruefung.signatur.rtr.at/](https://pruefung.signatur.rtr.at/) is used.

The visualization service allows to generate HTML output based on XSLT stylesheets. In addition PDF files may be generated, using the components from [ebinterface-rendering](https://github.com/austriapro/ebinterface-rendering).

Furthermore, ebInterface Web allows to transform a given ebInterface XML instance to other document formats, using the document mappings provided in [ebinterface-mappings](https://github.com/austriapro/ebinterface-mappings)

The validation contains two main artifacts

 * *ebinterface-core.* This module contains the validation logic for ebInterface instances, the logic for applying stylesheets to ebInterface instances, as well as the Web Service client for the RTR Web Service
 * *ebinterface-web.* This module contains a Web frontend for the *ebinterface-core* module. Currently, this module is being extended to support PDF generation and ebInterface mappings as well.

##Code style

For this project the Google code style is used. Please [download the code style file](https://code.google.com/p/google-styleguide/source/browse/trunk/intellij-java-google-style.xml) and set it in your IDE, before you commit to the repository. 

##Prerequisites

Please note, that the RTR Web Service is not open to the public, but requires credentials for its HTTP basic authentication in order to be accessed. Username and password must be provided in a properties file named ```rtr.properties```, which must contain the following values:

``` 
rtr.username=<username>
rtr.password=<password>
```

Place the file under `ebinterface-web/src/main/resources/rtr.properties`

Username and password may be requested from RTR directly, and are not provided in this GitHub repository.

:green_heart: Pull requests are greatly appreciated and welcomed.
