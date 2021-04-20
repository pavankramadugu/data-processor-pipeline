# Large File Processor

Application to run large Data Ingestion processes in a Distributed Fashion.

## Approach
I have tried to solve this problem in 2 Approaches:

- By Traversing through Partitions of Dataframe and Doing Parallel Batch Upsert `main branch`

- By Writing to Temporary table in DB and Update/Add New Records of using the temp tables DB Side asynchronously and drop the temp tables.

Submitted the 1st Approach as Solution. 2nd Approach Methods are also present in same code but commented out from execution in PipelineFlow Class. 

## Prerequisites

- Need [Docker Desktop](https://www.docker.com/products/docker-desktop) Installed on your  Machine, and it should be Up and Running.
- [Java 8/11](https://jdk.java.net/archive/)
- Package Manager [Maven](https://maven.apache.org/download.cgi) to Build Jar with Dependencies.

## Steps to Run the Pipeline

I have added a shell script file with all the  necessary commands in sequence to execute the Pipeline.

```bash
./run.sh
```
Alternatively you can run manually by running these commands in sequence,

```bash
mvn clean package

docker build -t data-processor:latest .

docker-compose up
```
## Technology Stack
- [Java 11](https://jdk.java.net/archive/) as Programming Language.
- [Apache Spark 3.1.1]() Framework to leverage Distributed Computing.
- [Postgres](https://www.postgresql.org/download/) as Data Store.
## Database Schema
![Product Schema](images/products-schema.PNG)
![Aggregate Schema](images/aggregate-schema.PNG)
## Database Scripts
### Table Creation
Check if Exists else Create.
```sql
CREATE TABLE if not exists products sku varchar(255) NOT NULL,
            name varchar(70) NULL,
            description varchar(1024) NULL,
            CONSTRAINT products_pkey PRIMARY KEY (sku));

CREATE TABLE if not exists aggregate id serial NOT NULL,
            name varchar(70) UNIQUE NOT NULL,
            "no. of products" int4 NOT NULL,
            CONSTRAINT aggregate_pkey PRIMARY KEY (id, name));
```
### Table Upsertion

Insert, if Conflict then Update.

```sql
INSERT INTO products ("name", sku, description)
            VALUES (?, ?, ?) ON CONFLICT (sku) DO UPDATE
            SET description = EXCLUDED.description, name = EXCLUDED.name;


INSERT INTO aggregate (name, "no. of products")
            VALUES (?, ?) ON CONFLICT (name) DO UPDATE
            SET "no. of products" = EXCLUDED."no. of products";
```
### Tables Info

- Products table Entries count `466,693`.

   ![Products_Top10](images/top10products.PNG)


- Aggregate table Entries count `212,630`.

   ![Aggregate Top10](images/top10aggregate.PNG) 


### Points Achieved
- Followed Oops Concepts in designing Application and Also Used Asynchrnous Programming.

- Supports Upsertion of data, without truncating the products table.

- Supports Parallell Non Blocking Data Ingestion.

- Can Update the Products table with 'sku' as Primary Key.

- All the Products are ingested into a single table `Products`.

- An Aggregated table with columns `name` and `no. of products` of name `Aggregate` has been created in database.

### Things that can be improved if had more days
- I would have extensively explored the effect of partitoning by running in cluster like environment.

- Would have worked on testing performance difference betwwen Partiton Based Upsertion approach and DB Side Temporary Table approach.

- Would have tested pipeline performance between Relational Databases and Nosql Distributed Databases like Cassandra etc, 
