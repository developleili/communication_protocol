package xyz.lilei.vo;

/**
 * @ClassName MyMessage
 * @Description TODO
 * @Author lilei
 * @Date 19/08/2019 07:28
 * @Version 1.0
 **/
public class MyMessage  {

    private MyHeader myHeader;

    private Object body;

    public final MyHeader getMyHeader() {
        return myHeader;
    }

    public final void setMyHeader(MyHeader myHeader) {
        this.myHeader = myHeader;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MyMessage [myHeader=" + myHeader + "][body="+body+"]";
    }
}
