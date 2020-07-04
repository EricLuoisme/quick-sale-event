package com.selfStudy.quicksaleevent.utils.EncoderTest;

import com.selfStudy.quicksaleevent.utils.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MD5Test {

    @Test
    public void encodeInput_thenReturnTrue() {
        String plainTest = "123456";
        String afterFirstEncoding = "d3b1294a61a07da9b49b6e22b2cbd7f9";
        assertEquals(afterFirstEncoding, MD5Util.inputPassToFormPass(plainTest));
    }
}
