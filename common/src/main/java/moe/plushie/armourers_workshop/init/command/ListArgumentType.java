package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ListArgumentType implements ArgumentType<String> {

    private final Collection<String> list;

    public ListArgumentType(Collection<String> list) {
        super();
        this.list = list;
    }

    public static ListArgumentType list(Iterable<String> values) {
        return new ListArgumentType(Collections.newList(values));
    }

    public static String getString(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        final String text = reader.getRemaining();
        for (String value : list) {
            if (text.startsWith(value)) {
                reader.setCursor(reader.getCursor() + value.length());
                return value;
            }
        }
        return text;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(list, builder);
    }

    public static class Serializer implements IArgumentSerializer<ListArgumentType> {

        @Override
        public void serializeToNetwork(ListArgumentType argument, IFriendlyByteBuf buffer) {
            ArrayList<String> lists = new ArrayList<>(argument.list);
            buffer.writeInt(lists.size());
            lists.forEach(buffer::writeUtf);
        }

        @Override
        public ListArgumentType deserializeFromNetwork(IFriendlyByteBuf buffer) {
            int size = buffer.readInt();
            ArrayList<String> lists = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                lists.add(buffer.readUtf());
            }
            return new ListArgumentType(lists);
        }

        @Override
        public void serializeToJson(ListArgumentType argument, JsonObject json) {
            JsonArray array = new JsonArray();
            argument.list.forEach(array::add);
            json.add("items", array);
        }
    }
}
