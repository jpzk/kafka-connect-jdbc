/**
 * Copyright 2015 Datamountaineer.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.datamountaineer.streamreactor.connect.jdbc.dialect;

import com.datamountaineer.streamreactor.connect.jdbc.sink.SinkRecordField;
import com.datamountaineer.streamreactor.connect.jdbc.common.ParameterValidator;
import org.apache.kafka.connect.data.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides support for Oracle database
 */
public class OracleDialect extends Sql2003Dialect {
  public OracleDialect() {
    super(getSqlTypeMap(), "\"", "\"");
  }

  private static Map<Schema.Type, String> getSqlTypeMap() {
    Map<Schema.Type, String> map = new HashMap<>();
    map.put(Schema.Type.INT8, "TINYINT");
    map.put(Schema.Type.INT16, "SMALLINT");
    map.put(Schema.Type.INT32, "INTEGER");
    map.put(Schema.Type.INT64, "NUMBER(19)");
    map.put(Schema.Type.FLOAT32, "REAL");
    map.put(Schema.Type.FLOAT64, "BINARY_DOUBLE");
    map.put(Schema.Type.BOOLEAN, "NUMBER(1,0)");
    map.put(Schema.Type.STRING, "VARCHAR(256)");
    map.put(Schema.Type.BYTES, "BLOB");
    return map;
  }

  @Override
  public List<String> getAlterTable(String tableName, Collection<SinkRecordField> fields) {
    ParameterValidator.notNullOrEmpty(tableName, "table");
    ParameterValidator.notNull(fields, "fields");
    if (fields.isEmpty()) {
      throw new IllegalArgumentException("<fields> is empty.");
    }
    final StringBuilder builder = new StringBuilder("ALTER TABLE ");
    builder.append(tableName.toUpperCase()); //yes oracles needs it uppercase
    builder.append(" ADD(");

    boolean first = true;
    for (final SinkRecordField f : fields) {
      if (!first) {
        builder.append(",");
      } else {
        first = false;
      }
      builder.append(lineSeparator);
      builder.append(escapeColumnNamesStart + f.getName() + escapeColumnNamesEnd);
      builder.append(" ");
      builder.append(getSqlType(f.getType()));
      builder.append(" NULL");
    }
    builder.append(")");

    final List<String> query = new ArrayList<String>(1);
    query.add(builder.toString());
    return query;
  }

  @Override
  protected String handleTableName(String tableName) {
    return tableName.toUpperCase();
  }
}
