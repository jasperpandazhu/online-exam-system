var backgroundColor1 = "#6bc2a8";
var backgroundColor2 = "#edfdff";
var balls = [];
var numBalls = 20;
var movingBallColor = "#f3a9af";
var backgroundBallColor = "#fff";

window.onload = function(){

    if( $("#legend").text() == "Student Login" ) {
        backgroundColor1 = "#f39095";
        backgroundColor2 = "#f3eaec";
        movingBallColor = "#92c2b2";
    }

    var canvas = document.createElement('canvas');
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    var context = canvas.getContext("2d");

    var canvas2 = document.getElementById('canvas2');
    canvas2.width = window.innerWidth;
    canvas2.height = window.innerHeight;
    var context2 = canvas2.getContext("2d");

    var linearGradient = context.createLinearGradient(0,0,0,canvas.height);
    linearGradient.addColorStop(0.0, backgroundColor1);
    linearGradient.addColorStop(1.0, backgroundColor2);
    context.fillStyle = linearGradient;
    context.fillRect(0, 0, canvas.width, canvas.height);

    for (var i = 0; i < 150; i++){
        drawCircle(context);
    }

    for (var j = 0; j < numBalls; j++){
        var ball = { x: Math.random() * canvas.width, y: Math.random() * canvas.height, r: Math.random() * 6 + 3, vx: Math.pow(-1, Math.ceil(Math.random()*1000)), vy: Math.pow(-1, Math.ceil(Math.random()*1000))};
        balls.push(ball);
    }

    setInterval(
        function () {
            render(context2);
            update()
        },
        100
    );

    document.body.style.background = 'url(' + canvas.toDataURL() + ')';
};

function drawCircle(context){
    context.fillStyle = backgroundBallColor;
    context.beginPath();
    context.arc(Math.random() * window.innerWidth, Math.random() * window.innerHeight, Math.random()*5 + 1, 0, 2*Math.PI, false);
    context.closePath();
    context.fill();
}

function update(){
    for (var i = 0; i < balls.length; i++){
        balls[i].x += balls[i].vx;
        balls[i].y += balls[i].vy;
        if (balls[i].x <= 0 + balls[i].r){
            balls[i].x = 0 + balls[i].r;
            balls[i].vx = -balls[i].vx;
        }
        if (balls[i].x >= window.innerWidth - balls[i].r){
            balls[i].x = window.innerWidth - balls[i].r;
            balls[i].vx = -balls[i].vx;
        }
        if (balls[i].y <= 0 + balls[i].r){
            balls[i].y = 0 + balls[i].r;
            balls[i].vy = -balls[i].vy;
        }
        if (balls[i].y >= window.innerHeight - balls[i].r){
            balls[i].y = window.innerHeight - balls[i].r;
            balls[i].vy = -balls[i].vy;
        }
    }
}

function render(context) {
    context.clearRect(0, 0, window.innerWidth, window.innerHeight);
    for( var i = 0; i < balls.length; i++ ){
        context.fillStyle = movingBallColor;
        context.beginPath();
        context.arc(balls[i].x, balls[i].y, balls[i].r, 0, 2*Math.PI, false);
        context.closePath();
        context.fill();
    }
}