# imdb-search
Mock project for Empathy Academy - Search Path
<div id="top"></div>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/uo271773/imdb-search">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Search IMDb</h3>

  <p align="center">
    Search for your favourite films and see information about them. <br />Based on IMDb API
    <br />
    <a href="https://github.com/uo271773/imdb-search/issues">Report Bug</a>
    ·
    <a href="https://github.com/uo271773/imdb-search/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This project is part of the Academy Search Path of Empathy co. It is being developed by Juan Rodríguez (`uo271773`).

<p align="right">(<a href="#top">back to top</a>)</p>



### Built With

* [Elasticsearch] (https://www.elastic.co)
* [SpringBoot] (https://spring.io/projects/spring-boot)
* [Java] (https://www.java.com/)
* [Docker] (https://www.docker.com)
* [Maven] (https://maven.apache.org)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

For now, the project is being developed, anyway, here you will find a tutorial to make your firsts steps.

### Prerequisites

Install Docker, Elasticsearch and Maven
* [Install Docker] (https://docs.docker.com/get-docker/)
* Pull Elasticsearch image from Docker and run it
  ```sh
  docker pull docker.elastic.co/elasticsearch/elasticsearch:7.11.1
  ```
  ```sh
  docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.11.1
  ```
* [Install Maven] (https://maven.apache.org/download.cgi). If you have SDKMAN, you can also use:
  ```sh
  sdk install maven 3.8.4
  ```
 

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/uo271773/imdb-search.git
   ```
2. Execute it with your favourite IDE or with the command (on the root folder of the project)
   ```sh
   mvn spring-boot:run
   ```

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

### Request

The endpoint `/search` is used to search and filter a title.
Mandatory parameter:
* `query`: It is the primary title of the title to be searched.

Optional parameters:
* `type`: Filters result by types, they can be separated by a comma.
If there are more than one, results will include titles with at least
one of the specified types.
* `genres`: Filters result by genres, they can be separated by a comma.
If there are more than one, results will include titles with 
at least one of the specified genres.
* `year`: Filters by ranges of years, they can be separated by a comma.
Format is YYYY/YYYY,YYYY/YYYY,etc. If there are more than one range,
results will include all of them. 

Example:
  ```sh
   curl "localhost:8080/search?query=spiderman&genre=action&type=movie,tvEpisode&year=2000/2001,2008/2015"
   ```
This query would search all the titles containing 'spiderman' on 
primary title, filtered by genre 'action' AND type 'movie' OR 'tvEpisode',
included on ranges '2000-2001' (both included) OR '2008-2015'
(both included)

### Response

The response is a json object which contains the following fields:
* `total`: Total number of results for the query.
* `items`: A list containing the first 10 titles retrieved.
* `aggregations`: Total results depending on fields:
  * `genres`: Number of results of each gender for the query.
  * `types`: Number of results depending on each type for the query.
  * `ranges`: Number of results for each decade from year 1900.

Example of response for previous query:

```{toggle}
{
   "aggregations" : {
      "genres" : {
         "Action" : 4,
         "Adventure" : 2,
         "Comedy" : 2
      },
      "ranges" : {
         "1900.0-1910.0" : 0,
         "1910.0-1920.0" : 0,
         "1920.0-1930.0" : 0,
         "1930.0-1940.0" : 0,
         "1940.0-1950.0" : 0,
         "1950.0-1960.0" : 0,
         "1960.0-1970.0" : 0,
         "1970.0-1980.0" : 0,
         "1980.0-1990.0" : 0,
         "1990.0-2000.0" : 0,
         "2000.0-2010.0" : 0,
         "2010.0-2020.0" : 4
      },
      "types" : {
         "tvEpisode" : 4
      }
   },
   "items" : [
      {
         "endYear" : null,
         "genres" : [
            "Action"
         ],
         "isAdult" : false,
         "originalTitle" : "Spiderman 2011",
         "primaryTitle" : "Spiderman 2011",
         "runtimeMinutes" : null,
         "startYear" : 2012,
         "tConst" : "tt4198962",
         "titleType" : "tvEpisode"
      },
      {
         "endYear" : null,
         "genres" : [
            "Action"
         ],
         "isAdult" : false,
         "originalTitle" : "Gentleman Spiderman",
         "primaryTitle" : "Gentleman Spiderman",
         "runtimeMinutes" : null,
         "startYear" : 2013,
         "tConst" : "tt4199102",
         "titleType" : "tvEpisode"
      },
      {
         "endYear" : null,
         "genres" : [
            "Action",
            "Adventure",
            "Comedy"
         ],
         "isAdult" : false,
         "originalTitle" : "Honest Review: Spiderman 1 & 2",
         "primaryTitle" : "Honest Review: Spiderman 1 & 2",
         "runtimeMinutes" : 9,
         "startYear" : 2012,
         "tConst" : "tt2384655",
         "titleType" : "tvEpisode"
      },
      {
         "endYear" : null,
         "genres" : [
            "Action",
            "Adventure",
            "Comedy"
         ],
         "isAdult" : false,
         "originalTitle" : "Top 10 Un-Amazing Spiderman 2 Moments",
         "primaryTitle" : "Top 10 Un-Amazing Spiderman 2 Moments",
         "runtimeMinutes" : 26,
         "startYear" : 2014,
         "tConst" : "tt4143124",
         "titleType" : "tvEpisode"
      }
   ],
   "total" : 4
}
```

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

Juan Rodríguez - [@JuanRdlF](https://twitter.com/JuanRdlF) - juanr@empathy.co

Project Link: [https://github.com/uo271773/imdb-search](https://github.com/uo271773/imdb-search)

<p align="right">(<a href="#top">back to top</a>)</p>

