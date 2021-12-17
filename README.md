# imdb-search
Mock project for Empathy Academy - Search Path
<div id="top"></div>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/uo271773/imdb-search">
    <img src="images/cinema.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Search IMDb</h3>

  <p align="center">
    Busca tus películas favoritas y obtén información sobre ellas. Basado en la API de IMDb
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

For now, there is only available a functionality which returns a query the user writes and the name of the container. Try it writing "localhost:8080/search?query=ThingToSearch"
or with the command:
  ```sh
   curl localhost:8080/search?query=ThingToSearch
   ```
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

Juan Rodríguez - [@JuanRdlF](https://twitter.com/JuanRdlF) - juanr@empathy.co

Project Link: [https://github.com/uo271773/imdb-search](https://github.com/uo271773/imdb-search)

<p align="right">(<a href="#top">back to top</a>)</p>

