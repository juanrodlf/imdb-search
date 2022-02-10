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

### Indexing
The endpoint `/index` is used to create an index of documents from a *.tsv* file. 
You can use the *title.basics.tsv.gz* and *title.ratings.tsv.gz* from [IMDb Webpage](https://www.imdb.com/interfaces/).
Mandatory parameter:
* `path`: Path to the tsv file containing the titles to be indexed.

Optional parameters:
* `ratingsPath`: Path to the tsv file containing the ratings for the titles

Example of usage:
  ```sh
   curl -d "path=pathToTitlesFile.tsv&ratingsPath=pathToRatingsFile.tsv" -X POST http://localhost:8080/index
   ```

### Search request

The endpoint `/search` is used to search and filter a title.
Mandatory parameter:
* `query`: It is the primary title of the title to be searched.

Optional parameters:
* `type`: Filters result by types, they can be separated by a comma.
If there are more than one, results will include titles with at least
one of the specified types.
* `genre`: Filters result by genres, they can be separated by a comma.
If there are more than one, results will include titles with 
at least one of the specified genres.
* `year`: Filters by ranges of years, they can be separated by a comma.
Format is YYYY/YYYY,YYYY/YYYY,etc. If there are more than one range,
results will include all of them. 
* `start`: Starting document offset. Defaults to 0.
* `rows`: Defines the number of hits to return. Defaults to 10.

Example:
  ```sh
   curl "localhost:8080/search?query=spiderman&genre=Action&type=Movie,tvEpisode&year=2000/2001,2008/2015"
   ```
This query would search all the titles containing 'spiderman' on 
primary title, filtered by genre 'action' AND type 'movie' OR 'tvEpisode',
included on ranges '2000-2001' (both included) OR '2008-2015'
(both included)

### Search response

The response is a json object which contains the following fields:
* `total`: Total number of results for the query.
* `items`: A list containing the titles retrieved and the score for each one.
* `aggregations`: Total results depending on fields:
  * `genres`: Number of results of each gender for the query.
  * `types`: Number of results depending on each type for the query.
  * `ranges`: Number of results for each decade from year 1900.

Example of response for previous query:

```{toggle}
{
   "total":27,
   "items":[
      {
         "primaryTitle":"The Amazing Spider-Man",
         "titleType":"movie",
         "runtimeMinutes":136,
         "score":4107.2383,
         "originalTitle":"The Amazing Spider-Man",
         "genres":[
            "Action",
            "Adventure",
            "Sci-Fi"
         ],
         "averageRating":6.9,
         "startYear":2012,
         "tConst":"tt0948470",
         "numVotes":621683,
         "isAdult":false
      },
      {
         "primaryTitle":"The Amazing Spider-Man 2",
         "titleType":"movie",
         "runtimeMinutes":142,
         "score":3309.829,
         "originalTitle":"The Amazing Spider-Man 2",
         "genres":[
            "Action",
            "Adventure",
            "Sci-Fi"
         ],
         "averageRating":6.6,
         "startYear":2014,
         "tConst":"tt1872181",
         "numVotes":469525,
         "isAdult":false
      },
      {
         "primaryTitle":"I Am Spider-Man",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":84.31079,
         "originalTitle":"I Am Spider-Man",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":7.2,
         "startYear":2012,
         "tConst":"tt2016724",
         "numVotes":190,
         "isAdult":false
      },
      {
         "primaryTitle":"Itsy Bitsy Spider-Man",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":77.298096,
         "originalTitle":"Itsy Bitsy Spider-Man",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":7.1,
         "startYear":2013,
         "tConst":"tt2949840",
         "numVotes":155,
         "isAdult":false
      },
      {
         "primaryTitle":"The Savage Spider-Man",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":71.98677,
         "originalTitle":"The Savage Spider-Man",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":7.5,
         "startYear":2014,
         "tConst":"tt3105108",
         "numVotes":129,
         "isAdult":false
      },
      {
         "primaryTitle":"The Avenging Spider-Man: Part 1",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":67.9639,
         "originalTitle":"The Avenging Spider-Man: Part 1",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":8.2,
         "startYear":2014,
         "tConst":"tt3091022",
         "numVotes":156,
         "isAdult":false
      },
      {
         "primaryTitle":"The Avenging Spider-Man: Part 2",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":65.75958,
         "originalTitle":"The Avenging Spider-Man: Part 2",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":8.2,
         "startYear":2014,
         "tConst":"tt3105060",
         "numVotes":144,
         "isAdult":false
      },
      {
         "primaryTitle":"The Avenging Spider-Man",
         "titleType":"movie",
         "runtimeMinutes":48,
         "score":50.858482,
         "originalTitle":"The Avenging Spider-Man",
         "genres":[
            "Action",
            "Sci-Fi",
            "Thriller"
         ],
         "averageRating":6,
         "startYear":2015,
         "tConst":"tt4321248",
         "numVotes":57,
         "isAdult":false
      },
      {
         "primaryTitle":"Batman VS Spider-Man",
         "titleType":"tvEpisode",
         "score":41.380707,
         "originalTitle":"Batman VS Spider-Man",
         "genres":[
            "Action",
            "Animation",
            "Comedy"
         ],
         "averageRating":8,
         "startYear":2012,
         "tConst":"tt4128544",
         "numVotes":29,
         "isAdult":false
      },
      {
         "primaryTitle":"Spider-Man",
         "titleType":"tvEpisode",
         "score":33.7278,
         "originalTitle":"Spider-Man",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":9.1,
         "startYear":2013,
         "tConst":"tt6353072",
         "numVotes":7,
         "isAdult":false
      }
   ],
   "aggregations":{
      "types":{
         "movie":4,
         "tvEpisode":23
      },
      "ranges":{
         "2010-2020":27
      },
      "genres":{
         "Action":27,
         "Sci-Fi":3,
         "Adventure":19,
         "Thriller":1,
         "Animation":15,
         "Family":1,
         "Comedy":9
      }
   },
   "suggestions":[
      
   ]
}
```

If the query does not match any title, the response will contain some 
suggestions. This is an example of searching "irxnman"

```
{
   "total":0,
   "items":[
      
   ],
   "aggregations":{
      
   },
   "suggestions":[
      {
         "score":0.0046554636,
         "text":"iron man"
      },
      {
         "score":0.0027191767,
         "text":"iranian"
      },
      {
         "score":0.0024995566,
         "text":"ironman"
      },
      {
         "score":0.00068895257,
         "text":"ironmen"
      },
      {
         "score":0.00046277102,
         "text":"iranían"
      }
   ]
}
```
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

Juan Rodríguez - [@JuanRdlF](https://twitter.com/JuanRdlF) - juanr@empathy.co

Project Link: [https://github.com/uo271773/imdb-search](https://github.com/uo271773/imdb-search)

<p align="right">(<a href="#top">back to top</a>)</p>

