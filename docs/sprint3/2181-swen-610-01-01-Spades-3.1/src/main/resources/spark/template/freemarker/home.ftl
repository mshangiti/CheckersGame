<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <meta http-equiv="refresh" content="4">
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
        <p>Welcome to the game  ${username}.</p>
            <#if playerChallengeStatus == "NO_CHALLENGES">
                <#if playersCount??>
                    <#if playersCount == 0>
                        <p>There are 0 players available online</p>
                    </#if>
                    <#if playersCount == 1>
                        <p>There is 1 player available online</p>
                        <p>Select a player from below to challenge him/her for a game </p>
                    </#if>
                    <#if playersCount gt 1>
                        <p>There are ${playersCount} players available online</p>
                        <p>Select a player from below to challenge him/her for a game </p>
                    </#if>
                </#if>

                <#if playersList??>
                    <#if playersCount gt 0>
                        <form action="./home" method="POST">
                        <#list playersList as i>
                            <#if i.name!=username>
                                <div class = "radio">
                                    <label><input type="radio" name="opponentName" value="${i.name}"> ${i.name} </label>
                                </div>
                            </#if>
                        </#list>
                            <br>
                            <input type="submit" value="Let's Play"/>
                            <input type="hidden" name="requestedOperation" value="1" />
                        </form>
                    </#if>
                </#if>
                <#if errorMessage??>
                        <div class="error">${errorMessage}</div>
                </#if>
            </#if>


            <#if playerChallengeStatus == "CHALLENGE_PENDING">
                <#if isPlayerChallenged!false>
                    <form action="./home" method="POST">
                        <div class = "radio">
                            <p>You have been challenged by ${opponentName}</p>
                            <label><input type="radio" name="requestedOperation" value="3"> Accept </label>
                            <label><input type="radio" name="requestedOperation" value="4"> Decline </label>
                        </div>
                        <br>
                        <input type="submit" value="Submit"/>
                    </form>
                <#else>
                    <form action="./home" method="POST">
                        <p>Waiting for opponent</p>
                        <br>
                        <input type="submit" value="Cancel Request"/>
                        <input type="hidden" name="requestedOperation" value="2" />
                    </form>
                </#if>
                <#if errorMessage??>
                        <div class="error">${errorMessage}</div>
                </#if>
            </#if>

            <#if playerChallengeStatus == "CHALLENGE_ACCEPTED">
                <#if isPlayerChallenged!false>
                    <p>Loading game...</p>
                <#else>
                    <p>Request has been accepted. Loading game...</p>
                </#if>
            </#if>

            <#if playerChallengeStatus == "CHALLENGE_DECLINED">
                <#if isPlayerChallenged!false>
                    <p>Cancelling game...</p>
                <#else>
                    <p>Request has been declined.</p>
                </#if>
            </#if>

            <#if playerChallengeStatus == "CHALLENGE_CANCELLED">
                <#if isPlayerChallenged!false>
                    <p>Request has been withdrawn.</p>
                <#else>
                    <p>Cancelling request...</p>
                </#if>
            </#if>

    </div>

</div>
</body>
</html>
