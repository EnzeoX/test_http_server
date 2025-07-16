# Test Http Server

## Tech Stack

**Server:** Java 11, Embedded Tomcat, Log4j2


#### Create .jar using Maven

`mvn clean package`

#### Run it

`-java -jar TestHttpServer-1.0.jar`



## API Reference

#### 1. Get UserInfo by userId
##### Returns the top 20 results of the user at all levels in descending order result, level_id

```http
  GET /api/v1/info/userinfo/{userId}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `userId` | `long` | **Required**. id of user |

#### 2. Get UserInfo by levelId
##### Returns top 20 users and their results at the selected level in descending order result, user_id
###### Request example:
```http
  GET /api/v1/info/userinfo/1
```
###### Response example:
```json
[
  {"user_id":1,"level_id":1,"result":55},
  {"user_id":1,"level_id":3,"result":15},
  {"user_id":1,"level_id":2,"result":8}
]
```

```http
  GET /api/v1/info/levelinfo/{levelId}
```
| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `levelId`      | `long` | **Required**. Id of level |
###### Request example:
```http
  GET /api/v1/info/levelinfo/1
```
###### Response example:
```json
[
  {"user_id":2,"level_id":3,"result":22},
  {"user_id":1,"level_id":3,"result":15}
]
```

#### 3. PUT data (user info)
##### Takes 3 parameters in JSON (user_id, level_id, result) sets the user's result at the level.
```http
	PUT /api/v1/info/setinfo
```
##### Request body example:
```json
    {
        "userId": 22,
        "levelId": 20,
        "result": 182
    }
```


