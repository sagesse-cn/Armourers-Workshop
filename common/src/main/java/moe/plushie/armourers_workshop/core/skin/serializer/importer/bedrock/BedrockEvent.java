package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.utils.MolangExpression;

public class BedrockEvent {

    private final MolangExpression expression;
    private final String soundId;
    private final String particleId;
    private final String particleType;
    private final MolangExpression particlePreExpression;

    public BedrockEvent(MolangExpression expression, String soundId, String particleId, String particleType, MolangExpression particlePreExpression) {
        this.expression = expression;
        this.soundId = soundId;
        this.particleId = particleId;
        this.particleType = particleType;
        this.particlePreExpression = particlePreExpression;
    }

    public MolangExpression getExpression() {
        return expression;
    }

    public String getSoundId() {
        return soundId;
    }

    public String getParticleId() {
        return particleId;
    }

    public String getParticleType() {
        return particleType;
    }

    public MolangExpression getParticlePreExpression() {
        return particlePreExpression;
    }

    protected static class Builder {

        private MolangExpression expression;
        private String soundId;
        private String particleId;
        private String particleType;
        private MolangExpression particlePreExpression;

        public void expression(MolangExpression expression) {
            this.expression = expression;
        }

        public void sound(String soundId) {
            this.soundId = soundId;
        }

        public void particle(String particleId, String type, MolangExpression pre) {
            this.particleId = particleId;
            this.particleType = type;
            this.particlePreExpression = pre;
            // emitter/emitter_bound/particle/particle_with_velocity
        }

        public BedrockEvent build() {
            return new BedrockEvent(expression, soundId, particleId, particleType, particlePreExpression);
        }
    }
}
