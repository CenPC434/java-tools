# java-tools
This GitHub project contains Java libraries to support CEN TC/434 / EN 16931.
This is the standard for the European data model for electronic invoices.

All projects contained in here are Apache 2 licensed.

## News and noteworthy:

* v2.0.0 - 2018-01-05
  * Adopted to ph-commons 9.0.0
* v0.1.0 - 2017-02-22
  * Initial release

## en16931-edifact-to-xml
The Altova Mapforce generated code to convert EDIFACT text files to ISO 20625 EDIFACT XML files.
It is a standalone tool without dependencies but nevertheless can also be used inside other applications.


**Maven usage:**
```xml
<dependency>
  <groupId>com.helger.en16931</groupId>
  <artifactId>en16931-edifact-to-xml</artifactId>
  <version>2.0.0</version>
</dependency>
```

## en16931-edifact-xml
JAXB generated domain objects for the special EDIFACT ISO 20625 XML dialect used in EN 16931.

**Maven usage:**
```xml
<dependency>
  <groupId>com.helger.en16931</groupId>
  <artifactId>en16931-edifact-xml</artifactId>
  <version>2.0.0</version>
</dependency>
```

## en16931-xml-validator
A generic XML validator that supports validation of XML files against arbitrary XML Schemas as well as Schematron files. It is meant to be used as a standalone application.

**Maven usage:**
```xml
<dependency>
  <groupId>com.helger.en16931</groupId>
  <artifactId>en16931-xml-validator</artifactId>
  <version>2.0.0</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
