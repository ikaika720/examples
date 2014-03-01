FROM inabatk/postgresql

ADD create_db_and_user_posgresql.sql create_db_and_user_posgresql.sql
ADD create_table_postgresql.sql create_table_postgresql.sql
ADD drop_table_postgresql.sql drop_table_postgresql.sql

RUN su postgres -c "/usr/local/postgresql/bin/pg_ctl -D /var/lib/pgsql/data/ start";\
  sleep 10;\
  su postgres -c "/usr/local/postgresql/bin/psql -U postgres -d postgres -f /create_db_and_user_posgresql.sql";\
  su postgres -c "/usr/local/postgresql/bin/psql -U user01 -d mydb -f /create_table_postgresql.sql";\
  su postgres -c "/usr/local/postgresql/bin/pg_ctl -D /var/lib/pgsql/data/ stop";\
  sleep 10;

EXPOSE 5432

CMD su postgres -c "/usr/local/postgresql/bin/postgres -D /var/lib/pgsql/data"

