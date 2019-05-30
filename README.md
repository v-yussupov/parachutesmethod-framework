
#### Description
This project is a research prototype implementing the concepts described in the paper 
> Serverless Parachutes: Preparing Chosen Functionalities for Exceptional Cases

---

#### Prerequisites

Following projects are required for testing the implementation:

1. Java parachute annotations library: https://github.com/v-yussupov/parachutesmethod-annotations
2. Example application: https://github.com/v-yussupov/parachutesmethod-exampleapp
3. Evaluation results: https://github.com/v-yussupov/parachutesmethod-evaluation

Following software needs to be installed:
1. AWS SAM (requires AWS CLI and Docker installed) 
2. Maven (including corresponding environment variables)    

Additional notes:
1. AWS credentials must be set in .aws/credentials file
2. AWS user must be able to deploy aws cloud formation stacks and have all related permissions 

---
#### Steps:
1. Maven build & install the annotations projects:
    * Clone the project
    * Navigate to its root directory
    * mvn clean install
2. Maven package & run srvls-parachutes-method project:
    * Clone the project
    * Navigate to its root directory
    * mvn clean package
    * Deploy the produced .war, e.g., to Tomcat
    * Open Swagger UI at http://[host]:[port]/swagger-ui/index.html
3. Use Example application repository link as an input
4. Extract / Generate / Refine / Deploy produced artifacts
