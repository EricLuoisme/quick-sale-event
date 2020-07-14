package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.QuickSaleKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.utils.MD5Util;
import com.selfStudy.quicksaleevent.utils.UUIDUtil;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import com.selfStudy.quicksaleevent.web.controller.QuicksaleController;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@SuppressWarnings("restriction")
@Service
public class QuickSaleService {

    GoodsService goodsService;

    OrderService orderService;

    RedisService redisService;

    public QuickSaleService(GoodsService goodsService, OrderService orderService, RedisService redisService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;
    }


    @Transactional
    public OrderInfo doQuicksale(QuickSaleUser user, GoodsVo goods) {
        /**
         * simulating the purchase operation
         */
        boolean successOrNot = goodsService.reduceStock(goods); // reduce the stock
        if (successOrNot) {
            return orderService.createOrder(user, goods); // create a order
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getQuicksaleResult(Long userId, long goodsId) {
        /**
         * return purchase result
         */
        QuickSaleOrder order = orderService.getQuickSaleOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) { // successfully purchased goods
            return order.getOrderId();
        } else {
            boolean outOfStock = getGoodsOver(goodsId);
            if (!outOfStock)
                return 0; // still need to wait the result
            else {
                // if the stock is over, but the order might still inside the rabbitmq
                // then we can check total number of order and this good's number to judge whether this user
                // successfully purchased or not
                List<QuickSaleOrder> orders = orderService.getAllQuicksaleOrdersByGoodsId(goodsId);
                if(orders == null || orders.size() < QuicksaleController.getGoodsStockOriginal(goodsId)){
                    return 0; // still need to wait
                }else { // or else, try to find this user's order
                    QuickSaleOrder o = get(orders, userId);
                    if(o != null) {
                        return o.getOrderId(); // successfully purchased
                    }else {
                        return -1; // bad luck
                    }
                }
            }
        }
    }

    private QuickSaleOrder get(List<QuickSaleOrder> orders, Long userId) {
        /**
         * get order by user's id
         */
        if(orders == null || orders.size() <= 0) {
            return null;
        }
        for(QuickSaleOrder order : orders) {
            if(order.getUserId().equals(userId)) {
                return order;
            }
        }
        return null;
    }

    private void setGoodsOver(Long goodsId) {
        /**
         * if good is out of stock, we set an unexpired key in Redis to represent it
         */
        redisService.set(QuickSaleKey.isOutOfStock, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        /**
         * check whether the good is out of stock, by checking whether it's exist in Redis
         */
        return redisService.exist(QuickSaleKey.isOutOfStock, "" + goodsId);
    }

    public String createQuicksalePath(QuickSaleUser user, long goodsId) {
        /**
         * for setting path
         */
        if (user == null || goodsId <= 0)
            return null;
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(QuickSaleKey.getQuicksalePath, "" + user.getId() + "_" + goodsId, str);
        return str;
    }

    public boolean checkPath(QuickSaleUser user, long goodsId, String path) {
        /**
         * for request path validation
         */
        if (user == null || path == null)
            return false;
        String pathGet = redisService.get(QuickSaleKey.getQuicksalePath, "" + user.getId() + "_" + goodsId, String.class);
        return pathGet.equals(path);
    }

    public BufferedImage createVerifyCode(QuickSaleUser user, long goodsId) {
        if (user == null || goodsId <= 0)
            return null;

        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyEquation(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        // cache verify code into Redis
        int rnd = calc(verifyCode);
        redisService.set(QuickSaleKey.getQuicksaleVerifyCode, user.getId() + "," + goodsId, rnd);
        // output image
        return image;
    }

    private static int calc(String exp) {
        /**
         * calculate the equation by engine
         */
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+', '-', '*'};

    /**
     * + - * (no /)
     */
    private String generateVerifyEquation(Random rdm) {
        /**
         * for generating verify equation of three integer num
         */
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(QuickSaleUser user, long goodsId, int verifyCode) {
        /**
         * validate the verify code
         */
        if (user == null || goodsId <= 0)
            return false;
        Integer code = redisService.get(QuickSaleKey.getQuicksaleVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (code == null || code - verifyCode != 0) {
            return false;
        } else {
            // delete this cache to prevent user use same verify code
            redisService.delete(QuickSaleKey.getQuicksaleVerifyCode, user.getId() + "," + goodsId);
            return true;
        }
    }

    public void reset(List<GoodsVo> goodsList) {
        /**
         * for quicker reset order info on pressure test
         */
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }
}
