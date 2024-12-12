package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.builtin.ForEach;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.builtin.Loop;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.builtin.Print;

import java.util.Map;

public class PrimaryBinding extends ContextBinding {

    public PrimaryBinding(Map<String, ObjectBinding> bindings) {
        // User's bindings
        if (bindings != null) {
            bindings.forEach(this::add);
        }

        add("math", new MathBinding());
        add("query", new QueryBinding());

        add("variable", new VariableBinding.Scoped());
        add("context", new VariableBinding.Foreign());
        add("temp", new VariableBinding.Temp());

        alias("query", "q");
        alias("variable", "v");
        alias("context", "c");
        alias("temp", "t");

        // Built-in functions
        function("loop", Loop::new);
        function("for_each", ForEach::new);
        function("print", Print::new);
    }
}
