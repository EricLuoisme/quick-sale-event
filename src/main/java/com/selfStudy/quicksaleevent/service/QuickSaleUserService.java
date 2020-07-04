package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.QuickSaleUserDao;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.exception.GlobalException;
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

    public boolean login(LoginVo loginVo) {
        if (loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        // 1. check mobile exists in database
        QuickSaleUser quickSaleUser = getById(Long.parseLong(loginVo.getMobile()));
        if (quickSaleUser == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        // 2. validate password (the second MD5)
        String dbRealPass = quickSaleUser.getPassword();
        String dbSalt = quickSaleUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(loginVo.getPassword(), dbSalt);
        if (!calcPass.equals(dbRealPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        return true;
    }
}
