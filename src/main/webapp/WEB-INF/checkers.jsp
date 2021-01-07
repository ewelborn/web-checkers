<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style>
            /* https://stackoverflow.com/questions/826782/how-to-disable-text-selection-highlighting */
            .noselect {
                -webkit-touch-callout: none; /* iOS Safari */
                  -webkit-user-select: none; /* Safari */
                   -khtml-user-select: none; /* Konqueror HTML */
                     -moz-user-select: none; /* Old versions of Firefox */
                      -ms-user-select: none; /* Internet Explorer/Edge */
                          user-select: none; /* Non-prefixed version, currently
                                                supported by Chrome, Edge, Opera and Firefox */
                cursor: default;
            }
            
            .hidden {
                display: none;
            }
            
            td {
                width: 75px;
                height: 75px;
                margin: 0px;
                font-size: 40pt;
                text-align: center;
            }
            
            tr {
                margin: 0px;
            }
            
            .dark {
                background-color: #769656
            }
            
            .light {
                background-color: #eeeed2
            }
            
            .active {
                background-color: #66ff66
            }
            
            .active2 {
                background-color: #6666ff
            }
            
            #gameBoardTable {
                float: left;
                margin-right: 25px;
            }
            
            #lobbyInfo {
                
            }
        </style>
        <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <title>Checkers.com</title>
    </head>
    <body>
        <script>
            let gameID = "${game.getGameID()}";
            let playerName = "${player.getScreenName()}"
            let currentGameData = null;
            
            function postGameUpdate(data,onSuccess) {
                $.ajax({
                    url: "API/gameUpdate/"+gameID,
                    type: "POST",
                    contextType: "application/json",
                    data: data,
                    dataType: "text",
                    success: function(data) {
                        onSuccess(data);
                        getGameUpdate();
                    },
                    error: function(){
                        alert("Network communication error.");
                    }
                })
            }
            
            function getBoardGameSetting(givenKey) {
                returnValue = null;
                currentGameData.boardGame.boardGameSettings.forEach(function(setting){
                    if(setting.key === givenKey) {
                        returnValue = setting.value;
                    }
                })
                return returnValue;
            }
            
            function getPlayerFromName(name) {
                returnValue = null;
                currentGameData.players.forEach(function(player){
                    if(player.screenName === name) {
                        returnValue = player;
                    }
                })
                return returnValue;
            }
            
            function getSquareFromPosition(x,y) {
                return $("#x-"+x+"y-"+y);
            }
            
            function getSquareFromCheckerPiece(checkerPiece) {
                return getSquareFromPosition(checkerPiece.x,checkerPiece.y);
            }
            
            function getCheckerPieceFromPosition(x,y) {
                returnValue = null;
                currentGameData.boardGame.checkerPieces.forEach(function(checkerPiece){
                    if(checkerPiece["x"] === x && checkerPiece["y"] === y) {
                        returnValue = checkerPiece;
                    }
                });
                return returnValue;
            }
            
            function getManhattanDistance(x1,y1,x2,y2) {
                xAbs = Math.abs(x1 - x2);
                yAbs = Math.abs(y1 - y2);
                return xAbs + yAbs;
            }
            
            function submitMove(checkerPiece,x2,y2) {
                // The player's move comes in the form of X1-Y1-X2-Y2
                move = checkerPiece.x + "-" + checkerPiece.y + "-" + x2 + "-" + y2;
                // Replicate move on the clientside, at least temporarily, while we wait for a game update from the server
                getSquareFromPosition(checkerPiece.x,checkerPiece.y).html("");
                getSquareFromPosition(x2,y2).html(checkerPiece.unicodeRepresentation);
                // If we captured, then remove the capturing piece
                if(getManhattanDistance(checkerPiece.x,checkerPiece.y,x2,y2) >= 4) {
                    getSquareFromPosition(checkerPiece.x + ((x2 - checkerPiece.x)/2),checkerPiece.y + ((y2 - checkerPiece.y)/2)).html("");
                }
                // Now send for a game update.
                postGameUpdate("move="+move,function(){});
            }
            
            function movementIsDiagonal(x1,y1,x2,y2) {
                xAbs = Math.abs(x1 - x2);
                yAbs = Math.abs(y1 - y2);
                return xAbs === yAbs;
            }
            
            // Returns true if the selectedChecker can move to the given x, y position
            function validateMove(selectedChecker,x2,y2) {
                player = getPlayerFromName(playerName);
                
                // First, check if this checker even belongs to us
                if(selectedChecker.owner !== player.gameData.playerColor) {
                    return false;
                }
                
                // If the movement isn't diagonal, then throw it out!
                diagonal = movementIsDiagonal(selectedChecker.x,selectedChecker.y,x2,y2);
                if(diagonal === false) {
                    return false;
                }
                
                // Check how far we're moving
                captureMove = null;
                manhattanDistance = getManhattanDistance(selectedChecker.x,selectedChecker.y,x2,y2);
                if(manhattanDistance === 2) {
                    captureMove = false;
                } else if (manhattanDistance === 4) {
                    captureMove = true;
                } else {
                    return false;
                }
                
                // Make sure there's nothing already occupying the tile we want to move to
                if(getCheckerPieceFromPosition(x2,y2)) {
                    return false;
                }
                
                // If we're capturing, make sure that there's a valid piece in our path to capture
                if(captureMove) {
                    capturedPiecePositionX = selectedChecker.x + ((x2 - selectedChecker.x)/2);
                    capturedPiecePositionY = selectedChecker.y + ((y2 - selectedChecker.y)/2);
                    //console.log(capturedPiecePositionX);
                    //console.log(capturedPiecePositionY);
                    capturedPiece = getCheckerPieceFromPosition(capturedPiecePositionX,capturedPiecePositionY);
                    //console.log(capturedPiece);
                    if(capturedPiece) {
                        // Check that it's not our piece
                        if(capturedPiece.owner === player.gameData.playerColor) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                // Now, check if this checker is a pawn (not a king)
                if(selectedChecker.king === false) {
                    // Pawns are only allowed to move forward (positive Y for light, negative Y for dark)
                    if(selectedChecker.owner === "LIGHT") {
                        if(y2 - selectedChecker.y < 0) {
                            return false;
                        }
                    } else {
                        if(y2 - selectedChecker.y > 0) {
                            return false;
                        }
                    }
                }

                return true;
            }
            
            function checkForPossibleCaptures(checkerPiece) {
                let squares = [];
                // Check for valid moves on the four diagonals
                let distance = 2;
                for(let i=-1;i<3;i = i + 2) {
                    for(let j=-1;j<3;j = j + 2) {
                        let x = (i * distance) + checkerPiece.x;
                        let y = (j * distance) + checkerPiece.y;
                        let square = getSquareFromPosition(x,y);
                        if(square && validateMove(checkerPiece,x,y)) {
                            squares.push(square);
                        }
                    }
                }
                if(squares.length === 0) {
                    return false;
                }
                return squares;
            }
            
            function checkForPossibleMoves(checkerPiece) {
                let squares = [];
                // Check for valid moves on the four diagonals
                for(let distance=1;distance<=2;distance++) {
                    for(let i=-1;i<3;i = i + 2) {
                        for(let j=-1;j<3;j = j + 2) {
                            let x = (i * distance) + checkerPiece.x;
                            let y = (j * distance) + checkerPiece.y;
                            let square = getSquareFromPosition(x,y);
                            if(square && validateMove(checkerPiece,x,y)) {
                                squares.push(square);
                            }
                        }
                    }
                }
                if(squares.length === 0) {
                    return false;
                }
                return squares;
            }
            
            let selectedChecker = null;
            function checkerSelected(element,checkerPiece,x,y) {
                if(selectedChecker) {
                    
                } else {
                    player = getPlayerFromName(playerName);
                    if(player.gameData && checkerPiece.owner === player.gameData.playerColor) {
                        selectedChecker = checkerPiece;
                        $(element).addClass("active");
                        
                        let mustCapture = false;
                        if(getBoardGameSetting("Forced capture") === "true") {
                            currentGameData.boardGame.checkerPieces.forEach(function(thatCheckerPiece){
                                if(thatCheckerPiece.owner === player.gameData.playerColor) {
                                    if(checkForPossibleCaptures(thatCheckerPiece)) {
                                        mustCapture = true;
                                    }
                                }
                            })
                        }
                        
                        let squares = null;
                        if(mustCapture) {
                            squares = checkForPossibleCaptures(checkerPiece)
                        } else {
                            squares = checkForPossibleMoves(checkerPiece)
                        }
                        if(squares) {
                            squares.forEach(function(square){
                                square.addClass("active2");
                            });
                        }
                    }
                }
            }
            
            function squareSelected(element,x,y) {
                checkerPieceOnSquare = getCheckerPieceFromPosition(x,y);
                if(checkerPieceOnSquare) {
                    checkerSelected(element,checkerPieceOnSquare,x,y);
                }
                
                if(selectedChecker && !checkerPieceOnSquare) {
                    if(validateMove(selectedChecker,x,y) && (currentGameData.boardGame.gameState === "PLAYER_TURN" || currentGameData.boardGame.gameState === "PLAYER_TURN_DOUBLE_JUMPING") && currentGameData.boardGame.currentPlayer.screenName === playerName) {
                        submitMove(selectedChecker,x,y);
                    }
                    
                    $(".checkerTile").removeClass("active");
                    $(".checkerTile").removeClass("active2");
                    selectedChecker = null;
                }
            }
            
            function getGameUpdate(additionalSuccess) {
                $.ajax({
                    url: "API/gameUpdate/"+gameID,
                    type: "GET",
                    dataType: "json",
                    success: function(gameData){
                        if(gameData) {
                            if(currentGameData === null) {
                                currentGameData = gameData;
                            }
                            
                            let playerList = "";
                            gameData.players.forEach(function(player){
                                playerList += "<h3 " + (player.screenName === playerName ? "class='active'" : "") + ">" + (typeof player.screenName === "undefined" ? "Player joining..." : player.screenName) + "</h3>";
                            });
                            $("#playerInfo").html(playerList);

                            // Clear out the checkers already on the table
                            $("#gameBoardTable").find("td").html("");

                            gameData.boardGame.checkerPieces.forEach(function(checkerPiece) {
                                getSquareFromCheckerPiece(checkerPiece).html(checkerPiece.unicodeRepresentation);
                            });
                            
                            $("#passButton").prop("disabled",true);
                            
                            // PLAYER_TURN,WAITING_FOR_PLAYERS,GAME_OVER,GAME_OVER_PLAYER_RESIGNED,GAME_OVER_PLAYER_LOST_CONNECTION;
                            if(gameData.boardGame.gameState === "PLAYER_TURN") {
                                $("#itIsXTurn").html("It is " + gameData.boardGame.currentPlayer.screenName + "'s turn");
                            } else if(gameData.boardGame.gameState === "PLAYER_TURN_DOUBLE_JUMPING") {
                                $("#itIsXTurn").html("It is " + gameData.boardGame.currentPlayer.screenName + "'s turn [Double jumping]");
                                if(gameData.boardGame.currentPlayer.screenName === playerName && getBoardGameSetting("Forced capture") === "false") {
                                    $("#passButton").prop("disabled",false);
                                }
                            } else if(gameData.boardGame.gameState === "WAITING_FOR_PLAYERS") {
                                $("#itIsXTurn").html("Waiting for players...");
                            } else if(gameData.boardGame.gameState === "GAME_OVER") {
                                $("#itIsXTurn").html("Game over! " + gameData.boardGame.winner.screenName + " won!");
                            } else if(gameData.boardGame.gameState === "GAME_OVER_PLAYER_RESIGNED") {
                                $("#itIsXTurn").html("Other player resigned. " + gameData.boardGame.winner.screenName + " won!");
                            } else if(gameData.boardGame.gameState === "GAME_OVER_PLAYER_LOST_CONNECTION") {
                                $("#itIsXTurn").html("Other player lost connection. " + gameData.boardGame.winner.screenName + " won!");
                            }

                            currentGameData = gameData;
                            if(additionalSuccess) {
                                additionalSuccess();
                            }
                        }
                    },
                    error: function(){
                        alert("Network communication error.");
                    }
                });
            }
            
            if(playerName.length === 0) {
                playerName = prompt("Please enter a screenname");
                postGameUpdate("screenName="+playerName,function(){});
            }
        </script>
        <h1>Checkers.com</h1>
        <h2 id="youArePlayingAs"></h2>
        <h2 id="itIsXTurn"></h2>
        <table id="gameBoardTable" cellspacing="0" class=".noselect"></table>
        <div id="lobbyInfo">
            <div>
                <div id="playerInfo">
                    <h2>Helter Skelter</h2>
                    <h2>Hey Jude</h2>
                </div>
                <button id="resignButton">Resign</button>
                <button id="passButton">Pass turn</button>
                <h2>The game code is <span id="gameCode">${game.getGameID()}</span></h2>
                <a href="" id="gameCodeLink"></a>
            </div>
        </div>
        <script>
            $("#gameCode").html($("#gameCode").html().toUpperCase());
            $("#gameCodeLink").html(window.location.href);
            
            var boardSize = ${game.getBoardGame().getBoardSize()};
            var bg = "dark";
            for(var y=boardSize-1;y>=0;y--) {
                var row = "<tr>";
                for(var x=boardSize-1;x>=0;x--) {
                    row += "<td class='checkerTile "+bg+"' id='x-"+x+"y-"+y+"'></td>";
                    // Shift the background color for each cell
                    if(bg === "dark") { bg = "light"; } else { bg = "dark"; }
                }
                row += "</tr>";
                $("#gameBoardTable").append(row);
                // Shift the background color for each row
                if(bg === "dark") { bg = "light"; } else { bg = "dark"; }
            }
            
            $("td").on("click",function(){
                position = this.id.split("-");
                x = parseInt(position[1].substr(0,position[1].length - 1));
                y = parseInt(position[2]);
                squareSelected(this,x,y);
            });
            
            $("#resignButton").on("click",function(){
                postGameUpdate("resign=true",function(){ window.location.replace("index.html"); });
            });
            
            getGameUpdate(function(){
                player = getPlayerFromName(playerName);
                $("#youArePlayingAs").html("You are playing as " + player.gameData.playerColor);
                if(player.gameData.playerColor === "DARK") {
                    $("#gameBoardTable").each(function(){
                        var arr = $.makeArray($("tr",this).detach());
                        arr.reverse();
                        $(this).append(arr);
                    });
                    $('#gameBoardTable tr').each(function() {
                        var tds = $(this).children('td').get().reverse();
                        $(this).append(tds);
                    });
                }
                
                console.log(getBoardGameSetting("Forced capture"));
                console.log(getBoardGameSetting("Double jump"));
                if(getBoardGameSetting("Forced capture") === "false" && getBoardGameSetting("Double jump") === "true") {
                    $("#passButton").on("click",function(){
                        postGameUpdate("passTurn=true",function(){});
                    });
                } else {
                    $("#passButton").addClass("hidden")
                }
            });
            
            setInterval(function() {
                getGameUpdate();
            }, 1000);
        </script>
    </body>
</html>