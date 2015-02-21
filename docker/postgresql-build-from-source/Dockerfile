FROM ubuntu:14.10
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install wget build-essential libreadline-dev zlib1g-dev &&\
  apt-get clean && rm -Rf /var/cache/*
RUN wget -P /tmp -q http://ftp.postgresql.org/pub/source/v9.4.1/postgresql-9.4.1.tar.gz.sha256 &&\
  wget -P /tmp -q http://ftp.postgresql.org/pub/source/v9.4.1/postgresql-9.4.1.tar.gz &&\
  cd /tmp &&\
  sha256sum -c postgresql-9.4.1.tar.gz.sha256 &&\
  tar xzf postgresql-9.4.1.tar.gz &&\
  cd postgresql-9.4.1 &&\
  chmod +x configure &&\
  ./configure --prefix=/usr/local/postgresql &&\
  make &&\
  make install &&\
  mkdir -p /var/lib/pgsql/data &&\
  useradd postgres &&\
  chown postgres:postgres /var/lib/pgsql/data &&\
  rm -rf /tmp/postgresql*
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

