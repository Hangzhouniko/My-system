package com.cqq.reggie.common;


/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户ID
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置ID
     * @param id
     */
    public void setCurrentId(Long id){
        threadLocal.set(id);
    }


    /**
     * 获取ID
     * @return
     */
    public Long getCurrentId(){
        return threadLocal.get();
    }

}
