upstream ${parachuteName} {
    server [Endpoint of original application and port, e.g., 192.168.0.1:9090];
    server [Endpoint of function @ AWS Lambda] backup;
}

server {
    listen 80;

    location / {
        proxy_pass http://[Endpoint of original application and port, e.g., 192.168.0.1:9090];
    }

    location ~* ${uri} {
        proxy_pass http://${parachuteName};
    }
}