CREATE OR REPLACE FUNCTION order_load(p_order_number text) RETURNS SETOF "order" AS
$$

  SELECT ROW(p_order_number,
             now(),
             ROW('1234',
                 now(),
                 ROW('Max Mustermann', 'Musterstr. 1', '12345', 'Berlin', 'DE')::address,
                 ROW('Max Mustermann', 'Musterstr. 1', '12345', 'Berlin', 'DE')::address
             )::customer,
             ROW('Max Mustermann', 'Musterstr. 1', '12345', 'Berlin', 'DE')::address,
             ROW('Max Mustermann', 'Musterstr. 1', '12345', 'Berlin', 'DE')::address,
             (SELECT ARRAY[ROW('1234', 99.50)::order_item, ROW('5678', 39.95)::order_item])::order_item[]
         )::"order"

$$ LANGUAGE SQL SECURITY DEFINER;
