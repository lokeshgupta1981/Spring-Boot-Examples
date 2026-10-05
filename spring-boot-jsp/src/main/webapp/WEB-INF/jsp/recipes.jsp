<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Recipes</title>
  <link href="/css/style.css" rel="stylesheet">
</head>
<body>
  <h1>Recipes</h1>
  <table>
    <tr><th>Name</th><th>Minutes</th></tr>
    <c:forEach items="${recipes}" var="recipe">
      <tr>
        <td><c:out value="${recipe.name}"/></td>
        <td>${recipe.minutes}</td>
      </tr>
    </c:forEach>
  </table>
  <p><a href="/recipes/new">Add a recipe</a></p>
</body>
</html>
