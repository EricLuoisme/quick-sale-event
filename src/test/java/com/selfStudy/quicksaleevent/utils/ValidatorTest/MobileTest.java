package com.selfStudy.quicksaleevent.utils.ValidatorTest;

import com.selfStudy.quicksaleevent.utils.ValidatorUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MobileTest {

    @Test
    public void whenInputMobileNumLessThen11_thenFalse() {
        String invalidMobileLess = "123";
        String invalidMobileMore = "123456789112";
        String validMobile = "18814129241";
        assertFalse(ValidatorUtil.isMobile(invalidMobileLess));
        assertFalse(ValidatorUtil.isMobile(invalidMobileMore));
        assertTrue(ValidatorUtil.isMobile(validMobile));
    }
}
