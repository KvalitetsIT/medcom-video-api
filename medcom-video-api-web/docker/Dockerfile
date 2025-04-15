FROM ibm-semeru-runtimes:open-21-jre-jammy

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y gettext-base tzdata inetutils-telnet

RUN addgroup --system --gid 11000 appuser
RUN adduser --system --uid 11001 --group appuser

COPY   entrypoint.sh /home/appuser
RUN chmod +x /home/appuser/entrypoint.sh
COPY configtemplates  /home/appuser/configtemplates

ADD /maven/medcom-video-api-web.jar /home/appuser/medcom-video-api-web.jar
WORKDIR /home/appuser

## Default ENV

ENV management_server_port=8081
ENV management_endpoints_web_base-path=/manage/actuator
ENV spring_mvc_pathmatch_matching-strategy=ant_path_matcher
ENV management_endpoints_web_exposure_include=*

USER appuser

ENTRYPOINT [ "/home/appuser/entrypoint.sh" ]