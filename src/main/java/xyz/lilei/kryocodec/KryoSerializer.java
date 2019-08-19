package xyz.lilei.kryocodec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName KryoDecoder
 * @Description TODO 反序列化/序列化器
 * @Author lilei
 * @Date 19/08/2019 07:24
 * @Version 1.0
 **/
public class KryoSerializer {
    private static Kryo kryo = KryoFactory.createKryo();

    // 序列化器
    public static void serialize(Object object, ByteBuf out){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, object);
        output.flush();
        output.close();

        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.writeBytes(b);
    }

    // 反序列化器
    public static Object deserialize(ByteBuf out){
        if (out == null)return null;

        Input input = new Input(new ByteBufInputStream(out));
        return kryo.readClassAndObject(input);
    }
}
