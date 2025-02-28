package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ShaderPreprocessor {

    public static final List<String> PATCHED_VANILLA_SHADERS = Collections.immutableList(builder -> {
        builder.add("rendertype_entity_solid");
        builder.add("rendertype_entity_shadow");
        builder.add("rendertype_entity_cutout");
        builder.add("rendertype_energy_swirl");
        builder.add("rendertype_outline");
    });

    private final String prefix;

    public ShaderPreprocessor(String prefix) {
        this.prefix = prefix;
    }

    public String process(String source) {
        return switch (prefix) {
            case "va" -> processOptifineShader(new Builder(source));
            case "iris_" -> processIrisShader(new Builder(source));
            case "frx_" -> processCanvasShader(new Builder(source));
            case "" -> processVanillaShader(new Builder(source));
            default -> source;
        };
    }

    private String processIrisShader(Builder builder) {
        builder.attribute("aw_UV0", "vec2", "iris_UV0", "mat4", "aw_TextureMatrix", "vec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_UV1", "ivec2", "iris_UV1", "mat4", "aw_OverlayTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_UV2", "ivec2", "iris_UV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_Color", "vec4", "iris_Color", "vec4", "aw_ColorModulator", "($2 * $1)");
        builder.attribute("aw_Normal", "vec3", "iris_Normal", "mat3", "aw_NormalMatrix", "($2 * $1)");
        builder.attribute("aw_Position", "vec3", "iris_Position", "mat4", "aw_ModelViewMat", "vec3($2 * vec4($1, 1))");
        return build("iris", builder);
    }

    private String processOptifineShader(Builder builder) {
        builder.attribute("aw_UV0", "vec2", "vaUV0", "mat4", "aw_TextureMatrix", "vec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_UV1", "ivec2", "vaUV1", "mat4", "aw_OverlayTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_UV2", "ivec2", "vaUV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_Color", "vec4", "vaColor", "vec4", "aw_ColorModulator", "($2 * $1)");
        builder.attribute("aw_Normal", "vec3", "vaNormal", "mat3", "aw_NormalMatrix", "($2 * $1)");
        builder.attribute("aw_Position", "vec3", "vaPosition", "mat4", "aw_ModelViewMat", "vec3($2 * vec4($1, 1))");
        return build("optifine", builder);
    }

    private String processCanvasShader(Builder builder) {
//        builder.attribute("aw_UV0", "vec2", "iris_UV0", "mat4", "aw_TextureMatrix", "vec2($2 * vec4($1, 1, 1))");
//        builder.attribute("aw_UV1", "ivec2", "iris_UV1", "mat4", "aw_OverlayTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
//        builder.attribute("aw_UV2", "ivec2", "iris_UV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
//        builder.attribute("aw_Color", "vec4", "iris_Color", "vec4", "aw_ColorModulator", "($2 * $1)");
//        builder.attribute("aw_Normal", "vec3", "iris_Normal", "mat3", "aw_NormalMatrix", "($2 * $1)");
//        builder.attribute("aw_Position", "vec3", "iris_Position", "mat4", "aw_ModelViewMat", "vec3($2 * vec4($1, 1))");
        return build("canvas", builder);
    }

    private String processVanillaShader(Builder builder) {
        builder.attribute("aw_UV0", "vec2", "UV0", "mat4", "aw_TextureMatrix", "vec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_UV1", "ivec2", "UV1", "mat4", "aw_OverlayTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_UV2", "ivec2", "UV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 1, 1))");
        builder.attribute("aw_Color", "vec4", "Color", "vec4", "aw_ColorModulator", "($2 * $1)");
        builder.attribute("aw_Normal", "vec3", "Normal", "mat3", "aw_NormalMatrix", "($2 * $1)");
        builder.attribute("aw_Position", "vec3", "Position", "mat4", "aw_ModelViewMat", "vec3($2 * vec4($1, 1))");
        return build("vanilla", builder);
    }

    private void processCommonShader(Builder builder) {
        // if normal exists, we need normalize it when flags is 0x2(non-uniform scaled) enabled.
        if (builder.variables.contains("aw_Normal")) {
            builder.scripts.add("if ((aw_MatrixFlags & 0x02) != 0) {");
            builder.scripts.add("  aw_Normal = normalize(aw_Normal);");
            builder.scripts.add("}");
        }
    }

    private String build(String type, Builder builder) {
        processCommonShader(builder);
        var source = builder.build();
        if (ModConfig.Client.enableShaderDebug) {
            ModLog.info("process {} shader: \n{}", type, source);
        }
        return source;
    }

    public static class Builder {

        private String source;

        private final ArrayList<String> variables = new ArrayList<>();
        private final ArrayList<String> scripts = new ArrayList<>();

        private final ArrayList<String> initializer1 = new ArrayList<>();
        private final ArrayList<String> initializer2 = new ArrayList<>();

        public Builder(String source) {
            this.source = source;
        }

        public Builder uniform(String name, String varType, String var, String matrixType, String matrixVar, String expr) {
            source = register("uniform", name, source, varType, var, matrixType, matrixVar, expr);
            return this;
        }

        public Builder attribute(String name, String varType, String var, String matrixType, String matrixVar, String expr) {
            source = register("in", name, source, varType, var, matrixType, matrixVar, expr);
            return this;
        }

        private String register(String category, String name, String source, String varType, String var, String matrixType, String matrix, String expr) {
            // compile regular expressions.
            String[] texts = {
                    "(${category}\\s+${varType}\\s+)(\\b${var}\\b)(.*?;)", "$1__aw_${var}_aw__$3",
                    "\\b${var}\\b", "${name}",
                    "(${category}\\s+${varType}\\s+)(\\b__aw_${var}_aw__)\\b(.*?;)", "uniform ${matrixType} ${matrix};\n${varType} ${name};\n$1${var}$3",
            };
            String[] regexes = new String[texts.length];
            for (int i = 0; i < texts.length; ++i) {
                String tmp = texts[i];
                tmp = tmp.replace("${category}", category);
                tmp = tmp.replace("${name}", name);
                tmp = tmp.replace("${varType}", varType);
                tmp = tmp.replace("${var}", var);
                tmp = tmp.replace("${matrixType}", matrixType);
                tmp = tmp.replace("${matrix}", matrix);
                regexes[i] = tmp;
            }
            // we need to replace all the content correctly.
            for (int i = 0; i < regexes.length / 2; ++i) {
                String newValue = source.replaceAll(regexes[i * 2], regexes[i * 2 + 1]);
                if (i == 0 && newValue.equals(source)) {
                    // sorry, we not found the input var.
                    return source;
                }
                source = newValue;
            }
            // the initializer must be relocation.
            variables.add(name);
            variables.add(matrix);
            initializer1.add(name + " = " + var);
            initializer2.add(name + " = " + expr.replace("$1", var).replace("$2", matrix));
            return source;
        }

        public String build() {
            // when not any changes, ignore.
            if (initializer1.isEmpty()) {
                return source;
            }
            // NOTE: we can't support "type x = y;" in the global;
            var builder = new SourceBuilder();
            builder.append("#ifdef GL_ES\n");
            builder.append("uniform int aw_MatrixFlags;\n");
            builder.append("#else\n");
            builder.append("uniform int aw_MatrixFlags = 0;\n");
            builder.append("#endif\n");
            builder.append("void aw_main_pre() {\n");
            builder.append("  if ((aw_MatrixFlags & 0x01) != 0) {\n");
            builder.append("    ", initializer2, ";\n");
            builder.append("    ", scripts, "\n");
            builder.append("  } else {\n");
            builder.append("    ", initializer1, ";\n");
            builder.append("  }\n");
            builder.append("}\n");
            return source.replaceAll("(void\\s+main\\s*\\(\\)\\s*\\{)(\\s*)", builder.build() + "\n$1$2aw_main_pre();$2$2");
        }

        @Override
        public String toString() {
            return build();
        }
    }

    public static class SourceBuilder {

        private final StringBuffer buffer = new StringBuffer();

        public void append(String value) {
            buffer.append(value);
        }

        public void append(String prefix, Collection<String> children, String suffix) {
            for (var content : children) {
                buffer.append(prefix);
                buffer.append(content);
                buffer.append(suffix);
            }
        }

        public String build() {
            return buffer.toString();
        }
    }
}
