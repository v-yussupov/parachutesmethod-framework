
These projects are required for testing the implementation:

    https://github.com/v-yussupov/parachutesmethod-annotations - Java parachute annotations library
    https://github.com/v-yussupov/parachutesmethod-exampleapp - Example application
    https://github.com/v-yussupov/parachutesmethod-evaluation - Evaluation results

    Steps:

    Maven build & install the annotations projects:
        Clone the project
        Navigate to its root directory
        mvn clean install
    Maven package & run srvls-parachutes-method project
        Clone the project
        Navigate to its root directory
        mvn clean package
        Deploy the produced .war, e.g., to Tomcat
        Open Swagger UI at http://[host]:[port]/swagger-ui/index.html
