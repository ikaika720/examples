PostgreSQL example
==================
This Dockerfile builds and setup PostgreSQL from the source.

After the container started up, use the database server with psql. The password for postgres is "password". See line 21 in Dockerfile.

    CONTAINER=$(sudo docker run -d <image_name>)
    CONTAINER_IP=$(sudo docker inspect -format='{{.NetworkSettings.IPAddress}}' $CONTAINER)
    psql -h $CONTAINER_IP -p 5432 -d postgres -U postgres -W

See [PostgreSQL Service](http://docs.docker.io/en/latest/examples/postgresql_service/) in docker document.
