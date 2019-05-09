AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Description: >
  SAM Template for generated parachute functionalities

<#if globals??>
# More info about Globals: https://github.com/awslabs/serverless-application-model/blob/master/docs/globals.rst
Globals:
    Function:
        Timeout: 20
    Environment: # More info about Env Vars: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#environment-object
        Variables:
            TABLE_NAME:
            ENDPOINT_OVERRIDE:
<#else>
</#if>

Resources:
    <#list functions as function>
    ${function.name}:
        <#-- More info about Function Resource: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#awsserverlessfunction -->
        Type: AWS::Serverless::Function
        Properties:
            CodeUri:
                Bucket: ${function.bucketName}
                Key: ${function.objectKey}
            Handler: ${function.packageName}.${function.className}::${function.handler}
            Runtime: ${function.runtime}
            <#-- More info about API Event Source: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#api -->
            Events:
                ${function.className}:
                    Type: Api
                    Properties:
                        Path: ${function.endpoint}
                        Method: post

    </#list>