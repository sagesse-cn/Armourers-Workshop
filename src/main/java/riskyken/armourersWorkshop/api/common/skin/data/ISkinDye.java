package riskyken.armourersWorkshop.api.common.skin.data;

import io.netty.buffer.ByteBuf;

public interface ISkinDye {
    
    public byte[] getDyeColour(int index);
    
    public boolean haveDyeInSlot(int index);
    
    public void addDye(byte[] rgbt);
    
    public void addDye(int index, byte[] rgbt);
    
    public void removeDye(int index);
    
    public int getNumberOfDyes();
    
    public void writeToBuf(ByteBuf buf);
    
    public void readFromBuf(ByteBuf buf);
}
