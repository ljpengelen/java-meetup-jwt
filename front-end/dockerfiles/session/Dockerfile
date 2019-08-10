FROM nginx

ADD dockerfiles/session/nginx.conf /etc/nginx/

ADD src /usr/share/nginx/html

EXPOSE 8000

CMD ["nginx", "-c", "nginx.conf", "-g", "daemon off;"]
