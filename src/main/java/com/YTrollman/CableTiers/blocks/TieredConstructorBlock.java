package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.tileentity.TieredConstructorTileEntity;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.CableBlock;
import com.refinedmods.refinedstorage.block.shape.ShapeCache;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.CollisionUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TieredConstructorBlock extends CableBlock {

    private static final VoxelShape HEAD_NORTH = VoxelShapes.or(box(2, 2, 0, 14, 14, 2), HOLDER_NORTH);
    private static final VoxelShape HEAD_EAST = VoxelShapes.or(box(14, 2, 2, 16, 14, 14), HOLDER_EAST);
    private static final VoxelShape HEAD_SOUTH = VoxelShapes.or(box(2, 2, 14, 14, 14, 16), HOLDER_SOUTH);
    private static final VoxelShape HEAD_WEST = VoxelShapes.or(box(0, 2, 2, 2, 14, 14), HOLDER_WEST);
    private static final VoxelShape HEAD_DOWN = VoxelShapes.or(box(2, 0, 2, 14, 2, 14), HOLDER_DOWN);
    private static final VoxelShape HEAD_UP = VoxelShapes.or(box(2, 14, 2, 14, 16, 14), HOLDER_UP);

    private final CableTier tier;

    public TieredConstructorBlock(CableTier tier) {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);
        this.tier = tier;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ContentType.CONSTRUCTOR.getTileEntityType(tier).create();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return ShapeCache.getOrCreate(state, s -> {
            VoxelShape shape = getCableShape(s);
            shape = VoxelShapes.or(shape, getHeadShape(s));
            return shape;
        });
    }

    private VoxelShape getHeadShape(BlockState state) {
        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                return HEAD_NORTH;
            case EAST:
                return HEAD_EAST;
            case SOUTH:
                return HEAD_SOUTH;
            case WEST:
                return HEAD_WEST;
            case UP:
                return HEAD_UP;
            case DOWN:
                return HEAD_DOWN;
            default:
                return VoxelShapes.empty();
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide && CollisionUtils.isInBounds(getHeadShape(state), pos, hit.getLocation())) {
            return NetworkUtils.attemptModify(
                    world,
                    pos,
                    player,
                    () -> NetworkHooks.openGui(
                            (ServerPlayerEntity) player,
                            new PositionalTileContainerProvider<TieredConstructorTileEntity>(
                                    new TranslationTextComponent(getDescriptionId()),
                                    (tile, windowId, inventory, p) -> ContentType.CONSTRUCTOR.createContainer(windowId, p, tile, tier),
                                    pos
                            ),
                            pos
                    )
            );
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
