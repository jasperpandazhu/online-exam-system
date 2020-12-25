function getCookie(cookieName) {
    var name = cookieName + "=";
    var cookieArr = document.cookie.split(';');
    for( var i=0; i < cookieArr.length; i++ ) {
        var cookie = cookieArr[i];
        while ( cookie.charAt(0)==' ' ) {
            cookie = cookie.substring(1);
        }
        if ( cookie.indexOf(name) == 0 ) {
            return cookie.substring(name.length,cookie.length).replace(/\+/g, " ").replace(/%3A/g, ":");
        }
    }
    return "";
}

function setCookie(name,value,days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function deleteCookie(name) {
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}