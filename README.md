# recipes-app-backend

# Spring Boot, H2 database (embedded), Spring Security, JWT, JPA, Rest API

This repository is a proof of concept for a recipe application where users can login and manage their favorite recipes. Users first need to register themselves to the application.
Users need to provide their username (email) and password to login and thereafter a token will be generated and stored on the client side to be sent to the server on every subsequent request.

The application follows a stateless protocol and uses restful services to add, update, get and delete data. The security mechanism used in the application is JWT based security.


## Steps to Setup

**1. Clone the application**

```bash
git clone https://github.com/chanakyass/recipes-app-backend.git
```

**2. embedded h2 database already available. Database is demodb and username and pasword are dev and dev. 
Howvever if you want to change you can create a new h2 database and update following properties**

+ open `src/main/resources/application.properties`
+ update `spring.datasource.url` property by adding new database name in the end
+ open `src/main/resources/application-dev.properties`
+ change `spring.datasource.username` and `spring.datasource.password` as per your h2 database installation

**4. Run the app using maven**

```bash
mvn  spring-boot:run -Dspring-boot.run.jvmArguments="-DACTIVE_PROFILE=dev"
```
The app will start running at <http://localhost:8080>

## Explore Rest APIs

The app defines following CRUD APIs.

### Auth

v1 -- below is the version of the api. env variable {VERSION_NUMBER} 

| Method | Url | Decription | Sample Valid Request Body | 
| ------ | --- | ---------- | --------------------------- |
| POST   | /api/v1/public/register | Sign up | [JSON](#user) |
| POST   | /api/v1/public/login | Log in | [JSON](#login) |
| POST   | /api/v1/admin/register | Sign up | [JSON](#user) |
| POST   | /api/v1/admin/login | Log in | [JSON](#login) |

### Users

| Method | Url | Description | Sample Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/v1/user/:id | Get user profile with given id | |
| PUT    | /api/v1/user | Get user profile by username | [JSON](#userUpdate) |


### Recipes

| Method | Url | Description | Sample Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| POST    | /api/v1/recipe | post a recipe | [JSON](#recipe) |
| PUT    | /api/v1/recipe | update a recipe | [JSON](#recipe) |
| GET    | /api/v1/recipe/:id | Get user recipe with given id | |
| GET    | /api/v1/recipes | Get all recipies available in DB | |
| DELETE    | /api/v1/recipe/:id | Delete user recipe with given id | |


### Ingredients

| Method | Url | Description | Sample Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| POST    | /api/v1/admin/ingredients | adds list of ingredients in DB | [JSON](#listOfIngredients) |
| PUT    | /api/v1/admin/ingredient | update a ingredient | [JSON](#ingredient) |
| GET    | /api/v1/ingredients/all | Get all recipies available in DB | |
| GET    | /api/v1/ingredients?startsWith=xyz | Used to get recipes whose names starting with | |


Test them using postman or any other rest client.

## Sample Valid JSON Request Bodys

##### <a id="user">Sign Up -> /api/public/register and /api/admin/register</a>
```json
{
  "firstName": "firstName",
  "middleName": "",
  "lastName": "lastName",
  "profileName": "profileName",
  "userSummary": "summary",
  "dob": "yyyy-mm-dd",
  "email": "name@rest.com",
  "password": "pass",
}
```

##### <a id="login">Log In -> /api/v1/public/login and api/v1/admin/login</a>
```json
{
	"username": "foo@rest.com",
	"password": "pass"
}
```

##### <a id="userUpdate">Update User -> /api/v1/user</a>
```json
{
  "id": 1, 
	"firstName": "firstName",
  "middleName": "",
  "lastName": "lastName",
  "profileName": "profileName",
  "userSummary": "summary",
  "dob": "yyyy-mm-dd",
  "email": "name@rest.com"
}
```

##### <a id="recipe">Create Post -> /api/v1/recipe</a>
```json
{
	  "name": "Paneer Tikka",
  "description": "Indian food/Punjabi",
  "createdOn": "2021-06-22T18:54:04Z",
  "itemType": "VEG",
  "serving": 4,
  "recipeIngredients": [
    {
      "ingredient": {
        "name": "Salt"
      },
      "quantity": 0.75,
      "uom": 0
    },
        { 
      "ingredient": {
          "name": "Paneer"
      },
      "quantity": 0.25,
      "uom": 2
    },
    {
        "ingredient": {
            id: 1, 
            "name": "Oil"
        },
        "quantity": 25,
        "uom": 3
    }

        
  ]
}
```
##### <a id="ingredient">update ingredient -> /api/v1/admin/ingredient</a>
```json
{
"id": 1,
"name": "name",
"description": "description"
}
```

##### <a id="listOfIngredients">Create ingredients -> /api/v1/admin/ingredients</a>
```json
[
{
"name": "name",
"description": "description"
}
]

```
