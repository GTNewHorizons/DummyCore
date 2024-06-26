package DummyCore.Utils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class DummyPacketIMSG implements IMessage {

    public String dataStr;

    public DummyPacketIMSG() {}

    public DummyPacketIMSG(String data) {
        dataStr = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dataStr = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, dataStr);
    }
}
