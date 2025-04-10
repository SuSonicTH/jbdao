# JBDAO
Just a Bunch of Data Access Object

a Java library to generate DAO classes at compile time

## todo

* implement from/to csv (inkl. read csv file as list and write list as csv file)
* Enums as members (& enum generation)
* implement as an abstract class to be overwritten with additional implementations, should also create the overwritten
  class if it does not exist
* builderFrom to return a builder with all the values set from this object
* automatic ID generation from sequence or trigger
* maskedPattern and masked Replace to replace sensitive data in toString/logs (i.e pattern "...(.*)" replace "xxx\1")
* spec flag to log db actions (with level)

## TBD
* implement servlet for put/get/patch ?
