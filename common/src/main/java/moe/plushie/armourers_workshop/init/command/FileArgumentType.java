package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

// /path/name.armour
public class FileArgumentType implements ArgumentType<String> {

    public static final SimpleCommandExceptionType ERROR_START = new SimpleCommandExceptionType(Component.literal("File must start with '/'"));
    public static final SimpleCommandExceptionType ERROR_NOT_FOUND = new SimpleCommandExceptionType(Component.literal("Not found any file"));
    private static final Collection<String> EXAMPLES = Collections.newList("/", "/file" + Constants.EXT, "\"<scheme>:<identifier>\"");
    private final File rootFile;
    private final ArrayList<String> fileList;
    private final StringArgumentType stringType = StringArgumentType.string();

    private String filteredName;
    private ArrayList<String> filteredFileList;

    public FileArgumentType(File rootPath) {
        super();
        this.rootFile = rootPath;
        this.fileList = null;
        ModLog.debug("setup root path: " + rootPath);
    }

    FileArgumentType(ArrayList<String> fileList) {
        super();
        this.rootFile = null;
        this.fileList = fileList;
    }


    public static String getString(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        var inputPath = reader.getRemaining();
        if (inputPath.startsWith("\"")) {
            return stringType.parse(reader);
        }
        if (inputPath.startsWith("/")) { // file
            var fileList = getFileList(inputPath);
            if (fileList.isEmpty()) {
                throw ERROR_NOT_FOUND.createWithContext(reader);
            }
//            if (fileList.stream().noneMatch(inputPath::equals)) {
//                if (!inputPath.endsWith(Constants.EXT)) {
//                    throw ERROR_NOT_FOUND.createWithContext(reader);
//                }
//            }
            reader.setCursor(reader.getCursor() + inputPath.length());
            return inputPath;
        }
        throw ERROR_START.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var inputPath = builder.getRemaining();
        if (inputPath.startsWith("/")) {
            var fileList = getFileList(inputPath);
            if (!fileList.isEmpty()) {
                return suggestFiles(fileList, inputPath, builder);
            }
        }
        return SharedSuggestionProvider.suggest(Collections.newList("/", "\""), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private CompletableFuture<Suggestions> suggestFiles(ArrayList<String> fileList, String inputPath, SuggestionsBuilder builder) {
        var parent = getParentPath(inputPath);
        builder = builder.createOffset(builder.getStart() + parent.length());
        for (var file : fileList) {
            var name = FileUtils.getRelativePath(file, parent);
            if (!name.isEmpty()) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }

    private ArrayList<String> getFileList(String name) {
        if (Objects.equals(name, filteredName)) {
            return filteredFileList;
        }
        if (rootFile != null) {
            filteredName = name;
            filteredFileList = getFileList(rootFile, name);
        } else {
            filteredName = name;
            filteredFileList = getFileList(fileList, name);
        }
        return filteredFileList;
    }

    public ArrayList<String> getFileList(File parentFile, String name) {
        var results = new ArrayList<String>();
        var files = parentFile.listFiles();
        if (files == null) {
            ModLog.error("load file error at {}", parentFile);
            return results;
        }
        for (var file : files) {
            var rv = FileUtils.getRelativePath(file, rootFile, true);
            if (file.isDirectory()) {
                if (name.startsWith(rv)) {
                    results.addAll(getFileList(file, name));
                } else if (rv.startsWith(name)) {
                    results.addAll(getFileList(file, name));
                }
                continue;
            }
            if (rv.toLowerCase().endsWith(Constants.EXT)) {
                if (rv.startsWith(name)) {
                    results.add(rv);
                } else if (name.startsWith(rv)) {
                    results.add(rv);
                }
            }
        }
        return results;
    }

    public ArrayList<String> getFileList(ArrayList<String> fileList, String name) {
        ArrayList<String> results = new ArrayList<>();
        for (String file : fileList) {
            if (file.startsWith(name)) {
                results.add(file);
            }
        }
        return results;
    }

    private String getParentPath(String file) {
        int index = file.lastIndexOf("/");
        if (index <= 0) {
            return "/";
        }
        return file.substring(0, index) + "/";
    }

    public static class Serializer implements IArgumentSerializer<FileArgumentType> {

        @Override
        public void serializeToNetwork(FileArgumentType argument, IFriendlyByteBuf buffer) {
            var lists = argument.getFileList("/");
            buffer.writeInt(lists.size());
            lists.forEach(buffer::writeUtf);
        }

        @Override
        public FileArgumentType deserializeFromNetwork(IFriendlyByteBuf buffer) {
            int size = buffer.readInt();
            ArrayList<String> lists = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                lists.add(buffer.readUtf());
            }
            return new FileArgumentType(lists);
        }

        @Override
        public void serializeToJson(FileArgumentType argument, JsonObject json) {
            JsonArray array = new JsonArray();
            ArrayList<String> lists = argument.getFileList("/");
            lists.forEach(array::add);
            json.add("files", array);
        }
    }
}
