http {
<#if default??>
    upstream default {
        server [Endpoint of original application];
    }
<#else>
    upstream ${parachuteName} {
        server [Endpoint of original application];
        server [Endpoint of function @ AWS Lambda] backup;
    }
</#if>

    server {
<#if default??>
        location ${uri} {
            proxy_pass http://default;
        }
<#else>
        location ${uri} {
            proxy_pass http://${parachuteName};
        }
</#if>
    }
}