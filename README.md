# JBDAO
Just a Bunch of Data Access Object

a Java library to generate DAO classes at compile time

## todo

* automatically convert primitive to object types for members if non-nullable (needs refactor in resultSetGenerator)
* update checks nr of updated rows and throws if zero
* implement from/to csv (inkl. read csv file as list and write list as csv file)
* implement persist that inserts or updates
* Enums as members (& enum generation)

## TBD
* implement servlet for put/get/patch ?
* implement validations? (min/max,pattern) 
* implement a "writeClassToOutput" method (remembering already written classes) for GsonUtil (only if there are more
  than GsonUtil)
 