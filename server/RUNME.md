## Running the Server

NOTE: this project contains various configuration files that you will have to 
change please __DO NOT__ version these files with Git. 

Make sure you have a MariaDB database setup at port 3306. The database should 
be named `era`. Remember the root username and password for later usage. 

We use gradle to automate the build for the Server. Gradle needs to be 
configured to work properly in your specific environment. You can view 
configurations in the gradle.properties file. You will most likely need 
to change `db.user` and `db.password`. When running the Server for
the first time, you will to make sure that the Server's database schema is 
current. You can do this by running `gradle flywayMigrate`. Once you schema is 
up to date you can build the Server JAR using `gradle jar`. 

Once you have the JAR built you can run it using various command line arguments:

 * `--app-port`: the port the application should bind to
 * `--db-host`: the hostname of the node containing the database. 
 * `--db-port`: the port that the database is running on
 * `--db-user`: the name of the user the application should use to work with 
 the database
 * `--db-name`: the name of the database containing the my-assignments schema
 (usually era to match with the gradle build)
 * `--db-password`: the password for that user. 
 * `--cas-enabled`: `true` if you want to use the CAS authentication, `false` otherwise