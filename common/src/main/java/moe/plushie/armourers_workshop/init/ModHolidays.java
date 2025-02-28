package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.core.holiday.ChristmasSeasonHandler;
import moe.plushie.armourers_workshop.core.holiday.HalloweenSeasonHandler;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.holiday.HolidayTracker;
import moe.plushie.armourers_workshop.core.holiday.ValentinesHandler;
import moe.plushie.armourers_workshop.core.item.GiftSackItem;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModHolidays {

    private static final ArrayList<Holiday> HOLIDAY_LIST = new ArrayList<>();

    // Spooky scary skeletons.
    public static final Holiday HALLOWEEN = register("halloween", 31, Calendar.OCTOBER, 1, 0, null);
    public static final Holiday HALLOWEEN_SEASON = register("halloween-season", 24, Calendar.OCTOBER, 8, 0, HalloweenSeasonHandler::new);

    // Some guy was born or something.
    public static final Holiday CHRISTMAS = register("christmas", 25, Calendar.DECEMBER, 0, 24, null);
    public static final Holiday CHRISTMAS_SEASON = register("christmas-season", 1, Calendar.DECEMBER, 31, 0, ChristmasSeasonHandler::new);

    // Forever alone.
    public static final Holiday VALENTINES = register("valentines", 14, Calendar.FEBRUARY, 1, 0, ValentinesHandler::new);

    // year++
    public static final Holiday NEW_YEARS = register("new-years", 1, Calendar.JANUARY, 1, 0, null);

    // The best holiday!
    public static final Holiday PONYTAIL_DAY = register("ponytail-day", 7, Calendar.JULY, 1, 0, null);

    // Should be 12 but making it 24 so more people can see it.
    public static final Holiday APRIL_FOOLS = register("april-fools", 1, Calendar.APRIL, 1, 0, null);

    public static void init() {
    }

    public static Collection<Holiday> getHolidays() {
        return HOLIDAY_LIST;
    }

    public static Collection<Holiday> getActiveHolidays() {
        return Collections.filter(HOLIDAY_LIST, Holiday::isHolidayActive);
    }

    @Nullable
    public static Holiday byName(String name) {
        for (var holiday : HOLIDAY_LIST) {
            if (holiday.getName().equals(name)) {
                return holiday;
            }
        }
        return null;
    }

    public static void welcome(Player player) {
        var server = player.getServer();
        if (server == null || ModConfig.Common.disableAllHolidayEvents) {
            return;
        }
        for (var holiday1 : getActiveHolidays()) {
            if (holiday1.getHandler() == null) {
                continue;
            }
            var storage = HolidayTracker.of(server);
            if (storage.has(player, holiday1)) {
                continue; // the gift is already give to player in this year.
            }
            var itemStack = GiftSackItem.of(holiday1);
            if (player.getInventory().add(itemStack)) {
                storage.add(player, holiday1);
            } else {
                player.sendSystemMessage(Component.translatable("chat.armourers_workshop.inventoryGiftFail"));
            }
        }
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Creates and new holiday that spans x number of hours.
     *
     * @param name          Name of the holiday.
     * @param dayOfMonth    The day of the month this holiday takes place on.
     * @param month         Month this holiday takes place on. Zero based 0 = Jan, 11 = Dec
     * @param lengthInDays  Number of days this holiday lasts.
     * @param lengthInHours Number of hours this holiday lasts.
     */
    private static Holiday register(String name, int dayOfMonth, int month, int lengthInDays, int lengthInHours, @Nullable Supplier<Holiday.IHandler> builder) {
        var startDate = Calendar.getInstance();
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MONTH, month);
        startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        var endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DAY_OF_YEAR, lengthInDays);
        endDate.add(Calendar.HOUR_OF_DAY, lengthInHours);
        var holiday = new Holiday(name, startDate, endDate);
        if (builder != null) {
            holiday.setHandler(builder.get());
        }
        ModLog.debug("Registering Holiday '{}:{}'", ModConstants.MOD_ID, name);
        HOLIDAY_LIST.add(holiday);
        return holiday;
    }
}
