package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

public class InfusionVatConnectivityHandler {
    // TODO: custom connecting for infusion vat
    public static void tryFormMultiBlock(InfusionVatBlockEntity be){
        ControllerSearchCache cache = new ControllerSearchCache();
        List<InfusionVatBlockEntity> pending = new ArrayList<>();
        pending.add(be);
        tryFormMultiBlock(be.getType(), be.getLevel(), cache, pending);
    }

    private static void tryFormMultiBlock(BlockEntityType<?> beType, BlockGetter level, ControllerSearchCache cache, List<InfusionVatBlockEntity> pending){
        PriorityQueue<Pair<Integer, InfusionVatBlockEntity>> creationQueue = new PriorityQueue<>((a, b) -> b.getKey() - a.getKey());
        Set<BlockPos> visited = new HashSet<>();

        int searchMinX = Integer.MAX_VALUE;
        int searchMinZ = Integer.MAX_VALUE;
        for (InfusionVatBlockEntity blockEntity : pending) {
            BlockPos pos = blockEntity.getBlockPos();
            searchMinX = Math.min(searchMinX, pos.getX());
            searchMinZ = Math.min(searchMinZ, pos.getZ());
        }
        searchMinX -= pending.getFirst().getMaxWidth();
        searchMinZ -= pending.getFirst().getMaxWidth();

        while(!pending.isEmpty()){
            var part = pending.removeFirst();
            var pos = part.getBlockPos();
            if(visited.contains(pos)) continue;
            visited.add(pos);
            int amount = tryToFormNewMultiblock(part, cache, true);
            if(amount > 1){
                creationQueue.add(Pair.of(amount, part));
            }
            // Searching backwards (on negative axes)
            for (Direction.Axis axis : Iterate.axes) {
                Direction dir = Direction.get(Direction.AxisDirection.NEGATIVE, axis);
                BlockPos next = pos.relative(dir);
                if(next.getX() <= searchMinX || next.getZ() <= searchMinZ) continue;
                if(visited.contains(next)) continue;
                var nextBe = partAt(beType, level, next);
                if (nextBe == null) continue;
                if(nextBe.isRemoved()) continue;
                pending.add(nextBe);
            }
        }
        visited.clear();
        while (!creationQueue.isEmpty()){
            var next = creationQueue.poll();
            var nextBe = next.getValue();
            if(visited.contains(nextBe.getBlockPos()))
                continue;
            visited.add(nextBe.getBlockPos());
            tryToFormNewMultiblock(nextBe, cache, false);
        }
    }

    private record AreaInfo(int xLen, int zLen){
        int area(){
            return xLen * zLen;
        }
    }

    private static List<AreaInfo> iterFromMaxBaseArea(int maxXLength, int maxZLength){
        return IntStream.rangeClosed(1, maxXLength)
                .mapToObj(x -> IntStream.rangeClosed(1, maxZLength).mapToObj(z -> new AreaInfo(x, z)))
                .flatMap(s -> s).sorted(Comparator.comparingInt(AreaInfo::area).thenComparingInt(AreaInfo::xLen).reversed())
                .toList();
    }

    private static int tryToFormNewMultiblock(InfusionVatBlockEntity be, ControllerSearchCache cache, boolean simulate){
        int bestXLength = 1;
        int bestZLength = 1;
        int bestAmount = -1;
        if(!be.isController()) return 0;

        int maxXLength = be.getMaxWidth();
        int maxZLength = be.getMaxWidth();
        for (AreaInfo areaInfo : iterFromMaxBaseArea(maxXLength, maxZLength)) {
            int amount = tryToFormNewMultiblockBy(be, areaInfo.xLen, areaInfo.zLen, cache, true);
            if(amount < bestAmount) continue;
            bestXLength = areaInfo.xLen;
            bestZLength = areaInfo.zLen;
            bestAmount = amount;
        }

        if(!simulate){
            int beXLen = be.getXLength();
            int beZLen = be.getZLength();
            if(beXLen == bestXLength && beZLen == bestZLength && beXLen * beZLen * be.getHeight() == bestAmount)
                return bestAmount; // already in best config
            splitMultiAndInvalidate(be, cache, false);
            tryToFormNewMultiblockBy(be, bestXLength, bestZLength, cache, false);
            // inner function will not update this block entity. do it here
            be.preventConnectivityUpdate();
            be.setXLength(bestXLength);
            be.setZLength(bestZLength);
            be.setHeight(bestAmount / bestXLength / bestZLength);
            be.notifyMultiUpdated();
        }
        return bestAmount;
    }

    private static int tryToFormNewMultiblockBy(InfusionVatBlockEntity be, int xLen, int zLen, ControllerSearchCache cache, boolean simulate){
        int amount = 0;
        int height = 0;
        var beType = be.getType();
        Level level = be.getLevel();
        if(level == null) return 0;

        BlockPos origin = be.getBlockPos();

        SearchLoop:
        for (int yOffset = 0; yOffset < be.getMaxHeight(); yOffset++) {
            for (int xOffset = 0; xOffset < xLen; xOffset++) {
                for (int zOffset = 0; zOffset < zLen; zOffset++) {
                    BlockPos currentPos = origin.offset(xOffset, yOffset, zOffset);
                    var maybeResponsibleController = cache.getOrCache(beType, level, currentPos);
                    if(maybeResponsibleController.isEmpty()) break SearchLoop; // currentPos is not target block entity
                    var responsibleController = maybeResponsibleController.get();
                    int otherXLength = responsibleController.getXLength();
                    int otherZLength = responsibleController.getZLength();
                    int otherHeight = responsibleController.getHeight();
                    if(otherXLength > xLen) break SearchLoop; // Will form multi block that exceed the target width
                    if(otherZLength > zLen) break SearchLoop; // Will form multi block that exceed the target depth
                    if(otherXLength == xLen && otherZLength == zLen && otherHeight == be.getMaxHeight()) break SearchLoop; // Will form multi block that exceed the max height
                    BlockPos controllerPos = responsibleController.getBlockPos();
                    if(!controllerPos.equals(origin)){
                        if(controllerPos.getX() < origin.getX()) break SearchLoop; // controller out of bound
                        if(controllerPos.getZ() < origin.getZ()) break SearchLoop;
                        if(controllerPos.getX() + otherXLength > origin.getX() + xLen) break SearchLoop;
                        if(controllerPos.getZ() + otherZLength > origin.getZ() + zLen) break SearchLoop;
                    }
                }
            }
            // this plane is good. form this
            amount += xLen * zLen;
            height++;
        }
        if(simulate) return amount;

        // start forming multi block
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < xLen; xOffset++) {
                for (int zOffset = 0; zOffset < zLen; zOffset++) {
                    BlockPos currentPos = origin.offset(xOffset, yOffset, zOffset);
                    var part = partAt(beType, level, currentPos);
                    if(part == null) continue; // this should not be triggered, but play safe
                    if(part == be) continue;

                    splitMultiAndInvalidate(part, cache, false);
                    part.setController(origin);
                    part.preventConnectivityUpdate();
                    cache.put(currentPos, be);
                    part.setHeight(height);
                    part.setXLength(xLen);
                    part.setZLength(zLen);
                    part.notifyMultiUpdated();
                }
            }
        }
        be.notifyMultiUpdated();
        return amount;
    }

    public static void splitMultiblock(InfusionVatBlockEntity be){
        splitMultiAndInvalidate(be, null, false);
    }

    private static void splitMultiAndInvalidate(InfusionVatBlockEntity be, @Nullable ControllerSearchCache cache, boolean tryReconnect) {
        Level level = be.getLevel();
        if(level == null) return;
        be = be.getControllerBE();
        if(be == null) return;

        int height = be.getHeight();
        int xLength = be.getXLength();
        int zLength = be.getZLength();
        if(xLength == 1 && zLength == 1 && height == 1) return;

        BlockPos origin = be.getBlockPos();
        List<InfusionVatBlockEntity> pending = new ArrayList<>();
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < xLength; xOffset++) {
                for (int zOffset = 0; zOffset < zLength; zOffset++) {
                    BlockPos pos = origin.offset(xOffset, yOffset, zOffset);
                    var partAt = partAt(be.getType(), level, pos);
                    if(partAt == null) continue;
                    if(!partAt.getController().equals(origin)) continue; // not belong to this block entity

                    partAt.removeController(true);
                    if(tryReconnect){
                        pending.add(partAt);
                        partAt.preventConnectivityUpdate();
                    }
                    if(cache != null){
                        cache.put(pos, partAt);
                    }
                }
            }
        }
        // TODO: invalidate capablilities
        if(tryReconnect){
            tryFormMultiBlock(be.getType(), level, cache == null ? new ControllerSearchCache() : cache, pending);
        }
    }

    public static InfusionVatBlockEntity partAt(BlockEntityType<?> beType, BlockGetter level, BlockPos pos){
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof InfusionVatBlockEntity && be.getType() == beType && !be.isRemoved()) return ((InfusionVatBlockEntity) be);
        return null;
    }

    private static InfusionVatBlockEntity checked(BlockEntity be) {
        if (be instanceof InfusionVatBlockEntity)
            return ((InfusionVatBlockEntity) be);
        return null;
    }

    public static boolean isConnected(BlockGetter level, BlockPos pos, BlockPos other) {
        var one = checked(level.getBlockEntity(pos));
        var two = checked(level.getBlockEntity(other));
        if (one == null || two == null)
            return false;
        return one.getController().equals(two.getController());
    }

    private static class ControllerSearchCache {
        Map<BlockPos, Optional<InfusionVatBlockEntity>> beMap;
        public ControllerSearchCache(){
            beMap = new HashMap<>();
        }

        void putEmpty(BlockPos pos){
            beMap.put(pos, Optional.empty());
        }

        void put(BlockPos pos, InfusionVatBlockEntity be){
            beMap.put(pos, Optional.of(be));
        }

        boolean hasVisited(BlockPos pos){
            return beMap.containsKey(pos);
        }

        Optional<InfusionVatBlockEntity> getOrCache(BlockEntityType<?> beType, BlockGetter level, BlockPos pos){
            if(hasVisited(pos)) return beMap.get(pos);

            InfusionVatBlockEntity partAt = partAt(beType, level, pos);
            if(partAt == null){
                putEmpty(pos);
                return Optional.empty();
            }
            BlockEntity maybeController = level.getBlockEntity(partAt.getController());
            if(maybeController instanceof InfusionVatBlockEntity){
                put(pos, ((InfusionVatBlockEntity) maybeController));
                return Optional.of(((InfusionVatBlockEntity) maybeController));
            }
            putEmpty(pos);
            return Optional.empty();
        }
    }
}
