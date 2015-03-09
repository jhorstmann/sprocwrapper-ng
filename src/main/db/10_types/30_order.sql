CREATE TYPE "order" AS (
  order_number     text,
  created          TIMESTAMP WITH TIME ZONE,
  customer         customer,
  shipping_address address,
  billing_address  address,
  items            order_item[]
)