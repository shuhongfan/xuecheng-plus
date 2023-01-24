var  getCookie = function (name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg))
        return (arr[2]);
    else
        return null;
}
function setCookie(name,value,Days){
    if(Days==0){
        document.cookie = name + "="+ escape (value) + ";domain=xuecheng-plus.com;expires=0;path=/" ;
    }else{
        var exp = new Date();
        exp.setTime(exp.getTime() + Days*24*60*60*1000);
        document.cookie = name + "="+ escape (value) + ";domain=xuecheng-plus.com;expires=" + exp.toGMTString()+";path=/";
    }
   
    
}
var getUserSession =function (key) {
    return sessionStorage.getItem(key);
}
var setUserSession =function (key, value) {
    return sessionStorage.setItem(key, value);
}
var delUserSession =function (key) {
    return sessionStorage.removeItem(key)
}
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r != null) return decodeURI(r[2]);
    return null;
}
// var getActiveUser = function(){
//     let uid = getCookie("uid")
//     if(uid){
//         let activeUserStr = getUserSession("activeUser");
//         if(activeUserStr){
//             return JSON.parse(activeUserStr);
//         }
//         return ;
//     }else{
//         delUserSession("activeUser")
//     }
// }
var getActiveUser = function(){
    let jwt = getCookie("jwt")
    if(jwt){
        let activeUser = getUserInfoFromJwt(jwt)
        return activeUser;
    }else{
        return ;
    }
 }

//获取jwt令牌
var getJwt = function(){
    return getCookie("jwt")
}
// var getJwt = function(){
//     let activeUser = getActiveUser()
//     if(activeUser){
//         return activeUser.jwt
//     }
// }
//解析jwt令牌，获取用户信息
var getUserInfoFromJwt = function (jwt) {
    if(!jwt){
        return ;
    }
    try{
        var jwtDecodeVal = jwt_decode(jwt);
        jwtDecodeVal=JSON.parse(jwtDecodeVal.user_name);
       
        if (!jwtDecodeVal) {
            return ;
        }
        var activeUser={}
        //console.log(jwtDecodeVal)
        activeUser.userid = jwtDecodeVal.id 
        activeUser.username = jwtDecodeVal.username 
        activeUser.name = jwtDecodeVal.name 
        activeUser.nickname = jwtDecodeVal.nickname 
        activeUser.userpic = jwtDecodeVal.userpic 
        return activeUser;
    }catch(e){
      
    }
    return ;
    
   
}
// var getUserInfoFromJwt = function (jwt) {
//     if(!jwt){
//         return ;
//     }
//     var jwtDecodeVal = jwt_decode(jwt);
//     if (!jwtDecodeVal) {
//         return ;
//     }
//     let activeUser={}
//     //console.log(jwtDecodeVal)
//     activeUser.utype = jwtDecodeVal.utype || '';
//     activeUser.username = jwtDecodeVal.name || '';
//     activeUser.userpic = jwtDecodeVal.userpic || '';
//     activeUser.userid = jwtDecodeVal.userid || '';
//     activeUser.authorities = jwtDecodeVal.authorities || '';
//     activeUser.uid = jwtDecodeVal.jti || '';
//     activeUser.jwt = jwt;
//     return activeUser;
// }
var  checkActiveUser2=  function(){
    // var jwt = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MjEyNjMzNjUsInVzZXJfbmFtZSI6IjEyMyIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iLCJnZXRSZXNvdXJjZSJdLCJqdGkiOiI3NmIxOTgzMi01MDk3LTQyMDMtYjhjMS1kOGI1N2ZmMmZhOTAiLCJjbGllbnRfaWQiOiJtYW5hZ2VyIiwic2NvcGUiOlsibWFuYWdlciJdfQ.MzycLCC9cR-905ilrd1bWH52nqto4MDYbbMSXgcRdVkUGlv2A2JrlIvbvxNc2BVug1L59AV_7hUa_SHZQgrOdHnyoMdcu5KoHHXsJi1O5wUXkuahc-K3KoBhwkyWY9O-DvwZhrmzsYN2gb_3qmU2xbHu6U1pwwscXGHjbKJDoWGdrmMkRc1cpxuqvH-0eusR1GLQ4gTBSyVNW4XVO7jMt9ATBubos7GhtbAMXnCQVO9pui2zvPvKVxlvwMjJowjdCc_5hvXjyUvWgbU1qUrdtXeskeT-HoVUtsol6OnFHnq7o9bIin1493ZwjDck_0R1R8mkGRGKylQtZdzESeQpYA';
    var jwt_base64 = getCookie("juid");
    if (jwt_base64 ) {
        let jwt = Base64.decode(jwt_base64)
        var jwtDecodeVal = jwt_decode(jwt);
        //console.log(jwtDecodeVal);
//    var user = sessionStorage.getItem('user');
        if (jwtDecodeVal) {

//      user = JSON.parse(user);
            let activeUser={}
            //console.log(jwtDecodeVal)
            activeUser.utype = jwtDecodeVal.utype || '';
            activeUser.username = jwtDecodeVal.user_name || '';
            activeUser.userpic = jwtDecodeVal.userpic || '';
            activeUser.userid = jwtDecodeVal.userid || '';
            activeUser.authorities = jwtDecodeVal.authorities || '';
            activeUser.menus = jwtDecodeVal.menus || '';

            setUserSession("activeUser",JSON.stringify(activeUser))
            return getUserSession("activeUser")
        }
    }
    return null;
}

function logout(){
    setCookie('jwt','',-1)
    window.location='/'
}

function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";
 
    var uuid = s.join("");
    return uuid;
}

function showlogin(){
    window.location = "http://www.xuecheng-plus.com/sign.html?returnUrl="+ Base64.encode(window.location)
}