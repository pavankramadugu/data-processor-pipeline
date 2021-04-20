FROM adoptopenjdk/openjdk11

RUN apt update -qq \
 && apt install -y -qq --no-install-recommends wget bash\
 && rm -rf /var/lib/apt/list/*

RUN wget https://archive.apache.org/dist/spark/spark-3.1.1/spark-3.1.1-bin-hadoop2.7.tgz

RUN tar xzf spark-3.1.1-bin-hadoop2.7.tgz -C / \
 && rm spark-3.1.1-bin-hadoop2.7.tgz \
 && ln -s /spark-3.1.1-bin-hadoop2.7 /spark

RUN echo "export PATH=$PATH:/spark/bin" >> ~/.bashrc

COPY target/data-pipeline.jar data-pipeline.jar

COPY products.csv products.csv

ENTRYPOINT ["java", "-jar", "data-pipeline.jar"]