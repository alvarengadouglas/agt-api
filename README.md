----
# How to run locally

1. Run SQL Server
   sudo docker run -d -e 'ACCEPT_EULA=Y' -e 'MSSQL_SA_PASSWORD=SalsaVerify123!' -p 1401:1433 -d mcr.microsoft.com/mssql/server:2017-latest

2. Run Redis
   docker run -p 6379:6379 -d redis

