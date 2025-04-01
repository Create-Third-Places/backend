This repo contains backend services for dmvboardgames.com.


The API can be accessed at https://api.dmvboardgames.com/


Data will be stored in PostgreSQL using Supabase.

### Running locally in Linux

- Compile the API using `mvn clean package`
- Make sure the database password is set in your `.bashrc` file
- Run using `java -jar target/app.jar`

The API will be available at http://localhost:7070/


### Code formatter

- Run `mvn prettier:write`.

### Production deployments

Production deployments will run with the latest changes from the release branch. Once a build image has been created, a deployment will then be manually triggered using the Digital Ocean app platform.

