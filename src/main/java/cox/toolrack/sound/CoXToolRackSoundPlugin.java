/*BSD 2-Clause License
 * Copyright (c) 2024, pollyrobin

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:

 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.

 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cox.toolrack.sound;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import static net.runelite.api.SoundEffectID.ITEM_PICKUP;

@Slf4j
@PluginDescriptor(
        name = "CoX Tool Rack Sound",
        description = "Have sound feedback when you grab a tool from the tool rack in cox",
        tags = {"cox","toolrack"}
)
public class CoXToolRackSoundPlugin extends Plugin
{
    @Inject
    private Client client;

    @Override
    protected void startUp() throws Exception
    {
    }

    @Override
    protected void shutDown() throws Exception
    {
    }

    private int menuOptionClickedId;
    private int inventoryToolAmount;
    private int toolId;

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.getId() == 29771)
        {
            menuOptionClickedId = event.getId();
            ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
            if (inventory != null)
            {
                switch (event.getMenuAction())
                {
                    case GAME_OBJECT_SECOND_OPTION:
                        //get rakes in inventory
                        toolId = 5341;
                        inventoryToolAmount = getToolAmount(toolId, inventory);
                        break;
                    case GAME_OBJECT_THIRD_OPTION:
                        //get spades in inventory
                        toolId = 952;
                        inventoryToolAmount = getToolAmount(toolId, inventory);
                        break;
                    case GAME_OBJECT_FOURTH_OPTION:
                        //get rakes in inventory
                        toolId = 5343;
                        inventoryToolAmount = getToolAmount(toolId, inventory);
                        break;
                }
            }
        } else
        {
            menuOptionClickedId = 0;
            inventoryToolAmount = 0;
            toolId = 0;
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getContainerId() == InventoryID.INVENTORY.getId() && toolId != 0)
        {

            if (inventoryToolAmount < getToolAmount(toolId, event.getItemContainer()))
            {
                int volume = client.getPreferences().getSoundEffectVolume();
                client.playSoundEffect(ITEM_PICKUP, volume);

                menuOptionClickedId = 0;
                inventoryToolAmount = 0;
                toolId = 0;
            }
        }
    }

    private int getToolAmount(int toolId, ItemContainer itemContainer)
    {
        int amount = 0;
        if (itemContainer.contains(toolId))
        {
            for (Item item : itemContainer.getItems())
            {
                if (item.getId() == toolId)
                {
                    amount++;
                }
            }
        }
        return amount;
    }
}
