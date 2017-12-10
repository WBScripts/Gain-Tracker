package scripts.wastedbro.api.tracking;

public interface InventoryListener
{
    void inventoryItemGained(int id, int count);
    void inventoryItemLost(int id, int count);
}