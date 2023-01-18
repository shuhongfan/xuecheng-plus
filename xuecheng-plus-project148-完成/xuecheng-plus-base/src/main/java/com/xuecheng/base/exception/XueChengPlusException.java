package com.xuecheng.base.exception;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/10/10 9:14
 */
public class XueChengPlusException extends RuntimeException {

    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String message) {
        super(message);
        this.errMessage = message;
    }

    public String getErrMessage(){
        return errMessage;
    }

    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
}
