<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <meta http-equiv="refresh" content="15">
    <title>${title} | Web Checkers</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>
<body>
<div class="page">

    <h1>Web Checkers</h1>

    <div class="navigation">
        <a href="/">my home</a>
        <a href="/signout">signout</a>
    </div>

    <div class="body">
        <#if gameDecision??>
            <#if gameDecision == 1>
                <h2>Congratulations, you <span style="color:green"> WON THE GAME.</span></h2>
                <p>${gameMessage}</p>
            <#else>
                <#if gameDecision == 0>
                    <h2>Sorry, you <span style="color:red"> LOST THE GAME.</span></h2>
                     <p>${gameMessage}</p>
                <#else>
                <h2>Sorry, <span style="color:green">GAME IS A TIE.</span> </h2>
                    <#if numberOfNoCaptureMoves??>
                    <p>The game is considered a <span style="color:green">tie</span> as ${numberOfNoCaptureMoves} sequential non-capture moves were made.</p>
                    <#else>
                    <p>The game is considered a <span style="color:green">tie</span> as 70 sequential non-capture moves were made.</p>
                    </#if>
                </#if>
            </#if>
        <#else >
            <h2>Ops, you should not be here</h2>
        </#if>

        <form action="./home" method="GET">
            <input class="inline" type="submit" value="Go back to lobby"/>
        </form>
    </div>

</div>
</body>
</html>
