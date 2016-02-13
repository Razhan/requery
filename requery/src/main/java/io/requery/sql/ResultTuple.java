/*
 * Copyright 2016 requery.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.requery.sql;

import io.requery.query.Aliasable;
import io.requery.query.Expression;
import io.requery.query.Tuple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Implementation of a tuple created from a query.
 *
 * @author Nikhil Purushe
 */
class ResultTuple implements Tuple {

    private final Map<String, Object> keyMap;
    private final Object[] values;

    ResultTuple(int size) {
        if (size <= 0) {
            throw new IllegalStateException();
        }
        keyMap = new HashMap<>(size);
        values = new Object[size];
    }

    private String keyFor(Expression<?> expression) {
        String key = expression.name();
        if (expression instanceof Aliasable) {
            key = ((Aliasable) expression).aliasName();
        }
        return key;
    }

    void set(int index, Expression<?> expression, Object value) {
        keyMap.put(keyFor(expression), value);
        values[index] = value;
    }

    @Override
    public <V> V get(Expression<V> key) {
        Object value = keyMap.get(keyFor(key));
        if (value == null) {
            throw new NoSuchElementException();
        }
        return key.classType().cast(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V get(int index) {
        return (V) values[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V get(String key) {
        return (V) keyMap.get(key);
    }

    @Override
    public int count() {
        return values.length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResultTuple) {
            ResultTuple other = (ResultTuple) obj;
            return Arrays.equals(values, other.values);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        sb.append(" [ ");
        for (Map.Entry<String, Object> entry : keyMap.entrySet()) {
            if (index > 0) {
                sb.append(", ");
            }
            Object value = entry.getValue();
            sb.append(value == null ? "null" : entry.getValue().toString());
            index++;
        }
        sb.append(" ]");
        return sb.toString();
    }
}