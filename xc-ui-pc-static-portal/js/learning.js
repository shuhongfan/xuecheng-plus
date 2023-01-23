var url = "/open";
const requestGetCourseInfo = (courseId) => {
    return  requestGet(url+"/content/course/whole/"+courseId,{});
}
const requestGetMeidaInfo = (mediaId,teachplanId,courseId) => {
    if(url=="/open"){
        return  requestGet(url+"/media/preview/"+mediaId,{});
    }else{
        return  requestGet(url+"/learning/open/learn/getvideo/"+courseId+"/"+teachplanId+"/"+mediaId,{});
    }
    
}
var location_url = String(window.location);
if(location_url.indexOf("/preview/")<0){
    url = "/api"
}