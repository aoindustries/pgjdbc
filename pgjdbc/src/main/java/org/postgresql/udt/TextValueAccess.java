/*
 * Copyright (c) 2018, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.udt;

import org.postgresql.core.BaseConnection;
import org.postgresql.jdbc.BooleanTypeUtil;
import org.postgresql.jdbc.PgArray;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.jdbc.PgResultSet;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;


// TODO: Consider how to share this implementation with PgResultSet
public class TextValueAccess extends BaseValueAccess {

  private final int oid;
  private final String value;

  public TextValueAccess(BaseConnection connection, int oid, String value) {
    super(connection);
    this.oid = oid;
    this.value = value;
  }

  @Override
  protected int getOid() {
    return oid;
  }

  @Override
  public String getString() throws SQLException {
    return value;
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getBoolean(int)
   */
  @Override
  public boolean getBoolean() throws SQLException {
    return (value == null) ? false : BooleanTypeUtil.fromString(value);
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getByte(int)
   */
  @Override
  public byte getByte() throws SQLException {
    return PgResultSet.toByte(PgResultSet.trimMoney(value));
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getShort(int)
   */
  @Override
  public short getShort() throws SQLException {
    return PgResultSet.toShort(value);
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getInt(int)
   */
  @Override
  public int getInt() throws SQLException {
    return PgResultSet.toInt(PgResultSet.trimMoney(value));
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getLong(int)
   */
  @Override
  public long getLong() throws SQLException {
    return PgResultSet.toLong(PgResultSet.trimMoney(value));
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getFloat(int)
   */
  @Override
  public float getFloat() throws SQLException {
    return PgResultSet.toFloat(PgResultSet.trimMoney(value));
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getDouble(int)
   */
  @Override
  public double getDouble() throws SQLException {
    return PgResultSet.toDouble(PgResultSet.trimMoney(value));
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getBigDecimal(int)
   */
  @Override
  public BigDecimal getBigDecimal() throws SQLException {
    return PgResultSet.toBigDecimal(PgResultSet.trimMoney(value));
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgConnection#encodeString(java.lang.String)
   */
  @Override
  public byte[] getBytes() throws SQLException {
    try {
      return connection.getEncoding().encode(value);
    } catch (IOException ioe) {
      throw new PSQLException(GT.tr("Unable to translate data into the desired encoding."),
          PSQLState.DATA_ERROR, ioe);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getDate(int)
   */
  @Override
  public Date getDate() throws SQLException {
    return connection.getTimestampUtils().toDate(getDefaultCalendar(), value);
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getTime(int)
   */
  @Override
  public Time getTime() throws SQLException {
    return connection.getTimestampUtils().toTime(getDefaultCalendar(), value);
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getTimestamp(int)
   */
  @Override
  public Timestamp getTimestamp() throws SQLException {
    return connection.getTimestampUtils().toTimestamp(getDefaultCalendar(), value);
  }

  @Override
  public Object getObject(UdtMap udtMap) throws SQLException {
    // TODO: typemap?  Could probably do a lot more in AbstractValueAccess for getObject()
    //       regarding intercepting custom type maps and doing conversions from the various get* methods.
    //       Then this would become a protected getObjectImpl() used when no custom maps applied.
    return value;
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getArray(int)
   */
  @Override
  public Array getArray() throws SQLException {
    return (value == null) ? null : new PgArray(connection, oid, value);
  }

  @Override
  public boolean wasNull() throws SQLException {
    return value == null;
  }
}
