FROM ibm-semeru-runtimes:open-21-jre

RUN apt-get update && apt-get install -y gettext-base tzdata inetutils-telnet

RUN groupadd --gid 11000 appuser && \
    useradd --gid 11000 --uid 11001 -m appuser

COPY entrypoint.sh /home/appuser
RUN chmod +x /home/appuser/entrypoint.sh

WORKDIR /opt/kvalitetsit
COPY /maven/application.cdx.json ./

WORKDIR /home/appuser

COPY config/logback.xml logback-spring.xml
COPY /maven/medcom-video-api-web-exec.jar web.jar

## Default ENV

ENV management_server_port=8081
ENV management_endpoints_web_base-path=/manage/actuator
ENV spring_mvc_pathmatch_matching-strategy=ant_path_matcher
ENV management_endpoints_web_exposure_include=*

USER appuser

ENTRYPOINT [ "/home/appuser/entrypoint.sh" ]