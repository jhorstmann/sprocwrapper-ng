package net.jhorstmann.sprocwrapperng;

import com.impossibl.postgres.api.data.Record;
import com.impossibl.postgres.datetime.instants.Instant;
import com.impossibl.postgres.types.CompositeType;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeMapper {

    private static final Set<Class> ATOMIC_TYPES = new HashSet<Class>(Arrays.asList(Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, String.class, BigInteger.class, BigDecimal.class));


    static <T> T construct(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Could not create instance of [" + clazz.getName() + "]", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not create instance of [" + clazz.getName() + "], constructor threw", e.getCause());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Can not instantiate a class without default constructor");
        }
    }

    public static <T> List<T> mapList(ResultSet rs, Class<T> clazz) throws SQLException, NoSuchFieldException, IllegalAccessException {
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(map(rs, clazz));
        }
        return result;
    }

    public static <T> List<T> mapList(Array array, Class<T> clazz) throws SQLException, NoSuchFieldException, IllegalAccessException {
        ResultSet rs = array.getResultSet();
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(map(rs, clazz, true));
        }
        return result;
    }

    public static <T> T map(ResultSet rs, Class<T> clazz) throws SQLException, NoSuchFieldException, IllegalAccessException {
        return map(rs, clazz, false);
    }

    private static <T> T map(ResultSet rs, Class<T> clazz, boolean isArray) throws SQLException, NoSuchFieldException, IllegalAccessException {
        final ResultSetMetaData md = rs.getMetaData();
        final int columnCount = md.getColumnCount();
        if (ATOMIC_TYPES.contains(clazz)) {
            if (columnCount != (isArray ? 2 : 1)) {
                throw new IllegalStateException("Could not convert resultset with [" + columnCount + "] columns to [" + clazz.getSimpleName() + "]");
            } else {
                return rs.getObject(isArray ? 2 : 1, clazz);
            }
        } else if (isArray) {
            final Record record = rs.getObject(2, Record.class);
            return map(record, clazz);
        } else {
            final T result = construct(clazz);
            for (int i = 1; i <= columnCount; i++) {
                final String columnName = md.getColumnName(i);
                final int columnType = md.getColumnType(i);
                final String property = Naming.underscoreToCamel(columnName);
                final Field field = clazz.getDeclaredField(property);
                final Class<?> type = field.getType();
                field.setAccessible(true);
                switch (columnType) {
                    case Types.STRUCT:
                        final Record record = rs.getObject(i, Record.class);
                        final Object bean = map(record, type);
                        field.set(result, bean);
                        break;
                    case Types.ARRAY:
                        if (!(type.isAssignableFrom(List.class))) {
                            throw new IllegalStateException("Can not convert array to [" + type.getName() + "]");
                        }
                        final Class<?> elementType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        final Array array = rs.getArray(i);
                        final List<?> list = mapList(array, elementType);
                        field.set(result, list);
                        break;
                    case Types.DATE:
                        final Date tmpDate = rs.getDate(i);
                        final Date date = rs.wasNull() ? null : new Date(tmpDate.getTime());
                        field.set(result, date);
                        break;
                    case Types.TIMESTAMP:
                        final Date tmpTimestamp = rs.getTimestamp(i);
                        final Date timestamp = rs.wasNull() ? null : new Date(tmpTimestamp.getTime());
                        field.set(result, timestamp);
                        break;
                    default:
                        final Object val = rs.getObject(i, type);
                        field.set(result, val);
                        break;
                }
            }
            return result;

        }

    }

    public static <T> T map(Record record, Class<T> clazz) throws SQLException, NoSuchFieldException, IllegalAccessException {
        final T result = construct(clazz);
        final CompositeType ct = record.getType();

        final Object[] values = record.getValues();

        final int length = values.length;

        for (int i = 0; i < length; i++) {
            final CompositeType.Attribute attribute = ct.getAttribute(i+1);
            final String attributeName = attribute.name;
            final String property = Naming.underscoreToCamel(attributeName);
            final Field field = clazz.getDeclaredField(property);
            Class<?> type = field.getType();
            field.setAccessible(true);

            switch (attribute.type.getCategory()) {
                case Composite:
                    final Record value = (Record) values[i];
                    final Object bean = map(value, type);
                    field.set(result, bean);
                    break;
                case Array:
                    // TODO: primitive arrays
                    final Record[] array = (Record[]) values[i];
                    if (!(type.isAssignableFrom(List.class))) {
                        throw new IllegalStateException("Can not convert array to [" + type.getName() + "]");
                    }
                    final Class<?> elementType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

                    final List<Object> list = new ArrayList<>(array.length);
                    for (int j = 0; j < array.length; j++) {
                        list.add(map(array[j], elementType));

                    }
                    break;
                case DateTime:
                    final Instant instant = (Instant)values[i];
                    field.set(result, instant == null ? null : new Date(instant.getMillisUTC()));
                    break;
                default:
                    // TODO: simple type conversions
                    field.set(result, values[i]);
            }

        }


        return result;
    }


}
