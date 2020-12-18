<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <meta http-equiv="refresh" content="10">
    <title>${title} | Web Checkers</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>
<body>
<div class="page">

    <h1>Web Checkers</h1>

    <div class="navigation">
        <a href="/">my home</a>
    </div>

    <div class="body">
        <p>Welcome to the world of online Checkers.</p>

        <form action="./signin" method="POST">
            <p class="inline">Username:</p>
            <input class="inline" type="text" placeholder="type in a username, e.g., Moe" size="30" name="username">

            <input class="inline" type="submit" value="Sign-in"/>
        </form>

        <br/>
        <#if errorMessage??><div class="error">${errorMessage}</div></#if>
    </div>
</div>
</body>
</html>
