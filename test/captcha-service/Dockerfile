FROM 10.113.75.156:31104/library/jdk8:v3
WORKDIR /home/
ENV RUN_ENV = "dev"
ENV JAVA_OPTS=""
ENV JAVA_OPTS_INNER=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=50.0 -XX:InitialRAMPercentage=50.0 -XX:+PrintCommandLineFlags "
ADD ./target/captcha-service-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT [ "sh", "-c", "java $SKYWALKING_AGENT $JAVA_OPTS_INNER $JAVA_OPT -Dspring.profiles.active=${RUN_ENV} -jar app.jar" ]