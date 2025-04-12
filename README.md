# JBDAO
Just a Bunch of Data Access Object

a Java library to generate DAO classes at compile time

## todo

* implement from/to csv (inkl. read csv file as list and write list as csv file)
* Enums as members (& enum generation)
* automatic ID generation from sequence or trigger
* implement as an abstract class to be overwritten with additional implementations, should also create the overwritten
  class if it does not exist

## TBD
* implement servlet for put/get/patch ?
* spec flag to log db actions (with level) adds dependecy to slf4j