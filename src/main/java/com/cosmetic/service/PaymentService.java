package com.cosmetic.service;


import java.util.List;

import com.cosmetic.entity.ExpressCheckout;
import com.cosmetic.entity.OrderHistory;
import com.cosmetic.entity.OrderPaypal;


public interface PaymentService {
    public int insertExpressCheckout(ExpressCheckout expressCheckout);
    public ExpressCheckout selectExpressCheckoutByUid(int user_id);
    public int updatePayerIdByUid(ExpressCheckout expressCheckout);
    public int deleteExpressCheckoutByUid(int user_id);
    public int countExpressCheckoutByUid(int user_id);
    public int insertOrderHistory(OrderHistory orderHistory);
    public OrderHistory selectOrderHistoryByChargeId(int charge_id);
    public List<OrderHistory> selectOrderHistoryByUserId(int user_id);
    public int insertOrderPaypal(OrderPaypal orderPaypal);
    public OrderPaypal selectOrderPaypalByOrderSn(String order_sn);
}
