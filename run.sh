bash -c "mvn clean package"

bash -c "docker build -t data-processor:latest ."

bash -c "docker-compose up"