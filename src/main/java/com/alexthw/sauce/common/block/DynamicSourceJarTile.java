package com.alexthw.sauce.common.block;

import com.alexthw.sauce.SauceConfig;
import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static com.alexthw.sauce.common.block.SourceJarFrame.FORMED;

public class DynamicSourceJarTile extends SourceJarTile {
    public DynamicSourceJarTile(BlockPos pos, BlockState state) {
        super(ModRegistry.BIG_SOURCE_JAR.get(), pos, state);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.getMaxSource() > 0) {
            tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getSource() * 100) / this.getMaxSource()));
            if (!ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) return;
            tooltip.add(Component.translatable("sauce.tooltip.buffer", this.getSource(), this.getMaxSource()));
            tooltip.add(Component.translatable("sauce.tooltip.transfer_rate", this.getTransferRate()));
        }
    }

    // Multiblock State
    private boolean isFormed = false;
    private BlockPos minPos = null;
    private BlockPos maxPos = null;

    public BlockPos getMaxPos() {
        return maxPos;
    }

    public BlockPos getMinPos() {
        return minPos;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return ModRegistry.BIG_SOURCE_JAR.get();
    }

    @Override
    protected @NotNull SourceStorage createDefaultStorage() {
        if (isFormed) {
            // Calculate capacity based on the size of the multiblock. Each block adds 10k capacity.
            int blocksCount = (maxPos.getX() - minPos.getX() + 1) * (maxPos.getY() - minPos.getY() + 1) * (maxPos.getZ() - minPos.getZ() + 1);

            return new SourceStorage(SauceConfig.Server.SOURCE_CAPACITY_PER_FRAME.get() * blocksCount, 10000) {
                @Override
                public void onContentsChanged() {
                    DynamicSourceJarTile.this.updateBlock();
                }
            };
        }
        return new SourceStorage(0, 0) {
            @Override
            public void onContentsChanged() {
                DynamicSourceJarTile.this.updateBlock();
            }
        };
    }

    /**
     * Trigger this method when the controller is placed, right-clicked,
     * or when a frame block tells the controller it was added/broken.
     */
    public void validateMultiblock() {
        if (level == null || level.isClientSide) return;

        BlockPos start = this.worldPosition;
        Set<BlockPos> frames = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        frames.add(start);

        // 1. Set up the initial flood-fill queue
        for (Direction dir : Direction.values()) {
            BlockPos offset = start.relative(dir);
            if (isFrame(offset)) {
                queue.add(offset);
                frames.add(offset);
            }
        }

        // Track bounding box extremes
        int minX = start.getX(), minY = start.getY(), minZ = start.getZ();
        int maxX = start.getX(), maxY = start.getY(), maxZ = start.getZ();

        // 2. Flood-Fill all connected frames
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            // Update bounds
            minX = Math.min(minX, current.getX());
            minY = Math.min(minY, current.getY());
            minZ = Math.min(minZ, current.getZ());
            maxX = Math.max(maxX, current.getX());
            maxY = Math.max(maxY, current.getY());
            maxZ = Math.max(maxZ, current.getZ());

            // Fast-fail: if the bounding box exceeds 9x9x9, it's invalid
            if (maxX - minX + 1 > 9 || maxY - minY + 1 > 9 || maxZ - minZ + 1 > 9) {
                setFormed(false);
                return;
            }

            // Check neighbors to continue flood fill
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (!frames.contains(neighbor) && isFrame(neighbor)) {
                    frames.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // 3. Fast-fail: Check minimum size (3x3x3)
        if (maxX - minX + 1 < 3 || maxY - minY + 1 < 3 || maxZ - minZ + 1 < 3) {
            setFormed(false);
            return;
        }

        // 4. Verify the solid box shape
        // Calculate the total volume of the bounding box
        int expectedVolume = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);

        // Fast-fail: The total number of blocks we found MUST equal the volume.
        // (frames.size() represents all contiguous frame blocks, +1 for the controller itself)
        if (frames.size() != expectedVolume) {
            setFormed(false);
            return; // The shape is irregular or missing blocks
        }

        // Deep verification: Ensure every single position in the bounds is valid
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    // If a block inside the bounds is NOT a connected frame
                    // and is NOT the controller, the structure is invalid.
                    if (!frames.contains(pos)) {
                        setFormed(false);
                        return; // Found dirt, air, or a disconnected block inside the box
                    }
                }
            }
        }

        // 5. If we survived all checks, the multiblock is valid!
        this.minPos = new BlockPos(minX, minY, minZ);
        this.maxPos = new BlockPos(maxX, maxY, maxZ);
        setFormed(true);
    }

    private boolean isFrame(BlockPos pos) {
        if (level == null) return false;
        Block block = level.getBlockState(pos).getBlock();
        return block instanceof SourceJarFrame;
    }

    private boolean isValve(BlockPos pos) {
        if (level == null) return false;
        // Replace with your actual frame block reference
        Block block = level.getBlockState(pos).getBlock();
        return block == ModRegistry.SOURCE_JAR_VALVE.get();
    }

    public void setFormed(boolean formed) {
        if (this.isFormed != formed) {
            this.isFormed = formed;
            if (level == null) return;
            // Update the source storage capacity based on the new formed state
            if (formed) {
                int blocksCount = (maxPos.getX() - minPos.getX() + 1) * (maxPos.getY() - minPos.getY() + 1) * (maxPos.getZ() - minPos.getZ() + 1);
                this.getSourceStorage().setMaxSource(SauceConfig.Server.SOURCE_CAPACITY_PER_FRAME.get() * blocksCount);
                this.getSourceStorage().setMaxExtract(10000);
                this.getSourceStorage().setMaxReceive(10000);
            } else {
                this.getSourceStorage().setMaxSource(0);
                this.getSourceStorage().setMaxExtract(0);
                this.getSourceStorage().setMaxReceive(0);
            }
            level.invalidateCapabilities(worldPosition);

            // Optional: Update the block state of frames to formed
            if (minPos != null && maxPos != null)
                BlockPos.betweenClosed(minPos, maxPos).forEach(pos -> {
                    if (isFrame(pos)) {
                        BlockState state = level.getBlockState(pos);
                        if (isValve(pos) && level.getBlockEntity(pos) instanceof DynamicSourceJarTileValve valveTile) {
                            valveTile.corePos = formed ? this.worldPosition : null;
                        }
                        level.setBlock(pos, state.setValue(FORMED, formed), 3);
                    }
                });

            // Sync to clients
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState().trySetValue(FORMED, formed), 3);

            setChanged(); // Marks the chunk as needing a save

        }
    }

    public boolean getIsFormed() {
        return this.isFormed;
    }

    // --- 1.21.1 NBT Serialization (Requires HolderLookup.Provider) ---

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("IsFormed", this.isFormed);

        if (this.isFormed && this.minPos != null && this.maxPos != null) {
            tag.putLong("MinPos", this.minPos.asLong());
            tag.putLong("MaxPos", this.maxPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        boolean isFormed = tag.getBoolean("IsFormed");
        if (isFormed != this.isFormed && tag.contains("MinPos") && tag.contains("MaxPos")) {
            this.minPos = BlockPos.of(tag.getLong("MinPos"));
            this.maxPos = BlockPos.of(tag.getLong("MaxPos"));
            setFormed(true);
        }
        this.isFormed = isFormed;
        super.loadAdditional(tag, registries);
    }

    // --- Client Syncing ---

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
