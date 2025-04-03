sudo docker pull postgres
sudo docker system prune
sudo docker run --name test-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres