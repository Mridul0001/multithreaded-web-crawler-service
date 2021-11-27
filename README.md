### Mutlithreaded Web Crawler as a Service
Created using Spring Boot. This web crawler can crawl websites with custom level configurations. As it is built on the concept of multithreading, it can crawl multiple websites simultaneously.

#### Steps to start web crawler
1. Clone the repository into your local machine. Make sure you have Maven and Java installed
2. Start the project with following command
> ./mvnw spring-boot:run
3. Now hit the following localhost endpoint with the following payload (POST)
> localhost:8080/v1/start
> 
    {
    "seeds":[
        "https://www.your-first-url.com",
        "https://www.your-second-url.com"
    ],
    "maxLevel":1
    }

4. Response time depends on how deep you need to go.

Note: ***maxLevel*** attribute accepts only three values (0,1,2). Results will be stored inside ***com.web.crawler.datafiles*** as .csv files.
