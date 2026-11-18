FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN mkdir -p bin && javac -d bin $(find src -name '*.java')
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-cp", "bin", "gamehub.Main"]
