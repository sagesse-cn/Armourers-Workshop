package moe.plushie.armourers_workshop.api.core;

public interface IDataSerializable {

    interface Immutable extends IDataSerializable {

        void serialize(IDataSerializer serializer);
    }

    interface Mutable extends Immutable {

        void deserialize(IDataSerializer serializer);
    }
}
