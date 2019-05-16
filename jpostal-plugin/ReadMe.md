# JPostalPDI Plugin

A working plugin for Spoon 8.2 and Pentaho/Vantara Data Integration
that splits addresses into their respective parts through JPostal.

Requires libpostal to be installed and on your path and a jar for JPostal
to be on the path to pdi libraries as specified in your Pentaho install.

WARNING: I used a template to generate the code so there is an issue with
margins.

### Building

To build the project, go to the maven pom.xml file and enter the paths to
your jpostal and Stanford NER-core jars in the appropriate properties
under the properties section. These are jpostal.jar and nercore.jar.


### Installing

Run mvn package in the pom directory. Copy the jpostal-plugin folder to
the plugins folder in your pdi directory.

Copy jpostal.jar and the Stanford NER core jar to lib, libext, or wherever
they can be picked up by spoon. In development libext was added as shown
in the pom properties.

### License

Apache v2.0. The same as Vantara CE.
