# Backend Template for SEPM Group Phase

## How to run it

### Start the backed
Depending on your OS:
`mvn spring-boot:run,win`
or
`mvn spring-boot:run,linux`

### Start the backed with test data
Important: If the database is not clean, the test data won't be inserted
Depending on your OS:
`mvn spring-boot:run -Dspring-boot.run.profiles=generateData,win`
or
`mvn spring-boot:run -Dspring-boot.run.profiles=generateData,linux`