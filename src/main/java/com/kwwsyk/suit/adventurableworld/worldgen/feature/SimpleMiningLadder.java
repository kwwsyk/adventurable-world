package com.kwwsyk.suit.adventurableworld.worldgen.feature;

import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MiningLadderConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class SimpleMiningLadder extends Feature<MiningLadderConfiguration> {

    Class ladderClass = LadderBlock.class;

    public SimpleMiningLadder(Codec<MiningLadderConfiguration> codec) {
        super(codec);
    }

    /**
     * Places the given feature at the given location.
     * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated, that they can safely generate into.
     *
     * @param context A context object with a reference to the level and the position
     *                the feature is being placed at
     */
    @Override
    public boolean place(FeaturePlaceContext<MiningLadderConfiguration> context) {
        final WorldGenLevel level = context.level();
        final BlockPos blockPos = context.origin();
        final MiningLadderConfiguration config = context.config();
        final int topY = blockPos.getY();

        if(level.isEmptyBlock(blockPos)
            && level.isEmptyBlock(blockPos.atY(topY+1))
            && level.isEmptyBlock(blockPos.atY(topY+2))
        ){
            for(Direction direction : Direction.values()){//the direction is the facing of climbable
                if(direction == Direction.DOWN || direction == Direction.UP) continue;

                final BlockPos topPlatformPos = blockPos.relative(direction.getOpposite());
                final BlockState topPlatformBlock = level.getBlockState(topPlatformPos);
                if(!topPlatformBlock.isFaceSturdy(level,topPlatformPos,direction)
                    || !level.isEmptyBlock(topPlatformPos.atY(topY+2))) continue;

                final List<BlockPos> toPlace = new ArrayList<>();
                //add a ladder to let top of ladder 'grab' the edge of a cliff.
                if(!level.isEmptyBlock(topPlatformPos.atY(topY+1))) toPlace.add(blockPos.atY(topY+1));

                int ladderY;
                boolean isHanging = false;
                for(ladderY = topY; level.isEmptyBlock(blockPos.atY(ladderY)); ladderY--){
                    toPlace.add(blockPos.atY(ladderY));
                    if(level.getBlockState(topPlatformPos.atY(ladderY))
                            .isFaceSturdy(level,
                                    topPlatformPos.atY(ladderY),
                                    direction.getOpposite())){
                        isHanging = true;
                    }
                }
                if(isHanging && !config.hangable()) continue;

                BlockPos basePos = blockPos.atY(ladderY-1);
                BlockState basePlatform = level.getBlockState(basePos);
                if(toPlace.size() < config.minLength() || toPlace.size() > config.maxLength()) continue;
                if(basePlatform.isFaceSturdy(level,basePos,Direction.UP) || isWalkable(basePlatform,basePos,level,direction)){
                    //doPlace
                    toPlace.forEach(//place ladder blocks in toPlace pos. ------------------------------- //set flags param to 0 so that block update will not trigger
                            pos-> level.setBlock(pos,Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING,direction),0)
                    );
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWalkable(BlockState state, BlockPos pos, BlockGetter level, Direction ladderDir){
        VoxelShape shape = state.getCollisionShape(level, pos);
        if (!shape.isEmpty()) {
            double maxY = shape.max(Direction.Axis.Y);
            return maxY <= 1.25;
        }
        return false;
    }


}
