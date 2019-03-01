http {
<#if default??>
    upstream default {
        server [Endpoint of original application and port, e.g., 192.168.0.1:9090];
    }
<#else>
    upstream ${parachuteName} {
        server [Endpoint of original application and port, e.g., 192.168.0.1:9090];
        server [Endpoint of function @ AWS Lambda] backup;
    }
</#if>

    server {
        listen 80;

        location ${uri} {
<#if default??>
            proxy_pass http://default;
<#else>
            proxy_pass http://${parachuteName};
</#if>
        }
    }
}