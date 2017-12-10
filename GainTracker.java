package scripts.wastedbro.api.tracking;

import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Skills.SKILLS;
import scripts.wastedbro.api.banking.Banking;

import javax.sound.midi.Track;
import java.util.HashMap;

/**
 * @author Wastedbro
 */
public class GainTracker implements InventoryListener
{
    private static GainTracker tracker = null;

    private InventoryObserver inventoryObserver;

    private HashMap<Integer, Integer> itemsGained;

    private long startTime = -1;
    private boolean trackAllItems = false;

    private HashMap<SKILLS, Integer> startingStats;
    private HashMap<SKILLS, Integer> startingStatsXp;


    private GainTracker()
    {
        inventoryObserver = new InventoryObserver(new Condition()
        {
            @Override
            public boolean active()
            {
                return !Banking.isBankScreenOpen();
            }
        });
        inventoryObserver.addListener(this);
        inventoryObserver.start();

        itemsGained = new HashMap<>();
        startingStats = new HashMap<>();
        startingStatsXp = new HashMap<>();

        startTime = Timing.currentTimeMillis();

        // Register starting stats
        for(SKILLS skill : SKILLS.values())
        {
            startingStats.put(skill, skill.getActualLevel());
            startingStatsXp.put(skill, skill.getXP());
        }
    }

    public static GainTracker instance()
    {
        if(tracker == null)
            tracker = new GainTracker();
        return tracker;
    }

    @Override
    public void inventoryItemGained(int id, int count)
    {
        if(trackAllItems || itemsGained.containsKey(id))
            itemsGained.put(id, itemsGained.get(id)+count);
    }

    @Override
    public void inventoryItemLost(int id, int count)
    {
        if(trackAllItems || itemsGained.containsKey(id))
            itemsGained.put(id, itemsGained.get(id)-count);
    }

    public void stop()
    {
        inventoryObserver.setShouldStop(true);
    }

    public int getLevelsGained(SKILLS skill)
    {
        return skill.getActualLevel() - startingStats.get(skill);
    }
    public int getXpGained(SKILLS skill)
    {
        return skill.getXP() - startingStatsXp.get(skill);
    }

    public void setTrackAllItems(boolean shouldTrackAllItems)
    {
        this.trackAllItems = shouldTrackAllItems;
    }

    public void registerItemTrackers(int... itemIds)
    {
        for(int id : itemIds)
            itemsGained.put(id, 0);
    }


    public int getItemsGained(int... itemIds)
    {
        int gained = 0;

        for (int id : itemIds)
            if(itemsGained.containsKey(id))
                gained += itemsGained.get(id);

        return gained;
    }
}
