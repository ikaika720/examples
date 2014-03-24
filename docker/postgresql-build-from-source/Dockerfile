FROM ubuntu:latest
RUN apt-get update
RUN apt-get -y install wget build-essential libreadline-dev zlib1g-dev
RUN wget -q http://ftp.postgresql.org/pub/source/v9.3.4/postgresql-9.3.4.tar.gz.sha256
RUN wget -q http://ftp.postgresql.org/pub/source/v9.3.4/postgresql-9.3.4.tar.gz
RUN sha256sum -c postgresql-9.3.4.tar.gz.sha256
RUN tar xzf postgresql-9.3.4.tar.gz
RUN cd postgresql-9.3.4;\
  chmod +x configure;\
  ./configure --prefix=/usr/local/postgresql-9.3.4;\
  make;\
  make install
RUN ln -s /usr/local/postgresql-9.3.4 /usr/local/postgresql
RUN mkdir -p /var/lib/pgsql/data
RUN useradd postgres
RUN chown postgres:postgres /var/lib/pgsql/data
RUN su postgres -c "/usr/local/postgresql/bin/initdb -D /var/lib/pgsql/data"
RUN echo "host    all             all             0.0.0.0/0               md5" >> /var/lib/pgsql/data/pg_hba.conf
RUN echo "listen_addresses='*'" >> /var/lib/pgsql/data/postgresql.conf
RUN su postgres -c "/usr/local/postgresql/bin/pg_ctl -D /var/lib/pgsql/data/ start";\
  sleep 10;\
  su postgres -c "/usr/local/postgresql/bin/psql -U postgres -d postgres -c \"ALTER USER postgres WITH ENCRYPTED PASSWORD 'password';\"";\
  su postgres -c "/usr/local/postgresql/bin/pg_ctl -D /var/lib/pgsql/data/ stop";\
  sleep 10

EXPOSE 5432

CMD su postgres -c "/usr/local/postgresql/bin/postgres -D /var/lib/pgsql/data"

