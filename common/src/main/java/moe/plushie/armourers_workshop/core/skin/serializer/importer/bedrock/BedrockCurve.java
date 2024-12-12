package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.utils.MolangExpression;

import java.util.List;

public class BedrockCurve {

    private final String type;

    private final MolangExpression input;
    private final MolangExpression range;

    private final List<Float> parameters;

    public BedrockCurve(String type, MolangExpression input, MolangExpression range, List<Float> parameters) {
        this.type = type;
        this.input = input;
        this.range = range;
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public MolangExpression getInput() {
        return input;
    }


    public MolangExpression getRange() {
        return range;
    }

    public List<Float> getParameters() {
        return parameters;
    }

    protected static class Builder {

        private String type = "linear"; // catmull_rom, linear, bezier, bezier_chain

        private MolangExpression input;
        private MolangExpression range;
        private List<Float> parameters = null;

        public void type(String type) {
            this.type = type;
        }

        public void input(MolangExpression input) {
            this.input = input;
        }

        public void range(MolangExpression range) {
            this.range = range;
        }

        public void parameters(List<Float> parameters) {
            this.parameters = parameters;
        }

        public BedrockCurve build() {
            return new BedrockCurve(type, input, range, parameters);
        }

    }
}
