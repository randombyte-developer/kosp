package de.randombyte.kosp.extensions

import org.spongepowered.api.command.CommandException
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult

/**
 * Gives the [Player] the [itemStack] by trying these things:
 * 1. putting it in the hand
 * 2. putting it somewhere in the inventory
 * 3. dropping it onto the ground
 */
fun Player.give(itemStack: ItemStack) {
    val isPlayerHoldingSomething = getItemInHand(HandTypes.MAIN_HAND).isPresent
    if (!isPlayerHoldingSomething) {
        // nothing in hand -> put item in hand
        setItemInHand(HandTypes.MAIN_HAND, itemStack)
    } else {
        // something in hand -> place item somewhere in the main player inventory(not equipment slots)
        val transactionResult = inventory
                .query<Inventory>(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory::class.java))
                .offer(itemStack)
        if (transactionResult.type != InventoryTransactionResult.Type.SUCCESS) {
            // main inventory full -> spawn as item
            val entity = location.extent.createEntity(EntityTypes.ITEM, location.position)
            entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot())
            if (!location.extent.spawnEntity(entity)) {
                throw CommandException("Couldn't spawn Item!".toText())
            }
        }
    }
}