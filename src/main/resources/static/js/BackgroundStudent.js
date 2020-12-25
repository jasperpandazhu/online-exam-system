var backgroundColor1 = "#f39095";
var backgroundColor2 = "#f3eaec";
var backgroundBallColor = "#fff";

window.onload = function(){

    var canvas = document.createElement('canvas');
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    var context = canvas.getContext("2d");

    var linearGradient = context.createLinearGradient(0,0,0,canvas.height);
    linearGradient.addColorStop(0.0, backgroundColor1);
    linearGradient.addColorStop(1.0, backgroundColor2);
    context.fillStyle = linearGradient;
    context.fillRect(0, 0, canvas.width, canvas.height);

    for (var i = 0; i < 150; i++){
        drawCircle(context);
    }

    document.body.style.background = 'url(' + canvas.toDataURL() + ')';
};

function drawCircle(context){
    context.fillStyle = backgroundBallColor;
    context.beginPath();
    context.arc(Math.random() * window.innerWidth, Math.random() * window.innerHeight, Math.random()*5 + 1, 0, 2*Math.PI, false);
    context.closePath();
    context.fill();
}
