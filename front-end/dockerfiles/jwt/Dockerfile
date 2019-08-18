FROM nginx

COPY dockerfiles/jwt/nginx.conf /etc/nginx/

COPY src /usr/share/nginx/html

EXPOSE 8000

CMD ["nginx", "-c", "nginx.conf", "-g", "daemon off;"]
