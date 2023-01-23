
/*创建订单*/
const createOrder = courseId => {
     let params = {
         courseId:courseId
     }
    return requestPost('/api/order/create',params);
}
/*查询课程*/
const queryCourse = courseId => {

    return requestGet('/openapi/portalview/course/get/'+courseId);
}
/*查询公司的统计信息*/
const queryCompanyStat = (companyId) => {
    return requestGet('/stat/company/company_stat_'+companyId+'.json');
}
/*查询课程的统计信息*/
const queryCourseStat = (courseId) => {
    return requestGet('/stat/course/course_stat_'+courseId+'.json');
}
/*查询 选课状态*/
const queryLearnstatus = courseId => {

    return requestPost('/api/learning/choosecourse/learnstatus/'+courseId);
}
/*添加选课*/
const addChoosecourse = courseId => {
    return requestPost('/api/learning/choosecourse/'+courseId);
}
/*生成支付二维码*/
const generatepaycode = (params) => {
    return requestPost('/api/orders/generatepaycode',params);
}
