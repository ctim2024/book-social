# Build stage
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17
ARG PROFILE=prod
ARG APP_VERSION=1.0.5

WORKDIR /app
COPY --from=build /build/target/book-network-*.jar /app/

ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}
ENV DB_URL=jdbc:mysql://mysql-db-bsn:3306/book_social_network

ENV EMAIL_HOSTNAME=smtp.gmail.com
ENV EMAIL_USER_NAME=belgecemjrad2018@gmail.com
ENV EMAIL_PASSWORD="avhn rgmu lnff kgea"

CMD java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL}  book-network-${JAR_VERSION}.jar
