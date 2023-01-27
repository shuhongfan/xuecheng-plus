package com.shf.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.base.utils.IdWorkerUtils;
import com.shf.xuecheng.base.utils.QRCodeUtil;
import com.shf.xuecheng.messagesdk.service.MqMessageService;
import com.shf.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.shf.xuecheng.orders.mapper.XcOrdersMapper;
import com.shf.xuecheng.orders.mapper.XcPayRecordMapper;
import com.shf.xuecheng.orders.model.dto.AddOrderDto;
import com.shf.xuecheng.orders.model.dto.PayRecordDto;
import com.shf.xuecheng.orders.model.dto.PayStatusDto;
import com.shf.xuecheng.orders.model.po.XcOrders;
import com.shf.xuecheng.orders.model.po.XcOrdersGoods;
import com.shf.xuecheng.orders.model.po.XcPayRecord;
import com.shf.xuecheng.orders.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务接口
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private XcOrdersMapper ordersMapper;

    @Autowired
    private XcOrdersGoodsMapper ordersGoodsMapper;

    @Autowired
    private XcPayRecordMapper payRecordMapper;

    @Autowired
    private MqMessageService mqMessageService;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;

    /**
     * @description 创建商品订单
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付交易记录(包括二维码)
     * @author Mr.M
     * @date 2022/10/4 11:02
     */
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
//        创建商品订单
        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);

//        添加支付记录
        XcPayRecord payRecord = createPayRecord(xcOrders);

//        生成支付二维码
        String qrCode = null;
        try {
            //url要可以被模拟器访问到，url为下单接口(稍后定义)
            qrCode = new QRCodeUtil().createQRCode("http://192.168.101.1/api/orders/requestpay?payNo="+payRecord.getPayNo(), 200, 200);
        } catch (IOException e) {
            XueChengPlusException.cast("生成二维码出错");
        }

//        封装需要返回的数据
        //封装要返回的数据
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        //支付二维码
        payRecordDto.setQrcode(qrCode);

        return payRecordDto;
    }

    /**
     * 添加支付记录
     * @param orders
     * @return
     */
    public XcPayRecord createPayRecord(XcOrders orders){
        XcPayRecord payRecord = new XcPayRecord();
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);//支付记录交易号
        //记录关键订单id
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001");//未支付
        payRecord.setUserId(orders.getUserId());
        payRecordMapper.insert(payRecord);
        return payRecord;
    }

    /**
     * 创建商品订单
     * @param userId
     * @param addOrderDto
     * @return
     */
    @Transactional
    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto) {
        //选课记录id
        String outBusinessId = addOrderDto.getOutBusinessId();
        //对订单插入进行幂等性处理
        //根据选课记录id从数据库查询订单信息
        XcOrders orders = getOrderByBusinessId(outBusinessId);
        if(orders!=null){
            return orders;
        }

        //添加订单
        orders = new XcOrders();
        long orderId = IdWorkerUtils.getInstance().nextId();//订单号
        orders.setId(orderId);
        orders.setTotalPrice(addOrderDto.getTotalPrice());
        orders.setCreateDate(LocalDateTime.now());
        orders.setStatus("600001");//未支付
        orders.setUserId(userId);
        orders.setOrderType(addOrderDto.getOrderType());
        orders.setOrderName(addOrderDto.getOrderName());
        orders.setOrderDetail(addOrderDto.getOrderDetail());
        orders.setOrderDescrip(addOrderDto.getOrderDescrip());
        orders.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        ordersMapper.insert(orders);
        //插入订单明细表
        String orderDetailJson = addOrderDto.getOrderDetail();
        //将json转成List
        List<XcOrdersGoods> xcOrdersGoods = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        //将明细List插入数据库
        xcOrdersGoods.forEach(ordersGods->{
            //在明细中记录订单号
            ordersGods.setOrderId(orderId);
            ordersGoodsMapper.insert(ordersGods);
        });

        return orders;
    }

    /**
     * 根据业务id查询订单
     * @param outBusinessId
     * @return
     */
    public XcOrders getOrderByBusinessId(String outBusinessId) {
        LambdaQueryWrapper<XcOrders> wrapper = new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, outBusinessId);
        XcOrders orders = ordersMapper.selectOne(wrapper);
        return orders;
    }

    /**
     * @description 查询支付交易记录
     * @param payNo  交易记录号
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @author Mr.M
     * @date 2022/10/20 23:38
     */
    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        return null;
    }

    /**
     * @description 保存支付宝支付结果
     * @param payStatusDto  支付结果信息
     * @return void
     * @author Mr.M
     * @date 2022/10/4 16:52
     */
    @Override
    public void saveAliPayStatus(PayStatusDto payStatusDto) {

    }
}
