package xyz.lilei.kryocodec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xyz.lilei.vo.MyHeader;
import xyz.lilei.vo.MyMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestKryoCodeC
 * @Description TODO 测试序列化的准确性
 * @Author lilei
 * @Date 19/08/2019 07:25
 * @Version 1.0
 **/
public class TestKryoCodeC {
    public MyMessage getMessage() {
        MyMessage myMessage = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setLength(123);
        myHeader.setSessionID(99999);
        myHeader.setType((byte) 1);
        myHeader.setPriority((byte) 7);
        Map<String, Object> attachment = new HashMap<String, Object>();
        for (int i = 0; i < 10; i++) {
            attachment.put("ciyt --> " + i, "lilinfeng " + i);
        }
        myHeader.setAttachment(attachment);
        myMessage.setMyHeader(myHeader);
        myMessage.setBody("abcdefg-----------------------AAAAAA");
        return myMessage;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        TestKryoCodeC testC = new TestKryoCodeC();

        for (int i = 0; i < 5; i++) {
            ByteBuf sendBuf = Unpooled.buffer();
            MyMessage message = testC.getMessage();
            System.out.println("Encode:"+message + "[body ] " + message.getBody());
            KryoSerializer.serialize(message, sendBuf);
            MyMessage decodeMsg = (MyMessage)KryoSerializer.deserialize(sendBuf);
            System.out.println("Decode:"+decodeMsg + "<body > "
                    + decodeMsg.getBody());
            System.out
                    .println("-------------------------------------------------");
        }

    }
}
