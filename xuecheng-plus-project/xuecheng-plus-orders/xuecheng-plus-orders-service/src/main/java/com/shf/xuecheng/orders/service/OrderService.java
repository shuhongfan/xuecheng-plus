package com.shf.xuecheng.orders.service;

import com.shf.xuecheng.orders.model.dto.AddOrderDto;
import com.shf.xuecheng.orders.model.dto.PayRecordDto;
import com.shf.xuecheng.orders.model.dto.PayStatusDto;
import com.shf.xuecheng.orders.model.po.XcPayRecord;

/**
 * 订单服务接口
 */
public interface OrderService {
    /**
     * @description 创建商品订单
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付交易记录(包括二维码)
     * @author Mr.M
     * @date 2022/10/4 11:02
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @description 查询支付交易记录
     * @param payNo  交易记录号
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @author Mr.M
     * @date 2022/10/20 23:38
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * @description 保存支付宝支付结果
     * @param payStatusDto  支付结果信息
     * @return void
     * @author Mr.M
     * @date 2022/10/4 16:52
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto);
}
