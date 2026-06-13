@echo off
echo Starting Resume Analyzer Platform...

start "Zookeeper" cmd /k "cd C:\kafka\kafka_2.13-3.7.0 && bin\windows\zookeeper-server-start.bat config\zookeeper.properties"

timeout /t 5

start "Kafka" cmd /k "cd C:\kafka\kafka_2.13-3.7.0 && bin\windows\kafka-server-start.bat config\server.properties"

timeout /t 8

start "Auth Service" cmd /k "cd C:\Study\Projects\Resume_Analyzer\resume-analyzer-platform\auth-service && mvn spring-boot:run"

timeout /t 5

start "API Gateway" cmd /k "cd C:\Study\Projects\Resume_Analyzer\resume-analyzer-platform\api-gateway && mvn spring-boot:run"

timeout /t 5

start "Resume Service" cmd /k "cd C:\Study\Projects\Resume_Analyzer\resume-analyzer-platform\resume-service && mvn spring-boot:run"

timeout /t 5

start "AI Service" cmd /k "cd C:\Study\Projects\Resume_Analyzer\resume-analyzer-platform\ai-service && mvn spring-boot:run"

timeout /t 10

start "Frontend" cmd /k "cd C:\Study\Projects\Resume_Analyzer\resume-analyzer-frontend && npm run dev"

echo All services started!