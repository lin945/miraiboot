# 基础镜像
FROM openjdk:16-slim-buster

# 作者信息
MAINTAINER "2315554550@qq.com"


RUN mkdir /home/qbot


WORKDIR home/qbot

# 暴露8080端口
EXPOSE 8088

ADD . /home/qbot/

ENTRYPOINT ["java","-jar","/home/qbot/bot.jar"]