FROM openjdk:21
ADD target/trade-0.0.1-SNAPSHOT.jar trade-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar", "/trade-0.0.1-SNAPSHOT.jar"]
