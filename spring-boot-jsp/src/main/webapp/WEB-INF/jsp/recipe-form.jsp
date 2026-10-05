<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Add Recipe</title>
  <link href="/css/style.css" rel="stylesheet">
</head>
<body>
  <h1>Add a recipe</h1>
  <form action="/recipes" method="post">
    <label>Name <input type="text" name="name" required></label>
    <label>Minutes <input type="number" name="minutes" min="1" required></label>
    <button type="submit">Save</button>
  </form>
</body>
</html>
