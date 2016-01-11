FROM	maven:3-jdk-8-onbuild

COPY . /usr/src/rzd

ENTRYPOINT ["java", "-jar", "/usr/src/app/target/RzdParser-1.0-SNAPSHOT-jar-with-dependencies.jar", "-config", "/usr/src/app/request.config"]
