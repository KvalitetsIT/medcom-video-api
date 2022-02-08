FROM adoptopenjdk:11-jre-openj9

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y gettext-base tzdata inetutils-telnet

RUN addgroup   --system --gid 1001 appuser & adduser  --system --uid  1001   --group appuser

COPY   entrypoint.sh /home/appuser
RUN chmod +x /home/appuser/entrypoint.sh
COPY configtemplates  /home/appuser/configtemplates

ADD /maven/medcom-video-api-web.jar /home/appuser/medcom-video-api-web.jar
WORKDIR /home/appuser

## Default ENV

ENV management_server_port=8081
ENV management_server_servlet_context-path=/manage
ENV management_endpoints_web_exposure_include=health,info,prometheus,appmetrics

RUN chmod a-w /etc
USER appuser

ENTRYPOINT [ "/home/appuser/entrypoint.sh" ]