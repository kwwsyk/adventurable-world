package com.kwwsyk.suit.adventurableworld.worldgen.feature;

import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MineLadderConfig;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntPredicate;

import static com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MineLadderConfig.LengthInclude.*;

public class SimpleMiningLadder extends Feature<MineLadderConfig> {

    /// @see LadderBlock

    public SimpleMiningLadder(Codec<MineLadderConfig> codec) {
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
    public boolean place(FeaturePlaceContext<MineLadderConfig> context) {
        final WorldGenLevel level = context.level();
        final BlockPos startPos = context.origin();
        final int startY = startPos.getY();
        final MineLadderConfig config = context.config();
        int topY = startY;

        if(level.isEmptyBlock(startPos)
            && level.isEmptyBlock(startPos.atY(topY+1))
            && level.isEmptyBlock(startPos.atY(topY+2))
        ){
            for(Direction direction : Direction.values()){//the direction is the facing of climbable
                if(direction == Direction.DOWN || direction == Direction.UP) continue;

                final BlockPos originRely = startPos.relative(direction.getOpposite());

                final BlockPos.MutableBlockPos ladderPos = new BlockPos.MutableBlockPos(startPos.getX(),topY,startPos.getZ());
                final BlockPos.MutableBlockPos top = new BlockPos.MutableBlockPos(originRely.getX(),topY,originRely.getZ());
                final IntPredicate noBlockingOnTop = y -> level.isEmptyBlock(top.relative(Direction.UP,y));
                final BooleanSupplier isTopWalkable = ()-> (noBlockingOnTop.test(1) || noBlockingOnTop.test(3)) && noBlockingOnTop.test(2);

                //Climb up
                if(config.climbable()){
                    boolean blocked = false;
                    while (!isTopWalkable.getAsBoolean()){
                        ladderPos.move(0,1,0);
                        if(!level.isEmptyBlock(ladderPos) || //and the condition the ladder hang on
                                !config.hangable() && noBlockingOnTop.test(1)){
                            blocked = true;
                            ladderPos.move(0,-1,0);//optional,,
                            break;
                        }
                        top.move(0,1,0);
                        topY++;
                        if(topY>level.getMaxBuildHeight()) throw new IllegalStateException("The ladder has climbed too high!");
                    }
                    if(blocked || !level.isEmptyBlock(ladderPos.atY(topY+1)) || !level.isEmptyBlock(ladderPos.atY(topY+2))) continue;
                    if(config.lengthInclude()==CLIMB && (topY-startY>config.maxLength() || topY-startY<config.minLength())) continue;
                }
                if(!isTopWalkable.getAsBoolean()) continue;

                final BlockState topPlatformBlock = level.getBlockState(top);
                if(!topPlatformBlock.isFaceSturdy(level,top,direction)) continue;

                int len = 0;

                // 1) 预扫长度 + 支撑判定
                while ((config.lengthInclude()==BOTH ? len <= config.maxLength() : (//len means CLIMB + DROP = BOTH length
                        config.lengthInclude() != DROP || startY - ladderPos.getY() <= config.maxLength()
                        ))//Priority of alg
                        && level.isEmptyBlock(ladderPos)) {
                    ladderPos.move(0, -1, 0);
                    len++;
                }
                if (len < config.minLength()) continue;//DROP <= BOTH = len
                // baseY 为第一块非空气
                int baseY = ladderPos.getY();
                BlockPos basePos = startPos.atY(baseY);
                BlockState basePlatform = level.getBlockState(basePos);

                // 2) 基座判定
                boolean okBase = basePlatform.isFaceSturdy(level, basePos, Direction.UP) || isWalkable(basePlatform, basePos, level, direction);
                if (!okBase) continue;

                // 3) 侧面支撑扫描（任一高度有支撑即非悬挂）//only scan startY->baseY as startY->topY has been checked
                BlockPos.MutableBlockPos behindPos = new BlockPos.MutableBlockPos(startPos.getX(),startY,startPos.getZ());
                boolean isHanging = false;
                for(int y=startY; y> baseY; y--){
                    behindPos.move(0,-1,0);
                    if(!level.getBlockState(behindPos).isFaceSturdy(level,behindPos,direction)) isHanging = true;
                }
                if(isHanging && !config.hangable()) continue;

                // 4) 生成放置列表
                final List<BlockPos> toPlace = new ArrayList<>(len);
                for (int y = topY; y >= baseY + 1; y--) toPlace.add(new BlockPos(startPos.getX(), y, startPos.getZ()));

                for (BlockPos p : toPlace) {//set ladders, the flag param is set to 0 so that block update will not be triggered.
                    level.setBlock(p, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, direction), 0);
                }
                return true;
            }
        }
        return false;
    }

    /**Check whether player can climb the ladder from the base platform
     * @param state of base platform block
     * @param pos of base platform block
     * @param level world level
     * @param ladderDir the direction of the ladder(facing). Reserver this param for future optimistic.
     * @return true for player can walk on
     */
    private boolean isWalkable(BlockState state, BlockPos pos, BlockGetter level, Direction ladderDir){
        if (state.isAir() || !state.getFluidState().isEmpty()) return false;
        if (state.isFaceSturdy(level, pos, Direction.UP)) return true;
        double h = state.getCollisionShape(level, pos).max(Direction.Axis.Y);
        return h >= 0.5 && h <= 1.25; // 允许半砖~满砖的平台
    }
}
