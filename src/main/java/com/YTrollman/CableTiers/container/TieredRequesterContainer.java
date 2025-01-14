package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.blockentity.TieredRequesterBlockEntity;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import net.minecraft.world.entity.player.Player;

public class TieredRequesterContainer extends TieredContainerMenu<TieredRequesterBlockEntity, TieredRequesterNetworkNode> {
    public TieredRequesterContainer(int windowId, Player player, TieredRequesterBlockEntity tile) {
        super(ContentType.REQUESTER, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());
        addPlayerInventory(8, 55 + (18 * (getTier().getSlotsMultiplier() - 1)));

        transferManager.addFilterTransfer(getPlayer().getInventory(), getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
