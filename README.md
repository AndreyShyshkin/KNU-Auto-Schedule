# KNU-Auto-Schedule

**PostgreSQL**: Ensure PostgreSQL is installed and running.
_ Host: `localhost`
_ Port: `5432`
_ Database: `schedule_db`
_ User: `postgres` \* Password: `password`

docker run -d \
 --name schedule_pg_container \
 -p 5432:5432 \
 -e POSTGRES_USER=postgres \
 -e POSTGRES_PASSWORD=password \
 -e POSTGRES_DB=schedule_db \
 postgres:latest
