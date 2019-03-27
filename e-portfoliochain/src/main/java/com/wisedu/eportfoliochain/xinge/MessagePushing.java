package com.wisedu.eportfoliochain.xinge;

import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.TagTokenPair;

public class MessagePushing {
    public static void main(String[] args) {
        //System.out.println(XingeApp.pushTokenAndroid(300, "secretKey", "test", "测试", "token"));
        System.out.println(XingeApp.pushTokenAndroid(300, "secretKey", "授权申请", "南京大学请求查看您的本科毕业证书！", "token"));
    }

}