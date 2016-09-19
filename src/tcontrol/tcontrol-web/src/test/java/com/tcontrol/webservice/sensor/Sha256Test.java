package com.tcontrol.webservice.sensor;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Test;

public class Sha256Test {
    
    public Sha256Test() {
    }
    
    @Test
    public void test(){
        Sha256Hash sha256Hash = new Sha256Hash("123456");
        System.out.println(sha256Hash.toHex());
    }
    
}
