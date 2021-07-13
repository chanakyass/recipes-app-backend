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

**2. Embedded h2 database already available. If you want to use the existing embedded database execute the below command to run the application.**

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DACTIVE_PROFILE=dev -DDEV_USERNAME=dev -DDEV_PASSWORD=dev -DDEV_DBNAME=demodb"
```

**3 If you want to configure your own embedded database, do the following**

[How to create embedded H2 database](https://www.h2database.com/html/features.html#database_url)

then run the following command to run application

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DACTIVE_PROFILE=dev -DDEV_USERNAME=#username you have used -DDEV_PASSWORD=#password you have used -DDEV_DBNAME=#db you have created"
```

The app will start running at <http://localhost:8080>

## Explore Rest APIs

The app defines following CRUD APIs.

### Auth

v1 is the version number of the API. Since this is development environment we can use v1 version

| Method | Url | Decription | Sample Valid Request Body | 
| ------ | --- | ---------- | --------------------------- |
| POST   | /api/v1/public/register | User Sign up | [JSON](#user) |
| POST   | /api/v1/public/login | User Log in | [JSON](#login) |
| POST   | /api/v1/admin/register | Admin Sign up | [JSON](#user) |
| POST   | /api/v1/admin/login | Admin Log in | [JSON](#login) |

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

##### <a id="user">Sign Up</a>
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

##### <a id="login">Log In </a>
```json
{
	"username": "foo@rest.com",
	"password": "pass"
}
```

##### <a id="userUpdate">Update User </a>
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

##### <a id="recipe">Create/Modify recipe </a>
List of Units of meaurements than can be given for uom field ["MILLIGRAMS", "GRAMS", "KILOGRAMS", "MILLILITRES", "LITRES", "TEA_SPOON", "TABLE_SPOON", "NUMBER"]
```json
{
  "id": "---integer to be given for modify mode",
  "name": "Paneer Tikka",
  "description": "Indian food/Punjabi",
  "recipeImageAddress": "#address to the image",
  "createdOn": "2021-06-22T18:54:04Z",
  "itemType": "VEG",
  "serving": 4,
  "cookingInstructions": "random instructions",
  "recipeIngredients": [
    {
      "ingredient": {
        "name": "Salt"
      },
      "quantity": 0.75,
      "uom": "MILLIGRAMS"
    },
    { 
      "ingredient": {
          "name": "Paneer"
      },
      "quantity": 0.25,
      "uom": "MILLIGRAMS"
    },
    {
      "ingredient": {
	   "id": 1, 
	   "name": "Oil"
       },
       "quantity": 25,
       "uom": "MILLIGRAMS"
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
