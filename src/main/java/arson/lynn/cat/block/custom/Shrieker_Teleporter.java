package arson.lynn.cat.block.custom;

import net.minecraft.block.Block;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;

public class Shrieker_Teleporter extends Block {
    public Shrieker_Teleporter(Block.Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState()
        );
    }
}
