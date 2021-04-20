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

    public static final String aggTempQuery = "CREATE TABLE if not exists temp_aggregate (\n" +
            "\tid serial NOT NULL,\n" +
            "\t\"name\" varchar(70) UNIQUE NOT NULL,\n" +
            "\t\"no. of products\" int4 NOT NULL,\n" +
            "\tCONSTRAINT temp_aggregate_pkey PRIMARY KEY (id, name));";

    public static final String prodTempQuery = "CREATE TABLE if not exists temp_products (\n" +
            "\tsku varchar(255) NOT NULL,\n" +
            "\t\"name\" varchar(70) NULL,\n" +
            "\tdescription varchar(1024) NULL,\n" +
            "\tCONSTRAINT temp_products_pkey PRIMARY KEY (sku));";

    public static final String dropAggTemp = "DROP TABLE temp_aggregate";

    public static final String dropProdTemp = "DROP TABLE temp_products;";

    public static final String mergeProducts = "INSERT INTO products (sku, \"name\", description) \n" +
            "SELECT \"sku\", \"name\", \"description\" \n" +
            "FROM temp_products";

    public static final String mergeAggregate = "INSERT INTO aggregate SELECT * FROM temp_aggregate;";

    public static final String dropDupProducts = "delete from temp_products tp USING products p where p.sku = tp.sku;";

    public static final String dropDupAggregate = "delete from temp_aggregate ta USING aggregate a where a.id = ta.id;";

    public static final String updateProducts = "UPDATE products p\n" +
            "SET \"name\" = tp.name, description = tp.description\n" +
            "From temp_products tp\n" +
            "where p.sku = tp.sku;";

    public static final String updateAggregate = "UPDATE aggregate a\n" +
            "SET \"no. of products\" = ta.\"no. of products\"\n" +
            "From temp_aggregate ta\n" +
            "where a.name = ta.name;";
}
