FROM ubuntu:latest
RUN apt-get update
RUN apt-get -y install wget build-essential libreadline-dev zlib1g-dev
RUN wget -q http://ftp.postgresql.org/pub/source/v9.3.3/postgresql-9.3.3.tar.gz.sha256
RUN wget -q http://ftp.postgresql.org/pub/source/v9.3.3/postgresql-9.3.3.tar.gz
RUN sha256sum -c postgresql-9.3.3.tar.gz.sha256
RUN tar xzf postgresql-9.3.3.tar.gz
RUN cd postgresql-9.3.3;\
  chmod +x configure;\
  ./configure --prefix=/usr/local/postgresql-9.3.3;\
  make;\
  make install
RUN mkdir -p /var/lib/pgsql/data
RUN useradd postgres
RUN chown postgres:postgres /var/lib/pgsql/data
RUN su postgres -c "/usr/local/postgresql-9.3.3/bin/initdb -D /var/lib/pgsql/data"
RUN echo "host    all             all             0.0.0.0/0               md5" >> /var/lib/pgsql/data/pg_hba.conf
RUN echo "listen_addresses='*'" >> /var/lib/pgsql/data/postgresql.conf
RUN su postgres -c "/usr/local/postgresql-9.3.3/bin/pg_ctl -D /var/lib/pgsql/data/ start";\
  sleep 10;\
  su postgres -c "/usr/local/postgresql-9.3.3/bin/psql -U postgres -d postgres -c \"ALTER USER postgres WITH ENCRYPTED PASSWORD 'password';\""

EXPOSE 5432

USER postgres
CMD ["/usr/local/postgresql-9.3.3/bin/postgres","-D","/var/lib/pgsql/data"]
