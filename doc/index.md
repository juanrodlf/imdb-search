# Index

Index a title database saved in a *.tsv* file.
You should put the file path as a query. 
Optionally you can add the path to a *.tsv* ratings file.

**URL** : `/index`

**Method** : `POST`

**URL Params**

* `path=[string]` (Required)
* `ratingsPath=[string]` (Optional)

**Auth required** : No

**Permissions required** : None

**Request example**

```
curl -d "path=/Users/juanr/Downloads/data.tsv&ratingsPath=/Users/juanr/Downloads/data-3.tsv" -X POST http://localhost:8080/index
```

## Success Response

**Condition** : If indexing process started correctly.

**Code** : `200 OK`
