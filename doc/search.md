# Search

Search a title between indexed titles.
It can be filtered by genre, type or year.
The pagination works with `start`, which is the start 
offset, and `rows`, the number of items to be retrieved.

**URL** : `/search?query=`

**Method** : `GET`

**URL Params**

* `query=[string]` (Required)
* `genres=[string]` (Optional)
* `type=[string]` (Optional)
* `genre=[string]` (Optional)
* `year=[string]` (Optional)
* `start=[int]` (Optional)
* `rows=[int]` (Optional)

**Auth required** : No

**Permissions required** : None

**Request example**

```
http://localhost:8080/search?query=avengers&genre=Action&type=movie,tvEpisode&year=2000/2001,2008/2015&rows=5&start=2
```

## Success Response

**Condition** : If indexing process started correctly.

**Code** : `200 OK`

**Response example** : 
```
{
   "total":31,
   "items":[
      {
         "primaryTitle":"Avengers Assemble",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":132.15198,
         "originalTitle":"Avengers Assemble",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":8.8,
         "startYear":2012,
         "tConst":"tt2207840",
         "numVotes":307,
         "isAdult":false
      },
      {
         "primaryTitle":"New Avengers",
         "titleType":"tvEpisode",
         "runtimeMinutes":22,
         "score":124.63511,
         "originalTitle":"New Avengers",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":8.8,
         "startYear":2012,
         "tConst":"tt2202952",
         "numVotes":268,
         "isAdult":false
      },
      {
         "primaryTitle":"The Avengers Protocol: Part 1",
         "titleType":"tvEpisode",
         "runtimeMinutes":22,
         "score":104.010124,
         "originalTitle":"The Avengers Protocol: Part 1",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":6.8,
         "startYear":2012,
         "tConst":"tt2473294",
         "numVotes":336,
         "isAdult":false
      },
      {
         "primaryTitle":"Avengers Disassembled",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":96.59236,
         "originalTitle":"Avengers Disassembled",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":8.2,
         "startYear":2015,
         "tConst":"tt4579416",
         "numVotes":148,
         "isAdult":false
      },
      {
         "primaryTitle":"Avengers: Impossible",
         "titleType":"tvEpisode",
         "runtimeMinutes":23,
         "score":96.51164,
         "originalTitle":"Avengers: Impossible",
         "genres":[
            "Action",
            "Adventure",
            "Animation"
         ],
         "averageRating":6.7,
         "startYear":2013,
         "tConst":"tt3129478",
         "numVotes":154,
         "isAdult":false
      }
   ],
   "aggregations":{
      "types":{
         "movie":3,
         "tvEpisode":28
      },
      "ranges":{
         "2010-2020":31
      },
      "genres":{
         "Action":31,
         "Sci-Fi":2,
         "Adventure":27,
         "Fantasy":1,
         "Animation":19,
         "Comedy":9
      }
   },
   "suggestions":[
      
   ]
}
```
