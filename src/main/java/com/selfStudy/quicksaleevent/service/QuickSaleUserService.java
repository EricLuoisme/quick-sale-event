package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.QuickSaleUserDao;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.utils.MD5Util;
import com.selfStudy.quicksaleevent.vo.LoginVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import org.springframework.stereotype.Service;

@Service
public class QuickSaleUserService {

    QuickSaleUserDao quickSaleUserDao; //injected by constructor

    public QuickSaleUserService(QuickSaleUserDao quickSaleUserDao) {
        this.quickSaleUserDao = quickSaleUserDao;
    }

    public QuickSaleUser getById(long id) {
        return quickSaleUserDao.getById(id);
    }

    public CodeMsg login(LoginVo loginVo) {
        if (loginVo == null)
            return CodeMsg.SERVER_ERROR;

        // do first validation
        String firstEncode = MD5Util.inputPassToFormPass(loginVo.getPassword());
        loginVo.setPassword(firstEncode);

        // 1. check mobile exists in database
        QuickSaleUser quickSaleUser = getById(Long.parseLong(loginVo.getMobile()));
        if (quickSaleUser == null)
            return CodeMsg.MOBILE_NOT_EXIST;
        // 2. validate password (the second MD5)
        String dbRealPass = quickSaleUser.getPassword();
        String dbSalt = quickSaleUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(loginVo.getPassword(), dbSalt);
        if (!calcPass.equals(dbRealPass))
            return CodeMsg.PASSWORD_ERROR;

        return CodeMsg.SUCCESS;
    }
}
