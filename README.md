# JBDAO
Just a Bunch of Data Access Object

a Java library to generate DAO classes at compile time

## todo

* implement from/to csv (inkl. read csv file as list and write list as csv file)
* Enums as members (& enum generation)
* move all validations for a variable together ( i.e name not null, then name not empty, name...; instead all not null,
  all not empty)
* if a string member has a min the empty check should not be done
* implement as an abstract class to be overwritten with additional implementations, should also create the overwritten
  class if it does not exist
* builderFrom to return a builder with all the values set from this object
* implement setters (including validation)
* automatic ID generation from sequence or trigger

* flags on members if save to log/tostring (no/yes default yes)
* maskedPattern and masked Replace to replace sensitive data in toString/logs (i.e pattern "...(.*)" replace "xxx\1")
* spec flag to log db actions (with level)

## TBD
* implement servlet for put/get/patch ?
