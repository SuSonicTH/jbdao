# JBDAO
Just a Bunch of Data Access Object

a Java library to generate DAO classes at compile time

## todo

* implement from/to csv (inkl. read csv file as list and write list as csv file)
* Enums as members (& enum generation)
* Make ResultSetSpliterator Abstract and standalone, just a createNew(ResultSet) method is abstract -> each class has a
  ResultSetSpliterator implementation with constructor call
* implement validations? (min/max,pattern)
* implement a "writeClassToOutput" method (remembering already written classes) for GsonUtil and
  AbstractResultSetSpliterator,...

## TBD
* implement servlet for put/get/patch ?
 