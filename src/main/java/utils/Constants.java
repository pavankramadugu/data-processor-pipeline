package utils;

public class Constants {

    public static final String productsQuery = "CREATE TABLE if not exists products (\n" +
            "\tsku varchar(255) NOT NULL,\n" +
            "\t\"name\" varchar(70) NULL,\n" +
            "\tdescription varchar(1024) NULL,\n" +
            "\tCONSTRAINT products_pkey PRIMARY KEY (sku))";

    public static final String aggregateQuery = "CREATE TABLE if not exists aggregate (\n" +
            "\tid serial NOT NULL,\n" +
            "\t\"name\" varchar(70) UNIQUE NOT NULL,\n" +
            "\t\"no. of products\" int4 NOT NULL,\n" +
            "\tCONSTRAINT aggregate_pkey PRIMARY KEY (id, name));";

    public static final String upsertProducts = "INSERT INTO products (\"name\", sku, description)\n" +
            "VALUES (?, ?, ?)\n" +
            "ON CONFLICT (sku) DO UPDATE SET description = EXCLUDED.description, name = EXCLUDED.name;";

    public static final String upsertAggregate = "INSERT INTO \"aggregate\" (\"name\", \"no. of products\")\n" +
            "VALUES (?, ?)\n" +
            "ON CONFLICT (name) DO UPDATE SET \"no. of products\" = EXCLUDED.\"no. of products\";";
}
