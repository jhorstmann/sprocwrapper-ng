CREATE TYPE customer AS (
  customer_number           text,
  created                   timestamp WITH TIME ZONE,
  default_billing_address   address,
  default_shipping_address  address
);