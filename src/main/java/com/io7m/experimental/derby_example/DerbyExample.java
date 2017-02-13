/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.experimental.derby_example;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.jdbc.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Properties;

@Component(immediate = true)
public final class DerbyExample
{
  private DataSourceFactory sources;

  public DerbyExample()
  {

  }

  @Reference(
    cardinality = ReferenceCardinality.MANDATORY,
    policy = ReferencePolicy.STATIC,
    policyOption = ReferencePolicyOption.RELUCTANT)
  public void onDatabaseRegister(
    final DataSourceFactory factory)
  {
    System.out.println("onDatabaseRegister: " + factory);
    this.sources = factory;
  }

  @Activate
  public void onActivate()
  {
    System.out.println("onActivate: ");

    try {
      final Properties props = new Properties();
      props.put("url", "jdbc:derby:/tmp/sales");
      props.put("user", "SA");
      props.put("databaseName", "sales");
      props.put("createDatabase", "create");

      final DataSource source = this.sources.createDataSource(props);
      try (final Connection conn = source.getConnection()) {
        conn.setAutoCommit(false);

        final DatabaseMetaData meta = conn.getMetaData();
        System.out.printf(
          "driver: %s %s\n", meta.getDriverName(), meta.getDriverVersion());

        {
          int count = 0;
          try (final ResultSet set = meta.getCatalogs()) {
            while (set.next()) {
              ++count;
              System.out.printf("catalog: %s\n", set.getString(1));
            }
          }

          System.out.printf("Database has %d catalogs\n", Integer.valueOf(count));
        }

        {
          int count = 0;
          try (final ResultSet set = meta.getSchemas()) {
            while (set.next()) {
              ++count;
              System.out.printf("schema: %s\n", set.getString(1));
            }
          }

          System.out.printf(
            "Database has %d schemas\n",
            Integer.valueOf(count));
        }

        conn.rollback();
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Deactivate
  public void onDeactivate()
  {
    System.out.println("onDeactivate: ");
  }
}
